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

/**
 * @author solo
 */
public class NativeMace {

    public static final int JNI_OK = 0;
    public static final int JNI_ERR = -1;

    static {
        System.loadLibrary("mace_neural_network");
    }

    /**
     * 获取 mace 版本号
     *
     * @return 版本号
     */
    public static native String nativeGetRuntimeVersion();

    /**
     * 获取 模型版本
     *
     * @param modelName 模型名称
     * @return 模型版本
     */
    public static native String nativeGetModelVersion(String modelName);

    /**
     * 获取输入的 tensor 名字
     *
     * @param modelName 模型名称
     * @return 输入的 tensor 名字
     */
    public static native String nativeGetInputTensorName(String modelName);

    /**
     * 获取输入的 tensor shape
     *
     * @param modelName 模型名称
     * @return tensor shape
     */
    public static native int[] nativeGetInputTensorShape(String modelName);

    /**
     * 获取输出的 tensor 名字
     *
     * @param modelName 模型名称
     * @return 输出的 tensor 名字
     */
    public static native String nativeGetOutputTensorName(String modelName);

    /**
     * 获取输出的 tensor shape
     *
     * @param modelName 模型名称
     * @return tensor shape
     */
    public static native int[] nativeGetOutputTensorShape(String modelName);

    /**
     * 创建网络
     *
     * @param storagePath
     * @param openclCacheFullPath
     * @param opencl_cache_reuse_policy
     * @return 是否成功
     */
    public static native int nativeMaceCreateNetwork(String storagePath, String openclCacheFullPath, int opencl_cache_reuse_policy);

    /**
     * @param model             模型名字
     * @param device            运行环境
     * @param ompNumThreads     线程数量
     * @param cpuAffinityPolicy cpu调度
     * @param gpuPerfHint       gpu性能
     * @param gpuPriorityHint   gpu优先级
     * @return 是否成功
     */
    public static native int nativeMaceCreateEngine(String model, String device, int ompNumThreads, int cpuAffinityPolicy, int gpuPerfHint, int gpuPriorityHint);

    /**
     * 推演
     *
     * @param input 输入的数据
     * @return 输出结果
     */
    public static native float[] nativeMaceExecute(float[] input);

}
