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

import android.app.Application;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import com.journeyOS.common.SmartLog;
import com.journeyOS.machinelearning.AbstractClassifier;
import com.journeyOS.machinelearning.datas.ModelInfo;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.metadata.MetadataExtractor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TFLiteClassifier
 *
 * @param <T> class
 * @author solo
 */
public abstract class AbstractTFLiteClassifier<T> extends AbstractClassifier<T> {
    private static final String TAG = AbstractTFLiteClassifier.class.getSimpleName();
    protected final Map<String, Integer> dic = new HashMap<>();
    protected final List<String> labels = new ArrayList<>();
    protected Interpreter tflite;
    protected String mModelName;

    private boolean debug = false;
    private boolean isLoad = false;

    /**
     * Load TF Lite model from assets.
     */
    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) {
        try {
            AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());

            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableLog(boolean debug) {
        this.debug = debug;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onApplyModelInfo(Application application, ModelInfo modelInfo) {
        if (isLoad) {
            SmartLog.v(TAG, "model has been loaded");
            return false;
        }

        mModelName = modelInfo.getFileName();
        try {
            // Load the TF Lite model
            ByteBuffer buffer = loadModelFile(application.getAssets(), mModelName);
            tflite = new Interpreter(buffer);
            SmartLog.v(TAG, "model loaded");

            // Use metadata extractor to extract the dictionary and label files.
            MetadataExtractor metadataExtractor = new MetadataExtractor(buffer);

            // Extract and load the dictionary file.
            InputStream dictionaryFile = metadataExtractor.getAssociatedFile("vocab.txt");
            loadDictionaryFile(dictionaryFile);
            SmartLog.v(TAG, "dictionary loaded");

            // Extract and load the label file.
            InputStream labelFile = metadataExtractor.getAssociatedFile("labels.txt");
            loadLabelFile(labelFile);
            SmartLog.v(TAG, "labels loaded");
            isLoad = true;
        } catch (IOException ex) {
            isLoad = false;
            SmartLog.e(TAG, "error loading model " + ex);
        }

        return isLoad;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onReleaseModelInfo() {
        if (!isLoad) {
            SmartLog.v(TAG, "model hasn't load");
            return false;
        }

        SmartLog.v(TAG, "release model");
        if (tflite != null) {
            tflite.close();
        }
        dic.clear();
        labels.clear();
        isLoad = false;
        return true;
    }

    /**
     * Load labels from model file.
     */
    private void loadDictionaryFile(InputStream ins) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
        // Each line in the dictionary has two columns.
        // First column is a word, and the second is the index of this word.
        while (reader.ready()) {
            List<String> line = Arrays.asList(reader.readLine().split(" "));
            if (line.size() < 2) {
                continue;
            }
            dic.put(line.get(0), Integer.parseInt(line.get(1)));
        }
    }

    /**
     * Load dictionary from model file.
     */
    private void loadLabelFile(InputStream ins) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
        // Each line in the label file is a label.
        while (reader.ready()) {
            labels.add(reader.readLine());
        }
    }

}
