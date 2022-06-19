/*
 * Copyright (c) 2022 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.machinelearning;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.journeyOS.common.SmartLog;
import com.journeyOS.i007manager.AiData;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.machinelearning.tasks.BaseTask;
import com.journeyOS.machinelearning.tasks.ITaskResultHandler;
import com.journeyOS.machinelearning.tasks.TaskManager;
import com.journeyOS.machinelearning.tasks.TaskResult;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ml manager
 */
public final class MachineLearningManager {
    protected static final int MAX_QUEUE_SIZE = 20;
    /**
     * Thread pool define
     */
    protected static final int MAX_THREAD_POOL_SIZE = 1;
    protected static final int THREAD_POOL_SIZE = MAX_THREAD_POOL_SIZE;

    private static final String TAG = MachineLearningManager.class.getSimpleName();
    private static final boolean DEBUG = false;
    private static volatile MachineLearningManager sInstance = null;
    private ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private Handler mHandler = null;
    private HashMap<String, WeakReference<Runnable>> mRunningTasks = new HashMap<>();
    private ConcurrentLinkedQueue<BaseTask> mMLTaskQueue = new ConcurrentLinkedQueue();
    private boolean mInit = false;

    private MachineLearningManager() {
    }

    /**
     * 获取 MachineLearningManager 单例
     *
     * @return MachineLearningManager 实例
     */
    public static MachineLearningManager getInstance() {
        if (sInstance == null) {
            synchronized (MachineLearningManager.class) {
                if (sInstance == null) {
                    sInstance = new MachineLearningManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化 worker
     *
     * @param application 上下文
     */
    public void init(Application application) {
        SmartLog.d(TAG, "start");
        if (mInit) {
            return;
        }
        mInit = true;
        this.mHandler = new MLHandle(Looper.getMainLooper());
        WorkerManager.getInstance().init(application);
    }

    /**
     * 初始化 worker
     *
     * @param aiModel 模型信息
     * @return 是否成功
     */
    public boolean initWorker(AiModel aiModel) {
        return WorkerManager.getInstance().initWorker(aiModel);
    }

    /**
     * 加载模型
     *
     * @param aiModel 模型信息
     * @return 是否成功
     */
    public boolean loadModel(AiModel aiModel) {
        return WorkerManager.getInstance().applyModel(aiModel);
    }

    /**
     * 释放模型
     *
     * @param aiModel 模型信息
     * @return 是否成功
     */
    public boolean releaseModel(AiModel aiModel) {
        return WorkerManager.getInstance().releaseModel(aiModel);
    }

    /**
     * 执行任务
     *
     * @param aiModel  模型信息
     * @param aiData   需要识别的信息
     * @param clientId 客户端id
     * @param handler  Handler
     * @return 任务结果
     */
    public TaskResult executeTask(AiModel aiModel, AiData aiData, int clientId, ITaskResultHandler handler) {
        String workerName = aiModel.getName();
        BaseTask task = TaskManager.get(workerName, clientId, aiData);
        if (task == null) {
            SmartLog.w(TAG, "can not find task for " + workerName);
            return null;
        }
        TaskResult finalResult = null;
        if (handler == null) {
            task.setTaskType(BaseTask.TYPE_SYNC);
            finalResult = task.execute();
        } else {
            if (mMLTaskQueue.size() > MAX_QUEUE_SIZE) {
                return null;
            }
            task.setTaskType(BaseTask.TYPE_ASYNC);
            task.setHandler(handler);
            mMLTaskQueue.offer(task);
            mHandler.sendEmptyMessage(MLHandle.MSG_ADD_TASK);
        }
        return finalResult;
    }

    /**
     * MLHandle
     */
    class MLHandle extends Handler {
        public static final int MSG_ADD_TASK = 1;
        public static final int MSG_REMOVE_TASK = 2;

        MLHandle(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MSG_ADD_TASK) {
                BaseTask task = mMLTaskQueue.poll();
                if (task != null) {
                    if (!needExecute(task.uniqueId())) {
                        SmartLog.w(TAG, "can not add more task");
                        return;
                    }
                    Runnable runnable = task.toRunnable();
                    WeakReference<Runnable> weakReference = new WeakReference<>(runnable);
                    mRunningTasks.put(task.uniqueId(), weakReference);
                    executorService.execute(runnable);
                }
            } else if (msg.what == MSG_REMOVE_TASK) {
                int clientId = msg.arg1;
                Iterator<BaseTask> queueIterator = mMLTaskQueue.iterator();
                while (queueIterator.hasNext()) {
                    BaseTask tTask = queueIterator.next();
                    if (tTask.uniqueId().startsWith(String.valueOf(clientId))) {
                        queueIterator.remove();
                    }
                }
                Iterator<Map.Entry<String, WeakReference<Runnable>>> iterator = mRunningTasks.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, WeakReference<Runnable>> next = iterator.next();
                    WeakReference<Runnable> runnableWeakReference = next.getValue();
                    String uniqueId = next.getKey();
                    if (uniqueId.startsWith(String.valueOf(clientId))
                            && runnableWeakReference != null
                            && runnableWeakReference.get() == null) {
                        executorService.remove(runnableWeakReference.get());
                        iterator.remove();
                    }
                }
            }
            super.handleMessage(msg);
        }

        private boolean needExecute(String task) {
            int runningTaskSize = mRunningTasks.size();
            if (runningTaskSize > MAX_QUEUE_SIZE) {
                SmartLog.d(TAG, "running task is more than set. size = " + runningTaskSize);
                Iterator<Map.Entry<String, WeakReference<Runnable>>> iterator = mRunningTasks.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, WeakReference<Runnable>> next = iterator.next();
                    WeakReference<Runnable> runnableWeakReference = next.getValue();
                    if ((runnableWeakReference != null && runnableWeakReference.get() == null) || (!executorService.getQueue().contains(runnableWeakReference.get()))) {
                        iterator.remove();
                    }
                }
            }
            WeakReference<Runnable> weakReference = mRunningTasks.get(task);
            if (weakReference != null) {
                Runnable tmp = weakReference.get();
                if (tmp != null) {
                    if (executorService.getQueue().contains(tmp)) {
                        SmartLog.d(TAG, task + " is running and same unique id task coming, so remove the old one!!!");
                        executorService.remove(tmp);
                    }
                    mRunningTasks.remove(task);
                }
            }
            int taskCount = executorService.getQueue().size();
            if (taskCount > MAX_QUEUE_SIZE) {
                SmartLog.e(TAG, "running task is too many count = " + taskCount);
                return false;
            } else {
                return true;
            }
        }
    }

}
