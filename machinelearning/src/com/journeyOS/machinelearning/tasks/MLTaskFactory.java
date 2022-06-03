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
import com.journeyOS.machinelearning.WorkerFactory;
import com.journeyOS.machinelearning.common.MLConstant;

/**
 * MLTaskFactory
 *
 * @author solo
 */
public final class MLTaskFactory {
    private static final String TAG = MLTaskFactory.class.getSimpleName();

    private MLTaskFactory() {
    }

    /**
     * get MLTask
     *
     * @param taskName taskName
     * @param clientId clientId
     * @param params   params
     * @return MLTask
     */
    public static AbstractMLTask get(String taskName, int clientId, Bundle params) {
        String workerName = params.getString(MLConstant.ML_TASK_BUNDLE_KEY_WORKER);
        Worker worker = WorkerFactory.getInstance().getWorker(workerName);
        AbstractMLTask mlTask = null;
        if (MLConstant.Task.TASK_TEXT_DETECTION.equals(taskName)) {
            mlTask = new TextDetectionTask(clientId, worker, params);
        }
        return mlTask;
    }
}
