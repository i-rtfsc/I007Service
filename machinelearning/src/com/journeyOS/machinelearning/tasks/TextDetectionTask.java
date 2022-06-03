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
import com.journeyOS.machinelearning.common.Text;

import java.util.List;

/**
 * TextDetectionTask
 *
 * @author solo
 */
public class TextDetectionTask extends AbstractMLTask {
    public static final String TASK_NAME = TextDetectionTask.class.getSimpleName();
    private static final String TAG = TASK_NAME;

    /**
     * TextDetectionTask
     *
     * @param clientId clientId
     * @param worker   worker
     * @param param    param
     */
    public TextDetectionTask(int clientId, Worker worker, Bundle param) {
        super(clientId, worker, param);
    }

    /**
     * execute
     *
     * @return TaskResult
     */
    @Override
    public TaskResult execute() {
        String data = mParams.getString(MLConstant.ML_TASK_BUNDLE_KEY_DATA);
        Text text = new Text(data);

        List<TaskResult> taskResults = mWorker.getClassifier().recognize(text);

        // convert and pack result
        if (taskResults.size() <= 0) {
            return null;
        }

        TaskResult result = generateResult(mParams, taskResults);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBasicTaskName() {
        return TASK_NAME;
    }
}