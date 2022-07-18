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

import java.util.HashMap;
import java.util.Map;

/**
 * @author solo
 */
public class AiModelBuilder {
    private static final String TAG = AiModelBuilder.class.getSimpleName();

    public static class TextClassification {
        /**
         * 生成 tflite 文字分类模型
         *
         * @return 文字分类模型
         */
        public static AiModel getTflite() {
            return new AiModel.Builder()
                    .setName(AiModel.Model.TEXT_CLASSIFICATION)
                    .setFileName("text_classification.tflite")
                    .setGraph(AiModel.Graph.TF_LITE)
                    .setRuntime(AiModel.Runtime.NNAPI)
                    .build();

        }
    }

    public static class ImageClassification {
        /**
         * 生成 tflite 图像分类模型
         *
         * @return 图像分类模型
         */
        public static AiModel getTflite(boolean quant) {
            AiModel.Builder builder = new AiModel.Builder();
            builder.setName(AiModel.Model.IMAGE_CLASSIFICATION);
            builder.setGraph(AiModel.Graph.TF_LITE);

            if (quant) {
                builder.setFileName("mobilenet_v1_1.0_224_quant.tflite");
                builder.setRuntime(AiModel.Runtime.CPU);
            } else {
                builder.setFileName("mobilenet_v1_1.0_224.tflite");
                builder.setRuntime(AiModel.Runtime.GPU);
            }

            return builder.build();
        }

        /**
         * 生成 pytorch 图像分类模型
         *
         * @return 图像分类模型
         */
        public static AiModel getPytorch() {
            return new AiModel.Builder()
                    .setName(AiModel.Model.IMAGE_CLASSIFICATION)
                    .setFileName("mobilenet_v3_small.pt")
                    .setConfigName("mobilenet_v3_small.json")
                    .setGraph(AiModel.Graph.PY_TORCH)
                    .setRuntime(AiModel.Runtime.CPU)
                    .build();
        }

        /**
         * 生成 snpe 图像分类模型
         *
         * @return 图像分类模型
         */
        public static AiModel getSnpe() {
            return new AiModel.Builder()
                    .setName(AiModel.Model.IMAGE_CLASSIFICATION)
                    .setFileName("inception_v3_quantized.dlc")
                    .setConfigName("inception_v3_quantized.json")
                    .setGraph(AiModel.Graph.SNPE)
                    .setRuntime(AiModel.Runtime.GPU)
                    .build();
        }

        /**
         * 生成 mace 图像分类模型
         *
         * @param maceModel mace model(enum)
         * @return 图像分类模型
         */
        public static AiModel getMace(MaceModel maceModel) {
            return getMaceCodeModel(maceModel);
        }

        /**
         * 生成 mace 图像分类模型
         *
         * @param maceModel mace model(enum)
         * @param fileModel code模型 还是 文件模型
         * @return 图像分类模型
         */
        public static AiModel getMace(MaceModel maceModel, boolean fileModel) {
            if (fileModel) {
                return getMaceFileModel(maceModel);
            } else {
                return getMaceCodeModel(maceModel);
            }
        }

        private static AiModel getMaceCodeModel(MaceModel maceModel) {
            AiModel.Builder builder = new AiModel.Builder();
            builder.setName(AiModel.Model.IMAGE_CLASSIFICATION);
            builder.setConfigName("mobilenet.json");

            builder.setGraph(AiModel.Graph.MACE);
            builder.setFileName(maceModel.ordinal);

            switch (maceModel) {
                case MOBILENET_V1:
                case MOBILENET_V2:
                    builder.setRuntime(AiModel.Runtime.GPU);
                    break;
                case MOBILENET_V1_QUANT:
                case MOBILENET_V2_QUANT:
                    builder.setRuntime(AiModel.Runtime.CPU);
                    break;
                default:
                    break;
            }

            return builder.build();
        }

        private static AiModel getMaceFileModel(MaceModel maceModel) {
            AiModel.Builder builder = new AiModel.Builder();
            builder.setName(AiModel.Model.IMAGE_CLASSIFICATION);
            builder.setConfigName("mobilenet.json");
            builder.setGraph(AiModel.Graph.MACE);
            builder.setFileName(maceModel.ordinal);

            switch (maceModel) {
                case MOBILENET_V1:
                    builder.setRuntime(AiModel.Runtime.GPU);
                    builder.setMaceFileModelData("/sdcard/mobilenet_v1/mobilenet_v1.data");
                    builder.setMaceFileModelGraph("/sdcard/mobilenet_v1/mobilenet_v1.pb");
                    Map<String, int[]> inputTensorsShapes = new HashMap<>();
                    inputTensorsShapes.put("input", new int[]{1, 224, 224, 3});
                    builder.setInputTensorsShapes(inputTensorsShapes);
                    Map<String, int[]> outputTensorsShapes = new HashMap<>();
                    outputTensorsShapes.put("MobilenetV1/Predictions/Reshape_1", new int[]{1, 1001});
                    builder.setOutputTensorsShapes(outputTensorsShapes);
                default:
                    break;
            }

            return builder.build();
        }

        public static enum MaceModel {
            MOBILENET_V1("mobilenet_v1"),
            MOBILENET_V2("mobilenet_v2"),
            MOBILENET_V1_QUANT("mobilenet_v1_quant"),
            MOBILENET_V2_QUANT("mobilenet_v2_quant");

            public final String ordinal;

            private MaceModel(String ordinal) {
                this.ordinal = ordinal;
            }
        }
    }

}
