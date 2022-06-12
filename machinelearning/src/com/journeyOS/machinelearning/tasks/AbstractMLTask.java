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

import android.os.Bundle;

import com.journeyOS.machinelearning.Worker;
import com.journeyOS.machinelearning.common.MLConstant;

import java.util.List;
import java.util.Objects;

/**
 * ML任务
 *
 * @author solo
 */
public abstract class AbstractMLTask {
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
    protected IMLTaskResultHandler mHandler;
    protected Bundle mParams;
    protected Worker mWorker;
    private int mTaskType;

    /**
     * 构造方法
     *
     * @param clientId 客户端id
     * @param worker   worker
     * @param param    bundle
     */
    public AbstractMLTask(int clientId, Worker worker, Bundle param) {
        this.mClientId = clientId;
        mTaskName = getBasicTaskName();
        if (param != null) {
            this.mWorker = worker;
            param.setClassLoader(getClass().getClassLoader());
            this.mTaskName = param.getString(MLConstant.ML_TASK_BUNDLE_KEY_TASK_NAME, getBasicTaskName());
            // Just for one monitor running the same task to recognize different pics in one loop
            int channel = param.getInt(MLConstant.ML_TASK_BUNDLE_KEY_CHANNEL, -1);
            if (channel >= 0) {
                mTaskName += ":" + channel;
            }
        }
    }

    /**
     * 执行任务
     *
     * @return TaskResult
     */
    public abstract TaskResult execute();

    /**
     * 获取基础任务名称
     *
     * @return 基础任务名称
     */
    public abstract String getBasicTaskName();

    /**
     * 获取任务名称
     *
     * @return 任务名称
     */
    public String getTaskName() {
        return mTaskName;
    }

    /**
     * 设置任务名称
     *
     * @param taskName 任务名称
     */
    public void setTaskName(String taskName) {
        this.mTaskName = taskName;
    }

    /**
     * 设置Handler
     *
     * @param handler Handler
     */
    public void setHandler(IMLTaskResultHandler handler) {
        this.mHandler = handler;
    }

    /**
     * 设置参数
     *
     * @param params 参数
     */
    public void setParams(Bundle params) {
        this.mParams = params;
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
     * TODO
     *
     * @param params
     * @param datas
     * @return
     */
    protected TaskResult generateResult(Bundle params, List<TaskResult> datas) {
        return generateResult(params, datas.get(0));
    }

    /**
     * TODO
     *
     * @param params
     * @param data
     * @return
     */
    protected TaskResult generateResult(Bundle params, TaskResult data) {
        int channel = params.getInt(MLConstant.ML_TASK_BUNDLE_KEY_CHANNEL, -1);
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
                    result = generateResult(mParams, result);
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

        AbstractMLTask task = (AbstractMLTask) o;
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