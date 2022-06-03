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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.journeyOS.common.SmartLog;
import com.journeyOS.machinelearning.common.MLConstant;
import com.journeyOS.machinelearning.datas.ModelInfo;
import com.journeyOS.machinelearning.tasks.IMLTaskResultHandler;
import com.journeyOS.machinelearning.tasks.AbstractMLTask;
import com.journeyOS.machinelearning.tasks.MLTaskFactory;
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
    private ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private Handler mHandler = null;
    private HashMap<String, WeakReference<Runnable>> mRunningTasks = new HashMap<>();
    private ConcurrentLinkedQueue<AbstractMLTask> mMLTaskQueue = new ConcurrentLinkedQueue();
    private boolean mInit = false;

    private MachineLearningManager() {
    }

    /**
     * get MachineLearningManager
     *
     * @return MachineLearningManager
     */
    public static MachineLearningManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * init worker
     *
     * @param application Application
     */
    public void init(Application application) {
        SmartLog.d(TAG, "start");
        if (mInit) {
            return;
        }
        mInit = true;
        this.mHandler = new MLHandle(Looper.getMainLooper());
        WorkerFactory.getInstance().init(application);
    }

    /**
     * init worker
     *
     * @param modelInfo model info
     */
    public void initWorker(ModelInfo modelInfo) {
        WorkerFactory.getInstance().initWorker(modelInfo);
    }

    /**
     * load model
     *
     * @param modelInfo model info
     */
    public void loadModel(ModelInfo modelInfo) {
        WorkerFactory.getInstance().applyModel(modelInfo);
    }

    /**
     * release model
     *
     * @param modelInfo model info
     */
    public void releaseModel(ModelInfo modelInfo) {
        WorkerFactory.getInstance().releaseModel(modelInfo);
    }

    /**
     * recognize
     *
     * @param modelInfo model info
     * @param clientId  client id
     * @param channel   channel
     * @param data      data
     * @param handler   handler
     * @return TaskResult
     */
    public TaskResult recognize(ModelInfo modelInfo, int clientId, int channel, String data, IMLTaskResultHandler handler) {
        Bundle bundle = new Bundle();
        bundle.putString(MLConstant.ML_TASK_BUNDLE_KEY_WORKER, modelInfo.getName());
        bundle.putString(MLConstant.ML_TASK_BUNDLE_KEY_DATA, data);
        bundle.putInt(MLConstant.ML_TASK_BUNDLE_KEY_CHANNEL, channel);
        return executeTask(taskName(modelInfo.getName()), clientId, bundle, handler);
    }

    /**
     * executeTask
     *
     * @param taskName taskName
     * @param clientId client id
     * @param params   params
     * @param handler  handler
     * @return TaskResult
     */
    public TaskResult executeTask(String taskName, int clientId, Bundle params, IMLTaskResultHandler handler) {
        AbstractMLTask task = MLTaskFactory.get(taskName, clientId, params);
        if (task == null) {
            SmartLog.w(TAG, "can not find task for " + taskName);
            return null;
        }
        TaskResult finalResult = null;
        if (handler == null) {
            task.setTaskType(AbstractMLTask.TYPE_SYNC);
            task.setParams(params);
            finalResult = task.execute();
        } else {
            if (mMLTaskQueue.size() > MAX_QUEUE_SIZE) {
                return null;
            }
            task.setTaskType(AbstractMLTask.TYPE_ASYNC);
            task.setParams(params);
            task.setHandler(handler);
            mMLTaskQueue.offer(task);
            mHandler.sendEmptyMessage(MLHandle.MSG_ADD_TASK);
        }
        return finalResult;
    }

    private String taskName(String workerName) {
        String taskName = MLConstant.Task.TASK_TEXT_DETECTION;
        switch (workerName) {
            case ModelInfo.Model.TEXT_CLASSIFICATION:
                taskName = MLConstant.Task.TASK_TEXT_DETECTION;
                break;
            default:
                break;
        }
        return taskName;
    }

    /**
     * SingletonHolder
     */
    private static class SingletonHolder {
        private static final MachineLearningManager INSTANCE = new MachineLearningManager();
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
                AbstractMLTask task = mMLTaskQueue.poll();
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
                Iterator<AbstractMLTask> queueIterator = mMLTaskQueue.iterator();
                while (queueIterator.hasNext()) {
                    AbstractMLTask tTask = queueIterator.next();
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
                    if ((runnableWeakReference != null && runnableWeakReference.get() == null)
                            || (!executorService.getQueue().contains(runnableWeakReference.get()))) {
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
