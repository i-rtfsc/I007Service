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

import java.io.IOException;

/**
 * BaseClassifier 主要用来加载、卸载 tflite 模型
 *
 * @param <T> 模版类
 * @author solo
 */
public abstract class BaseClassifier<T> extends Classifier<T> {
    private static final String TAG = BaseClassifier.class.getSimpleName();

    protected NLClassifier classifier;

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onApplyModelInfo(Context context, AiModel aiModel) {
        boolean success = false;
        String modelName = aiModel.getFileName();
        try {
            BaseOptions.Builder builder = BaseOptions.builder();
            switch (aiModel.getRuntime()) {
                case AiModel.Runtime.GPU:
                    SmartLog.v(TAG, "model not support gpu, use cpu...");
                    // builder.useGpu();
                    builder.useNnapi();
                    break;
                case AiModel.Runtime.DSP:
                    SmartLog.v(TAG, "model not support dsp, use cpu...");
                    builder.useNnapi();
                    break;
                case AiModel.Runtime.AIP:
                    SmartLog.v(TAG, "model not support aip, use cpu...");
                    builder.useNnapi();
                    break;
                case AiModel.Runtime.CPU:
                    builder.useNnapi();
                    break;
                default:
                    break;
            }

            NLClassifier.NLClassifierOptions options = NLClassifier.NLClassifierOptions.builder()
                    .setBaseOptions(builder.build())
                    .build();
            classifier = NLClassifier.createFromFileAndOptions(context, modelName, options);
            //classifier = NLClassifier.createFromFile(context, modelName);
            success = true;
        } catch (IOException ex) {
            success = false;
            SmartLog.e(TAG, "error loading model " + ex);
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onReleaseModelInfo() {
        classifier.close();
        classifier = null;
        return true;
    }


}
