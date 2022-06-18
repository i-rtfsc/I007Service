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

import android.content.Context;

import com.journeyOS.common.SmartLog;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.machinelearning.Classifier;

import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier;
import org.tensorflow.lite.task.vision.classifier.ImageClassifier;
import org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions;

import java.io.IOException;

/**
 * BaseClassifier 主要用来加载、卸载 tflite 模型
 *
 * @param <T> 模版类
 * @author solo
 */
public abstract class BaseClassifier<T> extends Classifier<T> {
    private static final String TAG = BaseClassifier.class.getSimpleName();

    protected NLClassifier textClassifier = null;
    protected ImageClassifier imageClassifier = null;

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onApplyModelInfo(Context context, AiModel aiModel) {
        boolean success = false;
        if (AiModel.Model.TEXT_CLASSIFICATION.equals(aiModel.getName())) {
            success = loadTextModel(context, aiModel);
        } else if (AiModel.Model.IMAGE_CLASSIFICATION.equals(aiModel.getName())) {
            success = loadImageModel(context, aiModel);
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onReleaseModelInfo() {
        unloadTextModel();
        unloadImageModel();
        return true;
    }

    private boolean loadTextModel(Context context, AiModel aiModel) {
        boolean success = false;
        String modelName = aiModel.getFileName();
        try {
            BaseOptions.Builder builder = BaseOptions.builder();
            switch (aiModel.getRuntime()) {
                case AiModel.Runtime.GPU:
                    SmartLog.w(TAG, "model not support gpu, use cpu...");
                    // builder.useGpu();
                    builder.useNnapi();
                    break;
                case AiModel.Runtime.NNAPI:
                    builder.useNnapi();
                    break;
                default:
                    break;
            }

            NLClassifier.NLClassifierOptions options = NLClassifier.NLClassifierOptions.builder()
                    .setBaseOptions(builder.build())
                    .build();
            textClassifier = NLClassifier.createFromFileAndOptions(context, modelName, options);
            //classifier = NLClassifier.createFromFile(context, modelName);
            success = true;
        } catch (IOException ex) {
            success = false;
            SmartLog.e(TAG, "error loading model " + ex);
        }

        return success;
    }

    private void unloadTextModel() {
        if (imageClassifier != null) {
            textClassifier.close();
            textClassifier = null;
        }
    }

    private boolean loadImageModel(Context context, AiModel aiModel) {
        boolean success = false;
        BaseOptions.Builder baseOptionsBuilder = BaseOptions.builder();
        switch (aiModel.getRuntime()) {
            case AiModel.Runtime.GPU:
                if (!supportGpu(aiModel.getFileName())) {
                    SmartLog.w(TAG, "model not support gpu, use cpu...");
                    baseOptionsBuilder.useNnapi();
                } else {
                    baseOptionsBuilder.useGpu();
                }
                break;
            case AiModel.Runtime.NNAPI:
                baseOptionsBuilder.useNnapi();
                break;
            default:
                break;
        }

        // Create the ImageClassifier instance.
        ImageClassifierOptions options = ImageClassifierOptions.builder()
                .setBaseOptions(baseOptionsBuilder.build())
                .setMaxResults(getTopN())
                .build();

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(context, aiModel.getFileName(), options);
            success = true;
        } catch (IOException e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }

    private void unloadImageModel() {
        if (imageClassifier != null) {
            imageClassifier.close();
            imageClassifier = null;
        }
    }

    /**
     * 当前量化的模型不支持GPU
     * 目前没有什么好手段，只能判断模型名字中包含有字符串"quant"
     * 所以量化过的模型名字的命名就很重要，一定要带上"quant"
     *
     * @param fileName
     * @return
     */
    private boolean supportGpu(String fileName) {
        if (fileName != null && fileName.contains("quant")) {
            return false;
        }

        return true;
    }

    /**
     * 获取多少个结果，等同于我们经常说的 top n
     *
     * @return n个结果
     */
    protected abstract int getTopN();

}
