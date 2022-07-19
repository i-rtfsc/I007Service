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

package com.journeyOS.i007manager;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.Map;

/**
 * 模型的信息
 *
 * @author solo
 */
public class AiModel implements Parcelable {
    /**
     * Creator
     */
    public static final Creator<AiModel> CREATOR = new Creator<AiModel>() {
        /**
         * {@inheritDoc}
         */
        @Override
        public AiModel createFromParcel(Parcel in) {
            return new AiModel(in);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public AiModel[] newArray(int size) {
            return new AiModel[size];
        }
    };
    private static final String TAG = AiModel.class.getSimpleName();
    /**
     * 模型名字
     * 如"text_classifier"
     */
    private String name;
    /**
     * 模型文件名
     * 如"text_classifier.tflite"
     */
    private String fileName;
    /**
     * 词汇文件名
     * 如"vocab.txt"
     */
    private String configName = Config.DEFAULT;
    /**
     * 何种模型
     * 如snpe、mace、tflite、ptl
     */
    private String graph = Graph.UNKNOWN;
    /**
     * 模型跑在什么设备上
     * 如CPU、GPU
     */
    private String runtime = Runtime.UNKNOWN;
    /**
     * 模型存在何处
     * 如assets目录、sdcard目录
     */
    private int storage = Storage.UNKNOWN;
    /**
     * 模型是否量化
     */
    private boolean isQuantized = false;
    /**
     * mace模型graph文件
     */
    private String graphPath = "";
    /**
     * mace模型data文件
     */
    private String dataPath = "";
    /**
     * mace模型storage目录
     */
    private String storagePath = "";
    /**
     * 输入shapes
     */
    private Map<String, int[]> inputTensorsShapes;
    /**
     * 输出shapes
     */
    private Map<String, int[]> outputTensorsShapes;

    /**
     * @param name                模型名字
     * @param fileName            模型文件名
     * @param configName          配置（词汇）文件名
     * @param graph               何种模型
     * @param runtime             模型跑在什么设备上
     * @param storage             模型存在何处
     * @param isQuantized         模型是否量化
     * @param graphPath           mace模型graph文件
     * @param dataPath            mace模型data文件
     * @param storagePath         mace模型storage目录
     * @param inputTensorsShapes  输入shapes
     * @param outputTensorsShapes 输出shapes
     */
    public AiModel(String name, String fileName, String configName, String graph, String runtime, int storage, boolean isQuantized, String graphPath, String dataPath, String storagePath, Map<String, int[]> inputTensorsShapes, Map<String, int[]> outputTensorsShapes) {
        this.name = name;
        this.fileName = fileName;
        this.configName = configName;
        this.graph = graph;
        this.runtime = runtime;
        this.storage = storage;
        this.isQuantized = isQuantized;
        this.graphPath = graphPath;
        this.dataPath = dataPath;
        this.storagePath = storagePath;
        this.inputTensorsShapes = inputTensorsShapes;
        this.outputTensorsShapes = outputTensorsShapes;
    }

    /**
     * Parcel
     * @param in Parcel
     */
    protected AiModel(Parcel in) {
        name = in.readString();
        fileName = in.readString();
        configName = in.readString();
        graph = in.readString();
        runtime = in.readString();
        storage = in.readInt();
        isQuantized = in.readByte() != 0;
        graphPath = in.readString();
        dataPath = in.readString();
        storagePath = in.readString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(fileName);
        dest.writeString(configName);
        dest.writeString(graph);
        dest.writeString(runtime);
        dest.writeInt(storage);
        dest.writeByte((byte) (isQuantized ? 1 : 0));
        dest.writeString(graphPath);
        dest.writeString(dataPath);
        dest.writeString(storagePath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
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
    public String getConfigName() {
        return configName;
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
     * 模型是否量化
     *
     * @return ture是量化模型
     */
    public boolean isQuantized() {
        return isQuantized;
    }

    /**
     * 获取mace模型graph文件
     *
     * @return mace模型graph文件
     */
    public String getMaceFileModelGraph() {
        return graphPath;
    }

    /**
     * 获取mace模型data文件
     *
     * @return mace模型data文件
     */
    public String getMaceFileModelData() {
        return dataPath;
    }

    /**
     * 获取mace模型主目录
     *
     * @return mace模型主目录
     */
    public String getMaceFileModelStorage() {
        return storagePath;
    }

    /**
     * 获取 输入shapes
     *
     * @return 输入shapes
     */
    public Map<String, int[]> getInputTensorsShapes() {
        return inputTensorsShapes;
    }

    /**
     * 获取 输出shapes
     *
     * @return 输出shapes
     */
    public Map<String, int[]> getOutputTensorsShapes() {
        return outputTensorsShapes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "AiModel{" +
                "name='" + name + '\'' +
                ", fileName='" + fileName + '\'' +
                ", configName='" + configName + '\'' +
                ", graph='" + graph + '\'' +
                ", runtime='" + runtime + '\'' +
                ", storage=" + storage +
                ", isQuantized=" + isQuantized +
                ", graphPath='" + graphPath + '\'' +
                ", dataPath='" + dataPath + '\'' +
                ", storagePath='" + storagePath + '\'' +
                ", inputTensorsShapes=" + inputTensorsShapes +
                ", outputTensorsShapes=" + outputTensorsShapes +
                '}';
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
        /**
         * image_classification
         */
        public static final String IMAGE_CLASSIFICATION = "image_classification";
    }

    /**
     * 配置文件（如词汇表、vocab.txt）
     */
    public static class Config {
        /**
         * unknown
         */
        public static final String DEFAULT = "vocab.txt";
        /**
         * py_torch_vocab
         */
        public static final String PY_TORCH_VOCAB = "py_torch_vocab.txt";
        /**
         * py_torch_vocab
         */
        public static final String PY_TORCH_LABELS = "labels.json";
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
        public static final String SNPE = "snpe";
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
        public static final String PY_TORCH = "pytorch";
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

        /**
         * nnapi
         */
        public static final String NNAPI = "nnapi";

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
        private String configName = Config.DEFAULT;
        private String graph = Graph.UNKNOWN;
        private String runtime = Runtime.CPU;
        private int storage = Storage.ASSETS;
        private boolean isQuantized = false;

        private Map<String, int[]> inputTensorsShapes;
        private Map<String, int[]> outputTensorsShapes;

        /**
         * mace模型graph文件
         */
        private String graphPath = "";
        /**
         * mace模型data文件
         */
        private String dataPath = "";
        /**
         * mace模型storage目录
         */
        private String storagePath = "";

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
         * @param configName 词汇名
         * @return Builder
         */
        public Builder setConfigName(String configName) {
            this.configName = configName;
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
         * 设置当前模型是否被量化
         *
         * @param quantized 量化
         * @return Builder
         */
        public Builder setQuantized(boolean quantized) {
            isQuantized = quantized;
            return this;
        }

        /**
         * 设置mace模型graph文件
         * mace file 模型才需要设置
         *
         * @param graphPath mace模型graph文件
         * @return Builder
         */
        public Builder setMaceFileModelGraph(String graphPath) {
            this.graphPath = graphPath;
            return this;
        }

        /**
         * 设置mace模型data文件
         * mace file 模型才需要设置
         *
         * @param dataPath mace模型data文件
         * @return Builder
         */
        public Builder setMaceFileModelData(String dataPath) {
            this.dataPath = dataPath;
            return this;
        }

        /**
         * 设置mace模型storage目录
         * mace file 模型才需要设置
         *
         * @param storagePath mace模型storage目录
         * @return Builder
         */
        public Builder setMaceFileModelStorageDirectory(String storagePath) {
            this.storagePath = storagePath;
            return this;
        }

        /**
         * 设置 输入shapes
         *
         * @param inputTensorsShapes 输入shapes
         * @return Builder
         */
        public Builder setInputTensorsShapes(Map<String, int[]> inputTensorsShapes) {
            this.inputTensorsShapes = inputTensorsShapes;
            return this;
        }

        /**
         * 设置 输出shapes
         *
         * @param outputTensorsShapes 输出shapes
         * @return Builder
         */
        public Builder setOutputTensorsShapes(Map<String, int[]> outputTensorsShapes) {
            this.outputTensorsShapes = outputTensorsShapes;
            return this;
        }

        /**
         * 构建
         *
         * @return ModelInfo
         */
        public AiModel build() {
            /**
             * 模型名称不能为空
             */
            if (TextUtils.isEmpty(name)) {
                throw new IllegalStateException("model name was not null");
            }

            /**
             * 模型文件名字不能为空
             */
            if (TextUtils.isEmpty(fileName)) {
                throw new IllegalStateException("model file name was not null");
            }

            /**
             * 模型类型不能为空
             */
            if (TextUtils.isEmpty(graph)) {
                throw new IllegalStateException("graph  was not null");
            }

            return new AiModel(name, fileName, configName, graph, runtime, storage, isQuantized, graphPath, dataPath, storagePath, inputTensorsShapes, outputTensorsShapes);
        }
    }

}
