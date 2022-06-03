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

package com.journeyOS.machinelearning.tflite;

import com.journeyOS.common.SmartLog;
import com.journeyOS.machinelearning.common.Text;
import com.journeyOS.machinelearning.datas.TextResult;
import com.journeyOS.machinelearning.tasks.TaskResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * TFLiteTextDetector
 *
 * @author solo
 */
public class TFLiteTextDetector extends AbstractTFLiteClassifier<Text> {
    private static final String TAG = TFLiteTextDetector.class.getSimpleName();

    private static final int SENTENCE_LEN = 256; // The maximum length of an input sentence.
    // Simple delimiter to split words.
    private static final String SIMPLE_SPACE_OR_PUNCTUATION = " |\\,|\\.|\\!|\\?|\n";

    /**
     * Number of results to show in the UI.
     */
    private static final int MAX_RESULTS = 3;

    /*
     * Reserved values in ImdbDataSet dic:
     * dic["<PAD>"] = 0      used for padding
     * dic["<START>"] = 1    mark for the start of a sentence
     * dic["<UNKNOWN>"] = 2  mark for unknown words (OOV)
     */
    private static final String START = "<START>";
    private static final String PAD = "<PAD>";
    private static final String UNKNOWN = "<UNKNOWN>";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TaskResult> doRecognize(Text text) {
        String word = text.getWord();
        ArrayList<TaskResult> taskResults = new ArrayList<>();
        List<TextResult> textResults = classify(word);
        taskResults.add(new TaskResult(textResults));
        return taskResults;
    }

    private List<TextResult> classify(String text) {
        // Pre-prosessing.
        int[][] input = tokenizeInputText(text);

        // Run inference.
        SmartLog.v(TAG, "Classifying text with TF Lite...");
        float[][] output = new float[1][labels.size()];
        tflite.run(input, output);

        // Find the best classifications.
        PriorityQueue<TextResult> pq =
                new PriorityQueue<>(
                        MAX_RESULTS, (lhs, rhs) -> Float.compare(rhs.getConfidence(), lhs.getConfidence()));

        for (int i = 0; i < labels.size(); i++) {
            pq.add(new TextResult("" + i, labels.get(i), output[0][i]));
        }
        ArrayList<TextResult> textResults = new ArrayList<>();
        while (!pq.isEmpty()) {
            textResults.add(pq.poll());
        }

        Collections.sort(textResults);
        // Return the probability of each class.
        return textResults;
    }

    /**
     * Pre-prosessing: tokenize and map the input words into a float array.
     */
    int[][] tokenizeInputText(String text) {
        int[] tmp = new int[SENTENCE_LEN];
        List<String> array = Arrays.asList(text.split(SIMPLE_SPACE_OR_PUNCTUATION));
        SmartLog.d(TAG, "array size = [" + array.size() + "]");
        SmartLog.d(TAG, "dic size = [" + dic.size() + "]");

        int index = 0;
        // Prepend <START> if it is in vocabulary file.
        if (dic.containsKey(START)) {
            tmp[index++] = dic.get(START);
        }

        SmartLog.d(TAG, "index = [" + index + "]");

        for (String word : array) {
            if (index >= SENTENCE_LEN) {
                break;
            }
            SmartLog.d(TAG, "for index = [" + index + "]");
            tmp[index++] = dic.containsKey(word) ? dic.get(word) : (int) dic.get(UNKNOWN);
        }
        // Padding and wrapping.
        Arrays.fill(tmp, index, SENTENCE_LEN - 1, (int) dic.get(PAD));
        int[][] ans = {tmp};
        return ans;
    }

}
