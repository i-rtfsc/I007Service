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
import android.text.TextUtils;

import com.journeyOS.i007manager.AiModel;
import com.journeyOS.i007manager.SmartLog;
import com.journeyOS.mace.core.FloatTensor;
import com.journeyOS.mace.core.MACE;
import com.journeyOS.mace.core.NeuralNetwork;
import com.journeyOS.machinelearning.Classifier;

import java.io.IOException;
import java.util.Set;

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
        mNeuralNetwork = loadNetwork(application, aiModel);
        SmartLog.d(TAG, "load network success = [" + (mNeuralNetwork != null) + "]");

        if (mNeuralNetwork != null) {
            Set<String> inputNames = mNeuralNetwork.getInputTensorsNames();
            Set<String> outputNames = mNeuralNetwork.getOutputTensorsNames();
            if (inputNames.size() != 1 || outputNames.size() != 1) {
                throw new IllegalStateException("Invalid network input and/or output tensors.");
            } else {
                mInputLayer = inputNames.iterator().next();
                mOutputLayer = outputNames.iterator().next();
            }
            SmartLog.d(TAG, "inputLayer = [" + mInputLayer + "]");
            SmartLog.d(TAG, "outputLayer = [" + mOutputLayer + "]");
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
                    SmartLog.d(TAG, "isGrayScale = [" + isGrayScale + "]");
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

    private NeuralNetwork loadNetwork(Application application, AiModel aiModel) {
        String fileName = aiModel.getFileName();
        String runtime = aiModel.getRuntime();
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
            default:
                break;
        }

        startInterval();
        /**
         * create the neural network
         */
        MACE.NeuralNetworkBuilder builder = new MACE.NeuralNetworkBuilder(application);
        builder.setModelName(fileName);
        builder.setRuntimeOrder(selectedRuntime);
        builder.setDebugEnabled(DEBUG);
        builder.setCpuPolicy(NeuralNetwork.CpuPolicy.BIG_ONLY);
        if (!TextUtils.isEmpty(aiModel.getMaceFileModelGraph())) {
            try {
                builder.setStorageDirectory(application.getFilesDir().getAbsolutePath());
                builder.setModelData(aiModel.getMaceFileModelData());
                builder.setModelGraph(aiModel.getMaceFileModelGraph());
                builder.setInputLayers(aiModel.getInputTensorsShapes());
                builder.setOutputLayers(aiModel.getOutputTensorsShapes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        NeuralNetwork network = builder.build();
        stopInterval("build mace");

        return network;
    }

}
