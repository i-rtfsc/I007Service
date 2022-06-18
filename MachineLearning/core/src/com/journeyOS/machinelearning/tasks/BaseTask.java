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

package com.journeyOS.machinelearning.tasks;

import com.journeyOS.i007manager.AiData;
import com.journeyOS.machinelearning.Worker;

import java.util.Objects;

/**
 * ML任务
 *
 * @author solo
 */
public abstract class BaseTask {
    /**
     * SYNC
     */
    public static final int TYPE_SYNC = 0;
    /**
     * ASYNC
     */
    public static final int TYPE_ASYNC = 1;

    protected String mTaskName;
    protected int mClientId;
    protected ITaskResultHandler mHandler;
    protected AiData mAiData;
    protected Worker mWorker;
    private int mTaskType;


    /**
     * 构造函数
     *
     * @param clientId 客户端id
     * @param worker   工作者
     * @param taskName 任务名称
     * @param aiData   需要识别的ai数据
     */
    public BaseTask(int clientId, Worker worker, String taskName, AiData aiData) {
        this.mClientId = clientId;
        this.mTaskName = taskName;
        this.mWorker = worker;
        this.mAiData = aiData;

        int channel = aiData.getChannel();
        if (channel >= 0) {
            mTaskName += ":" + channel;
        }
    }

    /**
     * 执行任务
     *
     * @return TaskResult
     */
    public abstract TaskResult execute();

    /**
     * 设置Handler
     *
     * @param handler Handler
     */
    public void setHandler(ITaskResultHandler handler) {
        this.mHandler = handler;
    }

    /**
     * 获取任务类型
     *
     * @return 任务类型
     */
    public int getTaskType() {
        return mTaskType;
    }

    /**
     * 设置任务类型
     *
     * @param taskType 任务类型
     */
    public void setTaskType(int taskType) {
        this.mTaskType = taskType;
    }

    /**
     * 生成新的任务数据
     *
     * @param aiData 需要识别的ai数据
     * @param data   任务结果
     * @return TaskResult 任务结果
     */
    protected TaskResult generateResult(AiData aiData, TaskResult data) {
        int channel = aiData.getChannel();
        TaskResult result = new TaskResult(channel, data.getResult());
        return result;
    }

    /**
     * 运行
     *
     * @return Runnable
     */
    public Runnable toRunnable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);
                TaskResult result = execute();
                // Add channel info for msg
                if (result == null) {
                    result = generateResult(mAiData, result);
                }
                mHandler.handleResult(result);
            }
        };
        return runnable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseTask task = (BaseTask) o;
        return mTaskType == task.mTaskType
                && Objects.equals(mHandler, task.mHandler)
                && Objects.equals(mClientId, task.mClientId)
                && Objects.equals(mWorker, task.mWorker);
    }

    /**
     * uniqueId
     *
     * @return String
     */
    public String uniqueId() {
        // remove mParams since different images add task all the time
        return mClientId + "_" + Objects.hash(mTaskType, mHandler, mWorker, mTaskName);
    }
}