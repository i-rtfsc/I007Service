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

/**
 * TextDetectionTask
 *
 * @author solo
 */
public class TextDetectionTask extends BaseTask {
    public static final String TASK_NAME = TextDetectionTask.class.getSimpleName();
    private static final String TAG = TASK_NAME;

    /**
     * 构造函数
     *
     * @param clientId 客户端id
     * @param worker   工作者
     * @param taskName 任务名称
     * @param aiData   需要识别的数据
     */
    public TextDetectionTask(int clientId, Worker worker, String taskName, AiData aiData) {
        super(clientId, worker, taskName, aiData);
    }

    /**
     * execute
     *
     * @return TaskResult 任务结果
     */
    @Override
    public TaskResult execute() {
        TaskResult taskResults = mWorker.getClassifier().recognize(mAiData);

        // convert and pack result
        if (taskResults == null) {
            return null;
        }

        TaskResult result = generateResult(mAiData, taskResults);
        return result;
    }

}