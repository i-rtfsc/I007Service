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

package com.journeyOS.tflite;

import com.journeyOS.i007manager.AiResult;
import com.journeyOS.machinelearning.tasks.TaskResult;

import org.tensorflow.lite.support.label.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * 文字检测
 *
 * @author solo
 */
public class TextDetector extends BaseClassifier<String> {
    private static final String TAG = TextDetector.class.getSimpleName();

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskResult doRecognize(String data) {
        List<AiResult> textResults = classify(data);
        return new TaskResult(textResults);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getTopN() {
        return -1;
    }

    /**
     * 推演
     *
     * @param text 需要识别的文字
     * @return 推演的结果
     */
    private List<AiResult> classify(String text) {
        List<Category> apiResults = textClassifier.classify(text);
        int size = apiResults.size();
        List<AiResult> results = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Category category = apiResults.get(i);
            String label = category.getLabel();
            float score = category.getScore();

            results.add(new AiResult.Builder()
                    .setLabel(label)
                    .setConfidence(score)
                    .build());
        }
        return results;
    }

}
