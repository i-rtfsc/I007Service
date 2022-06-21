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

/**
 * @author solo
 */
public interface NeuralNetwork {

    /**
     * 获取输入的shape
     * 如果有多输入的模型可以模仿高通 snpe
     * Map<String,int[]> getInputTensorsShapes();
     *
     * @return int数组
     */
    int[] getInputTensorShape();

    /**
     * 获取输出的shape
     * 如果有多输出的模型可以模仿高通 snpe
     * getOutputTensorsShapes
     *
     * @return int数组
     */
    int[] getOutputTensorsShapes();

    /**
     * 获取输入的tensor name
     * 如果有多输入的模型可以模仿高通 snpe
     * Set<String> getInputTensorsNames();
     *
     * @return 字符串tensor name
     */
    String getInputTensorName();

    /**
     * 获取输出的tensor name
     * 如果有多输出的模型可以模仿高通 snpe
     * Set<String> getOutputTensorsNames();
     *
     * @return 字符串tensor name
     */
    String getOutputTensorName();

    /**
     * 获取当前模型的版本
     *
     * @return 版本号
     */
    String getModelVersion();

    /**
     * 获取当前运行的环境
     *
     * @return 运行环境
     */
    NeuralNetwork.Runtime getRuntime();

    /**
     * 推演
     * 如果有多输入输出的模型可以模仿高通 snpe
     * Map<String, FloatTensor> execute(Map<String, FloatTensor> map);
     *
     * @return FloatTensor
     */
    FloatTensor execute(FloatTensor inputTensor);

    /**
     * 释放模型
     */
    void release();

    /**
     * 创建 FloatTensor
     *
     * @param tensors 输入
     * @return FloatTensor
     */
    FloatTensor createFloatTensor(int[] tensors);

    /**
     * 运行环境
     */
    public static enum Runtime {
        CPU("cpu"),
        GPU("gpu"),
        DSP("dsp");

        public final String ordinal;

        private Runtime(String ordinal) {
            this.ordinal = ordinal;
        }
    }

    /**
     * gpu performance
     */
    public static enum GpuPerformance {
        DEFAULT(0),
        LOW(1),
        NORMAL(2),
        HIGH(3);

        public final int ordinal;

        private GpuPerformance(int ordinal) {
            this.ordinal = ordinal;
        }
    }

    /**
     * gpu priority
     */
    public static enum GpuPriority {
        DEFAULT(0),
        LOW(1),
        NORMAL(2),
        HIGH(3);

        public final int ordinal;

        private GpuPriority(int ordinal) {
            this.ordinal = ordinal;
        }
    }

    /**
     * cpu policy
     */
    public static enum CpuPolicy {
        NONE(0),
        BIG_ONLY(1),
        LITTLE_ONLY(2),
        HIGH_PERFORMANCE(3),
        POWER_SAVE(3);

        public final int ordinal;

        private CpuPolicy(int ordinal) {
            this.ordinal = ordinal;
        }
    }

}
