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

import com.journeyOS.i007manager.AiModel;
import com.journeyOS.i007manager.SmartLog;
import com.journeyOS.machinelearning.Classifier;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.CompatibilityList;
import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.nnapi.NnApiDelegate;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.IOException;
import java.nio.MappedByteBuffer;

/**
 * tensorflow lite
 * TfliteClassifier 主要用来加载、卸载 tflite 模型
 *
 * @param <T> 模版类
 * @author solo
 */
public abstract class TfliteClassifier<T> extends Classifier<T> {
    private static final String TAG = TfliteClassifier.class.getSimpleName();

    protected boolean isQuantized = false;

    private static final int NUM_THREADS = 3;
    /**
     * Options for configuring the Interpreter.
     */
    private final Interpreter.Options mTFLiteOptions = new Interpreter.Options();
    /**
     * An instance of the driver class to run model inference with Tensorflow Lite.
     */
    protected Interpreter mTFLite;
    /**
     * 文字识别模型里有词汇表，这个设置成protected，需要需要的模型使用
     */
    protected MappedByteBuffer mModelBuffer;
    /**
     * Optional GPU delegate for accleration.
     */
    private GpuDelegate mGpuDelegate = null;
    /**
     * Optional NNAPI delegate for accleration.
     */
    private NnApiDelegate mNnApiDelegate = null;

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onStart(Application application, AiModel aiModel) {
        if (isStarted()) {
            SmartLog.e(TAG, "already started");
            return true;
        }

        boolean success = loadTfModel(application, aiModel);

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onStop() {
        if (!isStarted()) {
            SmartLog.e(TAG, "already stopped");
            return true;
        }
        return true;
    }

    boolean loadTfModel(Application application, AiModel aiModel) {
        isQuantized = aiModel.isQuantized();
        try {
            mModelBuffer = FileUtil.loadMappedFile(application, aiModel.getFileName());
            switch (aiModel.getRuntime()) {
                case AiModel.Runtime.GPU:
                    if (!supportGpu(aiModel.getFileName())) {
                        SmartLog.w(TAG, "model not support gpu, use cpu...");
                        mTFLiteOptions.setUseXNNPACK(true);
                    } else {
                        CompatibilityList compatList = new CompatibilityList();
                        if (compatList.isDelegateSupportedOnThisDevice()) {
                            /**
                             * if the device has a supported GPU, add the GPU delegate
                             */
                            GpuDelegate.Options delegateOptions = compatList.getBestOptionsForThisDevice();
                            GpuDelegate gpuDelegate = new GpuDelegate(delegateOptions);
                            mTFLiteOptions.addDelegate(gpuDelegate);
                            SmartLog.d(TAG, "GPU supported. GPU delegate created and added to options");
                        } else {
                            mTFLiteOptions.setUseXNNPACK(true);
                            SmartLog.d(TAG, "GPU not supported. Default to CPU.");
                        }
                    }
                    break;
                case AiModel.Runtime.NNAPI:
                    mNnApiDelegate = new NnApiDelegate();
                    mTFLiteOptions.addDelegate(mNnApiDelegate);
                    break;
                case AiModel.Runtime.CPU:
                    mTFLiteOptions.setUseXNNPACK(true);
                    break;
                default:
                    break;
            }

            mTFLiteOptions.setNumThreads(NUM_THREADS);
            mTFLite = new Interpreter(mModelBuffer, mTFLiteOptions);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
    protected boolean supportGpu(String fileName) {
        if (fileName != null && fileName.contains("quant")) {
            return false;
        }

        return true;
    }

}
