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

package com.journeyOS.snpe;

import android.app.Application;

import com.journeyOS.common.SmartLog;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.machinelearning.Classifier;
import com.qualcomm.qti.snpe.FloatTensor;
import com.qualcomm.qti.snpe.NeuralNetwork;
import com.qualcomm.qti.snpe.SNPE;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Snapdragon Neural Processing Engine (SNPE)
 * SnpeClassifier 主要用来加载、卸载 snpe 模型
 *
 * @param <T> 模版类
 * @author solo
 */
public abstract class SnpeClassifier<T> extends Classifier<T> {
    protected static final boolean DEBUG = false;
    private static final String TAG = SnpeClassifier.class.getSimpleName();
    protected NeuralNetwork mNeuralNetwork = null;
    protected FloatTensor mInputTensor = null;

    protected String mInputLayer;
    protected String mOutputLayer;

    protected int[] mTensorShape;
    protected int mTensorSize;

    protected boolean isGrayScale;
    protected int mWidth;
    protected int mHeight;

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onStart(Application application, AiModel aiModel) {
        if (isStarted()) {
            SmartLog.e(TAG, "already started with " + mNeuralNetwork);
            return true;
        }

        boolean success = false;
        mNeuralNetwork = loadNetwork(application, aiModel.getFileName(), aiModel.getRuntime());
        if (mNeuralNetwork != null) {
            Set<String> inputNames = mNeuralNetwork.getInputTensorsNames();
            Set<String> outputNames = mNeuralNetwork.getOutputTensorsNames();
            if (inputNames.size() != 1 || outputNames.size() != 1) {
                throw new IllegalStateException("Invalid network input and/or output tensors.");
            } else {
                mInputLayer = inputNames.iterator().next();
                mOutputLayer = outputNames.iterator().next();
            }
            startInterval();
            mInputTensor = mNeuralNetwork.createFloatTensor(mNeuralNetwork.getInputTensorsShapes().get(mInputLayer));
            stopInterval("create snpe tensor");
            success = (mInputTensor != null);
            if (success) {
                /**
                 * (batch_size, height, width, channels)
                 */
                mTensorShape = mInputTensor.getShape();
                mTensorSize = mInputTensor.getSize();

                isGrayScale = (mTensorShape[mTensorShape.length - 1] == 1);
                mWidth = mTensorShape[2];
                mHeight = mTensorShape[1];
                if (DEBUG) {
                    SmartLog.d(TAG, "batch_size = [" + mTensorShape[0] + "]");
                    SmartLog.d(TAG, "channels = [" + isGrayScale + "]");
                    SmartLog.d(TAG, "width = [" + mWidth + "], height = [" + mHeight + "]");
                    SmartLog.d(TAG, "tensor size = [" + mTensorSize + "]");
                }
            }
        }
        SmartLog.d(TAG, "load network, success = [" + success + "]");
        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onStop() {
        if (!isStarted()) {
            SmartLog.e(TAG, "already stopped with " + mNeuralNetwork);
            return true;
        }

        if (mNeuralNetwork != null) {
            mNeuralNetwork.release();
            mNeuralNetwork = null;
        }

        if (mInputTensor != null) {
            mInputTensor.release();
            mInputTensor = null;
        }

        return true;
    }

    private NeuralNetwork loadNetwork(Application application, String fileName, String runtime) {
        SmartLog.d(TAG, "load network, fileName = [" + fileName + "], runtime = [" + runtime + "]");
        NeuralNetwork.Runtime selectedRuntime = NeuralNetwork.Runtime.CPU;
        switch (runtime) {
            case AiModel.Runtime.CPU:
                selectedRuntime = NeuralNetwork.Runtime.CPU;
                break;
            case AiModel.Runtime.GPU:
                selectedRuntime = NeuralNetwork.Runtime.GPU;
                break;
            case AiModel.Runtime.DSP:
                selectedRuntime = NeuralNetwork.Runtime.DSP;
                break;
        }

        try {
            /**
             * input stream to read from the assets
             */
            InputStream assetInputStream = application.getAssets().open(fileName);

            startInterval();
            /**
             * create the neural network
             */
            NeuralNetwork network = new SNPE.NeuralNetworkBuilder(application)
                    .setDebugEnabled(false)
                    .setModel(assetInputStream, assetInputStream.available())
                    .setRuntimeOrder(selectedRuntime) // Runtime.DSP, Runtime.GPU_FLOAT16, Runtime.GPU, Runtime.CPU
                    .setCpuFallbackEnabled(true)
//                    .setPerformanceProfile(NeuralNetwork.PerformanceProfile.HIGH_PERFORMANCE)
                    .build();
            stopInterval("build snpe");
            /**
             * close input
             */
            assetInputStream.close();

            /**
             * all right, network loaded
             */
            return network;
        } catch (IOException e) {
            SmartLog.e(TAG, "io error = " + e);
            e.printStackTrace();
            return null;
        } catch (IllegalStateException | IllegalArgumentException e2) {
            SmartLog.e(TAG, "illegal error = " + e2);
            e2.printStackTrace();
            return null;
        }
    }

}
