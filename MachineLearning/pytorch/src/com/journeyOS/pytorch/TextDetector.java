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

package com.journeyOS.pytorch;

import android.content.Context;

import com.journeyOS.i007manager.AiData;
import com.journeyOS.machinelearning.tasks.TaskResult;

/**
 * 文字检测
 *
 * @author solo
 */
public class TextDetector extends BaseClassifier<AiData> {
    private static final String TAG = TextDetector.class.getSimpleName();

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onLoadConfig(Context context, String fileName) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskResult doRecognize(AiData aiData) {
        String text = aiData.getWord();
        String textResults = classify(text);
        return new TaskResult(textResults);
    }

    private String classify(String text) {
        //TODO
        return null;
    }

}
