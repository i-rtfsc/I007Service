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

import java.util.HashMap;
import java.util.Map;

/**
 * @author solo
 */
public class NativeMace {
    private static final String TAG = NativeMace.class.getSimpleName();
    public static final int JNI_OK = 0;
    public static final int JNI_ERR = -1;

    static {
        System.loadLibrary("mace_neural_network");
    }

    private String mModelVersion;
    private Map<String, int[]> mInputTensorsShapes = new HashMap<>();
    private Map<String, int[]> mOutputTensorsShapes = new HashMap<>();

    /**
     * 构造函数
     */
    public NativeMace() {
    }

    /**
     * 获取当前模型版本号
     * 仅限于code模型
     *
     * @return 模型版本号
     */
    public String getModelVersion() {
        return mModelVersion;
    }

    /**
     * 设置模型版本号
     * 仅限于cpp代码调用
     *
     * @param modelVersion
     */
    @Deprecated
    public void setModelVersion(String modelVersion) {
        this.mModelVersion = modelVersion;
    }

    /**
     * 获取输入的shape
     * 仅限于cpp代码调用
     *
     * @return map[输入名字, int数组]
     */
    public Map<String, int[]> getInputTensorsShapes() {
        return mInputTensorsShapes;
    }

    /**
     * 设置输入的shape
     * 仅限于cpp代码调用
     *
     * @param inputTensorName  输入名字
     * @param inputTensorShape int数组
     */
    @Deprecated
    public void setInputTensorShape(String inputTensorName, int[] inputTensorShape) {
        SmartLog.d(TAG, " inputTensorName = [" + inputTensorName + "], inputTensorShape = [" + inputTensorShape + "]");
        if (!this.mInputTensorsShapes.containsKey(inputTensorName)) {
            this.mInputTensorsShapes.put(inputTensorName, inputTensorShape);
        }
    }

    /**
     * 获取输出的shape
     * 仅限于cpp代码调用
     *
     * @return map[输出名字, int数组]
     */
    public Map<String, int[]> getOutputTensorsShapes() {
        return mOutputTensorsShapes;
    }

    /**
     * 设置输出的shape
     * 仅限于cpp代码调用
     *
     * @param outputTensorName  输出名字
     * @param outputTensorShape int数组
     */
    @Deprecated
    public void setOutputTensorShape(String outputTensorName, int[] outputTensorShape) {
        SmartLog.d(TAG, " outputTensorName = [" + outputTensorName + "], outputTensorShape = [" + outputTensorShape + "]");
        if (!this.mOutputTensorsShapes.containsKey(outputTensorName)) {
            this.mOutputTensorsShapes.put(outputTensorName, outputTensorShape);
        }
    }

    /**
     * 获取模型信息，包括模型版本号、输入的shape、输出的shape等
     *
     * @param modelName 模型名称
     * @return NativeMace-
     */
    public static native NativeMace nativeGetMaceModelInfo(String modelName);

    /**
     * 获取 mace 版本号
     *
     * @return 版本号
     */
    public static native String nativeGetRuntimeVersion();

    /**
     * 初始化 code 网络模型
     *
     * @param model                     模型名字
     * @param targetRuntime             运行环境
     * @param storagePath
     * @param openclCacheFullPath
     * @param opencl_cache_reuse_policy
     * @param ompNumThreads             线程数量
     * @param cpuAffinityPolicy         cpu调度
     * @param gpuPerfHint               gpu性能
     * @param gpuPriorityHint           gpu优先级
     * @return mace上下文
     */
    public static native long nativeMaceCodeCreateNetworkEngine(String model,
                                                                String targetRuntime,
                                                                String storagePath,
                                                                String openclCacheFullPath,
                                                                int opencl_cache_reuse_policy,
                                                                int ompNumThreads,
                                                                int cpuAffinityPolicy,
                                                                int gpuPerfHint,
                                                                int gpuPriorityHint);


    /**
     * 推演
     *
     * @param nativeMaceContext mace上下文
     * @param input             输入的数据
     * @return 输出结果
     */
    public static native float[] nativeMaceCodeExecute(long nativeMaceContext, float[] input);

    /**
     * @param nativeMaceContext mace上下文
     * @return 是否成功
     */
    public static native boolean nativeMaceCodeRelease(long nativeMaceContext);

    /**
     * 初始化 file 网络模型
     *
     * @param modelName          模型名字
     * @param targetRuntime      运行环境
     * @param modelGraphFilePath 模型graph文件
     * @param modelDataFilePath  模型data文件
     * @param storageDirectory   storage目录
     * @param ompNumThreads      线程数量
     * @param cpuAffinityPolicy  cpu调度
     * @param gpuPerfHint        gpu性能
     * @param gpuPriorityHint    gpu优先级
     * @param inputTensorsShapes 输入的shape
     * @param outputTensorShapes 输出的shape
     * @return mace上下文
     */
    public static native long nativeMaceFileCreateNetworkEngine(String modelName,
                                                                String targetRuntime,
                                                                String modelGraphFilePath,
                                                                String modelDataFilePath,
                                                                String storageDirectory,
                                                                int ompNumThreads,
                                                                int cpuAffinityPolicy,
                                                                int gpuPerfHint,
                                                                int gpuPriorityHint,
                                                                Map<String, int[]> inputTensorsShapes,
                                                                Map<String, int[]> outputTensorShapes);


    /**
     * 推演
     *
     * @param nativeMaceContext mace上下文
     * @param inputTensors      输入的shape
     * @param outputTensors     输出的shape
     * @return 是否成功
     */
    public static native boolean nativeMaceFileExecute(long nativeMaceContext,
                                                       Map<String, FloatTensor> inputTensors,
                                                       Map<String, FloatTensor> outputTensors);

    /**
     * @param nativeMaceContext mace上下文
     * @return 是否成功
     */
    public static native boolean nativeMaceFileRelease(long nativeMaceContext);

}
