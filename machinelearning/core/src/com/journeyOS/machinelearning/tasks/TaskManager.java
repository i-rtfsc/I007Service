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
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.machinelearning.Worker;
import com.journeyOS.machinelearning.WorkerManager;
import com.journeyOS.machinelearning.common.MLConstant;

/**
 * MLTaskFactory
 *
 * @author solo
 */
public final class TaskManager {
    private static final String TAG = TaskManager.class.getSimpleName();

    /**
     * get MLTask
     *
     * @param workerName 工作者名字
     * @param clientId   客户端id
     * @param aiData     需要预测的AI数据
     * @return BaseTask
     */
    public static BaseTask get(String workerName, int clientId, AiData aiData) {
        String taskName = taskName(workerName);
        Worker worker = WorkerManager.getInstance().getWorker(workerName);
        BaseTask task = null;
        if (MLConstant.Task.TASK_TEXT_DETECTION.equals(taskName)) {
            task = new TextDetectionTask(clientId, worker, taskName, aiData);
        } else if (MLConstant.Task.TASK_IMAGE_CLASSIFIER.equals(taskName)) {
            task = new ImageClassifierTask(clientId, worker, taskName, aiData);
        }
        return task;
    }

    private static String taskName(String workerName) {
        String taskName = MLConstant.Task.TASK_TEXT_DETECTION;
        switch (workerName) {
            case AiModel.Model.TEXT_CLASSIFICATION:
                taskName = MLConstant.Task.TASK_TEXT_DETECTION;
                break;
            case AiModel.Model.IMAGE_CLASSIFICATION:
                taskName = MLConstant.Task.TASK_IMAGE_CLASSIFIER;
                break;
            default:
                break;
        }
        return taskName;
    }
}
