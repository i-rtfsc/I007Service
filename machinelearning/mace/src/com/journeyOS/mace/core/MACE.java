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

package com.journeyOS.mace.core;

import android.app.Application;
import android.content.Context;

import com.journeyOS.common.SmartLog;
import com.journeyOS.mace.core.NeuralNetwork.Runtime;
import com.journeyOS.mace.internal.NativeMace;
import com.journeyOS.mace.internal.NativeNetwork;

import java.io.File;

/**
 * @author solo
 */
public class MACE {
    private static final String TAG = MACE.class.getSimpleName();

    private static volatile MACE sInstance = null;

    private boolean initialized;
    private Context mContext;

    private String sRuntimeVersion;

    private MACE() {
    }

    /**
     * 获取 MACE 单例
     *
     * @return MACE 实例
     */
    public static MACE getInstance() {
        if (sInstance == null) {
            synchronized (MACE.class) {
                if (sInstance == null) {
                    sInstance = new MACE();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     *
     * @param application 上下文
     */
    public void init(Application application) {
        if (!initialized) {
            mContext = application;
            initialized = true;
        }
    }

    /**
     * 获取当前 mace 版本
     *
     * @return 版本名称
     */
    public String getRuntimeVersion() {
        if (sRuntimeVersion == null) {
            sRuntimeVersion = NativeMace.nativeGetRuntimeVersion();
        }

        return sRuntimeVersion;
    }

    /**
     * NeuralNetworkBuilder
     */
    public static class NeuralNetworkBuilder {
        private String mModelName = "";
        private Runtime[] mRuntimeOrder;
        private Runtime mPreferRuntime = Runtime.CPU;

        private int mThreads = 2;
        private NeuralNetwork.CpuPolicy mCpuPolicy = NeuralNetwork.CpuPolicy.BIG_ONLY;
        private NeuralNetwork.GpuPerformance mGpuPerformance = NeuralNetwork.GpuPerformance.HIGH;
        private NeuralNetwork.GpuPriority mGpuPriority = NeuralNetwork.GpuPriority.HIGH;

        private String mStorageDirectory = "";
        private String mOpenclCacheFullPath = "";
        private int mOpenclCacheReusePolicy = 1;

        private boolean isDebugEnabled;

        /**
         * 构造函数
         *
         * @param application 上下文
         */
        public NeuralNetworkBuilder(Application application) {
            //default fake name
            mModelName = "0x" + this.hashCode();
        }

        /**
         * 设置模型名字
         *
         * @param modelName 模型名字
         * @return NeuralNetworkBuilder
         */
        public NeuralNetworkBuilder setModelName(String modelName) {
            this.mModelName = modelName;
            return this;
        }

        /**
         * 设置运行环境优先级
         *
         * @param order 运行环境
         * @return NeuralNetworkBuilder
         */
        public NeuralNetworkBuilder setRuntimeOrder(Runtime... order) {
            mRuntimeOrder = new Runtime[order.length];
            for (int i = 0; i < order.length; i++) {
                mRuntimeOrder[i] = order[i];
            }
            mPreferRuntime = order[0];
            return this;
        }

        /**
         * 设置线程数
         *
         * @param threads 线程数
         * @return NeuralNetworkBuilder
         */
        public NeuralNetworkBuilder setThreads(int threads) {
            this.mThreads = threads;
            return this;
        }

        /**
         * 设置cpu
         *
         * @param cpuPolicy cpu
         * @return NeuralNetworkBuilder
         */
        public NeuralNetworkBuilder setCpuPolicy(NeuralNetwork.CpuPolicy cpuPolicy) {
            this.mCpuPolicy = cpuPolicy;
            return this;
        }

        /**
         * 设置gpu性能
         *
         * @param gpuPerformance gpu性能
         * @return NeuralNetworkBuilder
         */
        public NeuralNetworkBuilder setGpuPerformance(NeuralNetwork.GpuPerformance gpuPerformance) {
            this.mGpuPerformance = gpuPerformance;
            return this;
        }

        /**
         * 设置gpu优先级
         *
         * @param gpuPriority gpu优先级
         * @return NeuralNetworkBuilder
         */
        public NeuralNetworkBuilder setGpuPriority(NeuralNetwork.GpuPriority gpuPriority) {
            this.mGpuPriority = gpuPriority;
            return this;
        }

        /**
         * 是否打开调试
         *
         * @param isEnabled 调试开关
         * @return NeuralNetworkBuilder
         */
        public NeuralNetworkBuilder setDebugEnabled(boolean isEnabled) {
            isDebugEnabled = isEnabled;
            return this;
        }

        /**
         * 如果是运行环境是GPU，设置storage和openCL cache后会crash
         * 已经给读写权限的情况下，错误提示是文件无法读写。
         * 暂时找不到原因，所以此接口先不使用（改成private）
         *
         * @param storageDirectory
         * @return NeuralNetworkBuilder
         */
        @Deprecated
        private NeuralNetworkBuilder setStorageDirectory(String storageDirectory) {
            File directory = new File(storageDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            mStorageDirectory = storageDirectory;

            /**
             * storage path will be replaced by openCL cache full path in the future
             */
            mOpenclCacheFullPath = mStorageDirectory + File.separator + "mace_cl_compiled_program.bin";
            return this;
        }

        public NeuralNetwork build() throws IllegalArgumentException {
            if (mPreferRuntime == NeuralNetwork.Runtime.GPU && !mStorageDirectory.isEmpty()) {
                File file = new File(mStorageDirectory);
                if (!file.canRead()) {
                    SmartLog.e(TAG, "GPU storage directory: " + mStorageDirectory);
                    throw new IllegalArgumentException("file : " + mStorageDirectory + " could not read");
                }
            }

            NeuralNetwork neuralNetwork = new NativeNetwork(mStorageDirectory, mOpenclCacheFullPath, mOpenclCacheReusePolicy,
                    mModelName, mPreferRuntime,
                    mThreads, mCpuPolicy, mGpuPerformance, mGpuPriority, isDebugEnabled);
            return neuralNetwork;
        }
    }

}
