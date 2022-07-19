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

import android.app.Application;
import android.util.Pair;

import com.journeyOS.i007manager.AiModel;
import com.journeyOS.i007manager.AiResult;
import com.journeyOS.i007manager.SmartLog;
import com.journeyOS.machinelearning.tasks.TaskResult;

import org.tensorflow.lite.support.metadata.MetadataExtractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文字检测
 *
 * @author solo
 */
public class TextDetector extends TfliteClassifier<String> {
    protected static final int TOP_K = 2;
    private static final String TAG = TextDetector.class.getSimpleName();
    /**
     * The maximum length of an input sentence.
     */
    private static final int SENTENCE_LEN = 256;
    /**
     * Simple delimiter to split words.
     */
    private static final String SIMPLE_SPACE_OR_PUNCTUATION = " |\\,|\\.|\\!|\\?|\n";
    /*
     * Reserved values in ImdbDataSet dic:
     * dic["<PAD>"] = 0      used for padding
     * dic["<START>"] = 1    mark for the start of a sentence
     * dic["<UNKNOWN>"] = 2  mark for unknown words (OOV)
     */
    private static final String START = "<START>";
    private static final String PAD = "<PAD>";
    private static final String UNKNOWN = "<UNKNOWN>";

    private final Map<String, Integer> mDic = new HashMap<>();
    private final List<String> mLabels = new ArrayList<>();

    @Override
    protected boolean onExtraLoad(Application application, AiModel aiModel) {
        try {
            /**
             * Use metadata extractor to extract the dictionary and label files.
             */
            MetadataExtractor metadataExtractor = new MetadataExtractor(mModelBuffer);

            // Extract and load the dictionary file.
            InputStream dictionaryFile = metadataExtractor.getAssociatedFile("vocab.txt");
            loadDictionaryFile(dictionaryFile);

            // Extract and load the label file.
            InputStream labelFile = metadataExtractor.getAssociatedFile("labels.txt");
            loadLabelFile(labelFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskResult doRecognize(String data) {
        List<AiResult> textResults = classify(data);
        return new TaskResult(textResults);
    }


    /**
     * 推演
     *
     * @param text 需要识别的文字
     * @return 推演的结果
     */
    private List<AiResult> classify(String text) {
        List<AiResult> results = new ArrayList<>();

        /**
         * Pre-processing
         */
        int[][] input = tokenizeInputText(text);

        startInterval();
        float[][] output = new float[1][mLabels.size()];
        mTFLite.run(input, output);
        long time = stopInterval("Run tf-lite-model inference");

        /**
         * getting tensor content as java array of floats
         */
        final float[] scores = output[0];

        for (Pair<Integer, Float> pair : topK(TOP_K, scores)) {
            String label = mLabels.get(pair.first);
            float probability = pair.second;
            SmartLog.d(TAG, " label = [" + label + "], probability = [" + probability + "]");
            results.add(new AiResult.Builder()
                    .setLabel(label)
                    .setProbability(probability)
                    .setTime(time)
                    .build()
            );
        }

        return results;
    }

    /**
     * Pre-processing: tokenize and map the input words into a float array.
     */
    private int[][] tokenizeInputText(String text) {
        int[] tmp = new int[SENTENCE_LEN];
        List<String> array = Arrays.asList(text.split(SIMPLE_SPACE_OR_PUNCTUATION));

        int index = 0;
        /**
         * Prepend <START> if it is in vocabulary file.
         */
        if (mDic.containsKey(START)) {
            tmp[index++] = mDic.get(START);
        }

        for (String word : array) {
            if (index >= SENTENCE_LEN) {
                break;
            }
            tmp[index++] = mDic.containsKey(word) ? mDic.get(word) : (int) mDic.get(UNKNOWN);
        }
        /**
         * Padding and wrapping.
         */
        Arrays.fill(tmp, index, SENTENCE_LEN - 1, (int) mDic.get(PAD));
        int[][] ans = {tmp};
        return ans;
    }

    /**
     * Load dictionary from model file.
     */
    private void loadLabelFile(InputStream ins) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
        /**
         * Each line in the label file is a label.
         */
        while (reader.ready()) {
            mLabels.add(reader.readLine());
        }
    }

    /**
     * Load labels from model file.
     */
    private void loadDictionaryFile(InputStream ins) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
        /**
         * Each line in the dictionary has two columns.
         * First column is a word, and the second is the index of this word.
         */
        while (reader.ready()) {
            List<String> line = Arrays.asList(reader.readLine().split(" "));
            if (line.size() < 2) {
                continue;
            }
            mDic.put(line.get(0), Integer.parseInt(line.get(1)));
        }
    }

}
