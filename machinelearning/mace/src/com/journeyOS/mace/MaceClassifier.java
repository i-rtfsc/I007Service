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

package com.journeyOS.mace;

import android.app.Application;

import com.journeyOS.common.SmartLog;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.mace.core.FloatTensor;
import com.journeyOS.mace.core.MACE;
import com.journeyOS.mace.core.NeuralNetwork;
import com.journeyOS.machinelearning.Classifier;

/**
 * Mobile AI Compute Engine  (MACE)
 * MaceClassifier 主要用来加载、卸载 mace 模型
 *
 * @param <T> 模版类
 * @author solo
 */
public abstract class MaceClassifier<T> extends Classifier<T> {
    protected static final boolean DEBUG = true;
    private static final String TAG = MaceClassifier.class.getSimpleName();
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

        String maceVersion = MACE.getInstance().getRuntimeVersion();
        SmartLog.d(TAG, "mace version = [" + maceVersion + "]");

        boolean success = false;
        mNeuralNetwork = loadNetwork(application, aiModel.getFileName(), aiModel.getRuntime());
        SmartLog.d(TAG, "load network success = [" + (mNeuralNetwork != null) + "]");
        if (mNeuralNetwork != null) {
            String modelVersion = mNeuralNetwork.getModelVersion();
            SmartLog.d(TAG, "model version = [" + modelVersion + "]");

            mInputLayer = mNeuralNetwork.getInputTensorName();
            mOutputLayer = mNeuralNetwork.getOutputTensorName();
            mTensorShape = mNeuralNetwork.getInputTensorShape();

            SmartLog.d(TAG, "mInputLayer = [" + mInputLayer + "]");
            SmartLog.d(TAG, "mOutputLayer = [" + mOutputLayer + "]");

            startInterval();
            mInputTensor = mNeuralNetwork.createFloatTensor(mTensorShape);
            stopInterval("create mace tensor");
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
                    SmartLog.d(TAG, "is gray scale = [" + isGrayScale + "]");
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

        startInterval();
        /**
         * create the neural network
         */
        NeuralNetwork network = new MACE.NeuralNetworkBuilder(application)
                .setModelName(fileName)
                .setRuntimeOrder(selectedRuntime)
                .setCpuPolicy(NeuralNetwork.CpuPolicy.BIG_ONLY)
                .setDebugEnabled(false)
                .build();
        stopInterval("build mace");

        return network;
    }

}
