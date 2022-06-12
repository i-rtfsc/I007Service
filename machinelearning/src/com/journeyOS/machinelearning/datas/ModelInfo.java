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

package com.journeyOS.machinelearning.datas;

import com.google.gson.annotations.SerializedName;

/**
 * 模型的信息
 *
 * @author solo
 */
public class ModelInfo implements Cloneable {
    /**
     * 模型名字
     * 如"text_classifier"
     */
    @SerializedName("name")
    private String name;

    /**
     * 模型文件名
     * 如"text_classifier.tflite"
     */
    @SerializedName("file_name")
    private String fileName;

    /**
     * 词汇文件名
     * 如"vocab.txt"
     */
    @SerializedName("vocab_name")
    private String vocabName = Vocab.DEFAULT;

    /**
     * 何种模型
     * 如snpe、mace、tflite、ptl
     */
    @SerializedName("graph_type")
    private String graph = Graph.UNKNOWN;

    /**
     * 模型跑在什么设备上
     * 如CPU、GPU
     */
    @SerializedName("runtime")
    private String runtime = Runtime.UNKNOWN;

    /**
     * 模型存在何处
     * 如assets目录、sdcard目录
     */
    @SerializedName("storage")
    private int storage = Storage.UNKNOWN;

    /**
     * @param name      模型名字
     * @param fileName  模型文件名
     * @param vocabName 词汇文件名
     * @param graph     何种模型
     * @param runtime   模型跑在什么设备上
     * @param storage   模型存在何处
     */
    public ModelInfo(String name, String fileName, String vocabName, String graph, String runtime, int storage) {
        this.name = name;
        this.fileName = fileName;
        this.vocabName = vocabName;
        this.graph = graph;
        this.runtime = runtime;
        this.storage = storage;
    }

    /**
     * 获取模型名字
     *
     * @return 模型名字
     */
    public String getName() {
        return name;
    }

    /**
     * 获取worker名字（跟模型名字同名）
     *
     * @return name worker名字
     */
    public String getWorkerName() {
        return name;
    }

    /**
     * 获取模型文件名
     *
     * @return 模型文件名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 获取词汇文件名
     *
     * @return 词汇文件名
     */
    public String getVocabName() {
        return vocabName;
    }

    /**
     * 获取何种模型
     *
     * @return 何种模型
     */
    public String getGraph() {
        return graph;
    }

    /**
     * 获取模型跑在什么设备上
     *
     * @return 模型跑在什么设备上
     */
    public String getRuntime() {
        return runtime;
    }

    /**
     * 获取模型存在何处
     *
     * @return 模型存在何处
     */
    public int getStorage() {
        return storage;
    }

    /**
     * Model
     */
    public static class Model {
        /**
         * unknown
         */
        public static final String UNKNOWN = "unknown";
        /**
         * text_classification
         */
        public static final String TEXT_CLASSIFICATION = "text_classification";
    }

    /**
     * Vocab
     */
    public static class Vocab {
        /**
         * unknown
         */
        public static final String DEFAULT = "vocab.txt";
        /**
         * py_torch_vocab
         */
        public static final String PY_TORCH_VOCAB = "py_torch_vocab.txt";
    }

    /**
     * Graph
     */
    public static class Graph {
        /**
         * unknown
         */
        public static final String UNKNOWN = "unknown";
        /**
         * snpe
         */
        public static final String SNPE = "dlc";
        /**
         * mace
         */
        public static final String MACE = "mace";
        /**
         * tflite
         */
        public static final String TF_LITE = "tflite";
        /**
         * ptl
         */
        public static final String PY_TORCH = "ptl";
    }

    /**
     * Runtime
     */
    public static class Runtime {
        /**
         * unknown
         */
        public static final String UNKNOWN = "unknown";
        /**
         * cpu
         */
        public static final String CPU = "cpu";
        /**
         * gpu
         */
        public static final String GPU = "gpu";
        /**
         * aip
         */
        public static final String AIP = "aip";
        /**
         * dsp
         */
        public static final String DSP = "dsp";

    }

    /**
     * Storage
     */
    public static class Storage {
        /**
         * unknown
         */
        public static final int UNKNOWN = 0;
        /**
         * ASSETS
         */
        public static final int ASSETS = 1;
        /**
         * EXTRACTED
         */
        public static final int EXTRACTED = 2;
        /**
         * LOCAL
         */
        public static final int LOCAL = 3;
    }

    /**
     * Builder
     */
    public static class Builder {
        private String name;
        private String fileName;
        private String vocabName = Vocab.DEFAULT;
        private String graph = Graph.UNKNOWN;
        private String runtime = Runtime.CPU;
        private int storage = Storage.ASSETS;

        /**
         * 设置模型名字
         *
         * @param name 模型名字
         * @return Builder
         */
        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置模型文件名
         *
         * @param fileName 模型文件名
         * @return Builder
         */
        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        /**
         * 设置词汇名
         *
         * @param vocabName 词汇名
         * @return Builder
         */
        public Builder setVocabName(String vocabName) {
            this.vocabName = vocabName;
            return this;
        }

        /**
         * 设置模型类型
         *
         * @param graph 何种模型
         * @return Builder
         */
        public Builder setGraph(String graph) {
            this.graph = graph;
            return this;
        }

        /**
         * 设置运行环境
         *
         * @param runtime 运行环境（CPU、GPU）
         * @return Builder
         */
        public Builder setRuntime(String runtime) {
            this.runtime = runtime;
            return this;
        }

        /**
         * 设置文件位置
         *
         * @param storage 位置
         * @return Builder
         */
        public Builder setStorage(int storage) {
            this.storage = storage;
            return this;
        }

        /**
         * 构建
         *
         * @return ModelInfo
         */
        public ModelInfo build() {
            return new ModelInfo(name, fileName + "." + graph, vocabName, graph, runtime, storage);
        }
    }

}
