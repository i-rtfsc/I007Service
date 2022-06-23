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

package com.journeyOS.mace.internal;

import com.journeyOS.i007manager.SmartLog;
import com.journeyOS.mace.core.FloatTensor;
import com.journeyOS.mace.core.NeuralNetwork;

/**
 * @author solo
 */
public class NativeNetwork implements NeuralNetwork {
    private final String TAG = NativeNetwork.class.getSimpleName();
    private int[] mInputTensorShape;
    private int[] mOutputTensorShape;
    private String mInputTensorName;
    private String mOutputTensorName;

    private long mNativeHandle = NativeMace.JNI_ERR;
    private String mModelVersion;

    private String mStorageDirectory;
    private String mOpenclCacheFullPath;
    private int mOpenclCacheReusePolicy;

    private String mModelName;
    private Runtime mRuntime;

    private int mThreads;
    private CpuPolicy mCpuPolicy;
    private GpuPerformance mGpuPerformance;
    private GpuPriority mGpuPriority;

    private boolean isDebugEnabled;

    public NativeNetwork(String storagePath, String openclCacheFullPath, int openclCacheReusePolicy,
                         String modelName, Runtime runtime,
                         int ompNumThreads, CpuPolicy cpuPolicy, GpuPerformance gpuPerfHint, GpuPriority gpuPriorityHint,
                         boolean debug) {
        this.mModelName = modelName;
        this.mRuntime = runtime;
        this.mStorageDirectory = storagePath;
        this.mOpenclCacheFullPath = openclCacheFullPath;
        this.mOpenclCacheReusePolicy = openclCacheReusePolicy;
        this.mThreads = ompNumThreads;
        this.mCpuPolicy = cpuPolicy;
        this.mGpuPerformance = gpuPerfHint;
        this.mGpuPriority = gpuPriorityHint;
        this.isDebugEnabled = debug;

        mModelVersion = NativeMace.nativeGetModelVersion(modelName);
        mInputTensorName = NativeMace.nativeGetInputTensorName(mModelName);
        mInputTensorShape = NativeMace.nativeGetInputTensorShape(mModelName);
        mOutputTensorName = NativeMace.nativeGetOutputTensorName(mModelName);
        mOutputTensorShape = NativeMace.nativeGetOutputTensorShape(mModelName);

        mNativeHandle = NativeMace.nativeMaceCreateNetwork(mStorageDirectory, mOpenclCacheFullPath, mOpenclCacheReusePolicy);
        SmartLog.d(TAG, "create network, success = [" + (mNativeHandle == NativeMace.JNI_OK) + "]");

        if (mNativeHandle == NativeMace.JNI_OK) {
            mNativeHandle = NativeMace.nativeMaceCreateEngine(mModelName, mRuntime.name(), mThreads, mCpuPolicy.ordinal, mGpuPerformance.ordinal, mGpuPriority.ordinal);
            SmartLog.d(TAG, "create engine, success = [" + (mNativeHandle == NativeMace.JNI_OK) + "]");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getInputTensorShape() {
        return mInputTensorShape;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getOutputTensorsShapes() {
        return mOutputTensorShape;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInputTensorName() {
        return mInputTensorName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOutputTensorName() {
        return mOutputTensorName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Runtime getRuntime() {
        return mRuntime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FloatTensor execute(FloatTensor inputTensor) {
        if (mNativeHandle == NativeMace.JNI_ERR) {
            SmartLog.e(TAG, "execute fail with mNativeHandle == null");
            return null;
        }

        float[] output = NativeMace.nativeMaceExecute(new float[inputTensor.getSize()]);
        FloatTensor outputTensor = new FloatTensor(mOutputTensorShape);
        outputTensor.write(output, 0, output.length);

        return output != null ? outputTensor : null;
    }


    @Override
    public String getModelVersion() {
        return mModelVersion;
    }

    @Override
    public void release() {
        //TODO
    }

    @Override
    public FloatTensor createFloatTensor(int[] tensors) {
        return new FloatTensor(tensors);
    }

}
