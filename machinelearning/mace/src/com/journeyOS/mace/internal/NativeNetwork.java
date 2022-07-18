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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author solo
 */
public class NativeNetwork implements NeuralNetwork {
    private final String TAG = NativeNetwork.class.getSimpleName();
    private int[] mInputTensorShape;
    private int[] mOutputTensorShape;
    private String mInputTensorName;
    private String mOutputTensorName;

    private long mNativeMaceContext = NativeMace.JNI_ERR;
    private String mModelVersion;

    private int mOpenclCacheReusePolicy;

    private String mModelName;
    private Runtime mRuntime;

    private int mThreads;
    private CpuPolicy mCpuPolicy;
    private GpuPerformance mGpuPerformance;
    private GpuPriority mGpuPriority;

    private boolean isDebugEnabled;

    private boolean isFileModel = false;

    /*---------------- file mace_file_model ----------------*/
    private String mModelGraphFilePath;
    private String mModelDataFilePath;
    private String mStorageDirectory;

    private Map<String, int[]> mInputTensorsShapes = new HashMap<>();
    private Map<String, int[]> mOutputTensorsShapes = new HashMap<>();

    private Map<String, FloatTensor> mInputTensors = new HashMap<>();
    private Map<String, FloatTensor> mOutputTensors = new HashMap<>();
    /*---------------- file mace_file_model ----------------*/


    /**
     * 构造函数（code模型）
     *
     * @param modelName              模型名字
     * @param runtime                运行环境
     * @param storagePath
     * @param openclCacheFullPath
     * @param openclCacheReusePolicy
     * @param ompNumThreads          线程数量
     * @param cpuPolicy              cpu调度
     * @param gpuPerfHint            gpu性能
     * @param gpuPriorityHint        gpu优先级
     * @param debug                  是否打开log
     */
    public NativeNetwork(String modelName, Runtime runtime,
                         String storagePath, int openclCacheReusePolicy,
                         int ompNumThreads, CpuPolicy cpuPolicy, GpuPerformance gpuPerfHint, GpuPriority gpuPriorityHint,
                         boolean debug) {
        SmartLog.d(TAG, "start mace code network");
        this.isFileModel = false;
        this.mModelName = modelName;
        this.mRuntime = runtime;
        this.mStorageDirectory = storagePath;
        this.mOpenclCacheReusePolicy = openclCacheReusePolicy;
        this.mThreads = ompNumThreads;
        this.mCpuPolicy = cpuPolicy;
        this.mGpuPerformance = gpuPerfHint;
        this.mGpuPriority = gpuPriorityHint;
        this.isDebugEnabled = debug;

        NativeMace nativeMaceInfo = NativeMace.nativeMaceCodeGetModelInfo(modelName);

        mModelVersion = nativeMaceInfo.getModelVersion();

        mInputTensorsShapes = nativeMaceInfo.getInputTensorsShapes();
        mInputTensorName = mInputTensorsShapes.keySet().iterator().next();
        mInputTensorShape = mInputTensorsShapes.get(mInputTensorName);

        mOutputTensorsShapes = nativeMaceInfo.getOutputTensorsShapes();
        mOutputTensorName = mOutputTensorsShapes.keySet().iterator().next();
        mOutputTensorShape = mOutputTensorsShapes.get(mOutputTensorName);

        mNativeMaceContext = NativeMace.nativeMaceCodeCreateNetworkEngine(mModelName, mRuntime.name(),
                mStorageDirectory, mOpenclCacheReusePolicy,
                mThreads, mCpuPolicy.ordinal, mGpuPerformance.ordinal, mGpuPriority.ordinal, isDebugEnabled);
        SmartLog.d(TAG, "create mace code network engine, native mace context = [" + mNativeMaceContext + "]");
    }

    /**
     * 构造函数（file模型）
     *
     * @param modelName           模型名字
     * @param runtime             运行环境
     * @param modelGraphFilePath  模型graph文件
     * @param modelDataFilePath   模型data文件
     * @param storageDirectory    storage目录
     * @param ompNumThreads       线程数量
     * @param cpuPolicy           cpu调度
     * @param gpuPerfHint         gpu性能
     * @param gpuPriorityHint     gpu优先级
     * @param inputTensorsShapes  输入的shape
     * @param outputTensorsShapes 输出的shape
     * @param debug               是否打开log
     */
    public NativeNetwork(String modelName, Runtime runtime,
                         String modelGraphFilePath, String modelDataFilePath, String storageDirectory,
                         int ompNumThreads, CpuPolicy cpuPolicy, GpuPerformance gpuPerfHint, GpuPriority gpuPriorityHint,
                         Map<String, int[]> inputTensorsShapes, Map<String, int[]> outputTensorsShapes,
                         boolean debug) {
        SmartLog.d(TAG, "start mace file network");
        this.isFileModel = true;
        this.mModelName = modelName;
        this.mRuntime = runtime;

        this.mModelGraphFilePath = modelGraphFilePath;
        this.mModelDataFilePath = modelDataFilePath;
        this.mStorageDirectory = storageDirectory;

        this.mThreads = ompNumThreads;
        this.mCpuPolicy = cpuPolicy;
        this.mGpuPerformance = gpuPerfHint;
        this.mGpuPriority = gpuPriorityHint;

        this.mInputTensorsShapes = inputTensorsShapes;
        this.mOutputTensorsShapes = outputTensorsShapes;

        this.isDebugEnabled = debug;

        mNativeMaceContext = NativeMace.nativeMaceFileCreateNetworkEngine(mModelName, mRuntime.name(),
                mModelGraphFilePath, mModelDataFilePath, mStorageDirectory, mOpenclCacheReusePolicy,
                mThreads, mCpuPolicy.ordinal, mGpuPerformance.ordinal, mGpuPriority.ordinal,
                mInputTensorsShapes, mOutputTensorsShapes, isDebugEnabled);
        SmartLog.d(TAG, "create mace file network engine, native mace context = [" + mNativeMaceContext + "]");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, int[]> getInputTensorsShapes() {
        return mInputTensorsShapes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, int[]> getOutputTensorsShapes() {
        return mOutputTensorsShapes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getInputTensorsNames() {
        Set<String> inputTensorsNames = new HashSet<>();
        for (String key : mInputTensorsShapes.keySet()) {
            inputTensorsNames.add(key);
        }
        return inputTensorsNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getOutputTensorsNames() {
        Set<String> outputTensorsNames = new HashSet<>();
        for (String key : mOutputTensorsShapes.keySet()) {
            outputTensorsNames.add(key);
        }
        return outputTensorsNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, FloatTensor> execute(Map<String, FloatTensor> map) {
        if (mNativeMaceContext == NativeMace.JNI_ERR) {
            SmartLog.e(TAG, "execute fail with mNativeHandle == null");
            return null;
        }
        if (isFileModel) {
            mInputTensors.clear();
            mOutputTensors.clear();
            for (String key : map.keySet()) {
                mInputTensors.put(key, map.get(key));
            }
            boolean ret = NativeMace.nativeMaceFileExecute(mNativeMaceContext, mInputTensors, mOutputTensors);
            return ret ? mOutputTensors : null;
        } else {
            FloatTensor inputTensor = map.get(mInputTensorName);
            float[] input = new float[inputTensor.getSize()];
            inputTensor.read(input, 0, input.length);
            float[] output = NativeMace.nativeMaceCodeExecute(mNativeMaceContext, input);
            FloatTensor outputTensor = new NativeFloatTensor(mOutputTensorShape);
            outputTensor.write(output, 0, output.length);
            mOutputTensors.put(mOutputTensorName, outputTensor);
            return output != null ? mOutputTensors : null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModelVersion() {
        return mModelVersion;
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
    public void release() {
        if (isFileModel) {
            NativeMace.nativeMaceFileRelease(mNativeMaceContext);
        } else {
            NativeMace.nativeMaceCodeRelease(mNativeMaceContext);
        }
        mNativeMaceContext = NativeMace.JNI_ERR;
    }

    @Override
    public FloatTensor createFloatTensor(int[] tensors) {
        return new NativeFloatTensor(tensors);
    }

}
