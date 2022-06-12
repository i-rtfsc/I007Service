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

package com.journeyOS.machinelearning.pytorch;

import com.journeyOS.machinelearning.common.Text;
import com.journeyOS.machinelearning.tasks.TaskResult;

import java.util.ArrayList;
import java.util.List;

/**
 * PyTorchTextDetector
 *
 * @author solo
 */
public class PyTorchTextDetector extends AbstractPyTorchClassifier<Text> {
    private static final String TAG = PyTorchTextDetector.class.getSimpleName();

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TaskResult> doRecognize(Text datas) {
        String text = datas.getWord();
        ArrayList<TaskResult> taskResults = new ArrayList<>();
        String textResults = classify(text);
        taskResults.add(new TaskResult(textResults));
        return taskResults;
    }

    private String classify(String text) {
        return null;
    }

}
