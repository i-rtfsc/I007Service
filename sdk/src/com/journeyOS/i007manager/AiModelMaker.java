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

/**
 * @author solo
 */
public class AiModelMaker {
    private static final String TAG = AiModelMaker.class.getSimpleName();
    private static volatile AiModelMaker sInstance = null;

    private AiModelMaker() {
    }

    /**
     * 获取 AiModelMaker 单例
     *
     * @return AiModelMaker 实例
     */
    public static AiModelMaker getInstance() {
        if (sInstance == null) {
            synchronized (AiModelMaker.class) {
                if (sInstance == null) {
                    sInstance = new AiModelMaker();
                }
            }
        }
        return sInstance;
    }

    /**
     * 生成 tflite 文字分类模型
     *
     * @return 文字分类模型
     */
    public AiModel makeTFLiteTextClassification() {
        return new AiModel.Builder()
                .setName(AiModel.Model.TEXT_CLASSIFICATION)
                .setFileName("text_classification.tflite")
                .setGraph(AiModel.Graph.TF_LITE)
                .setRuntime(AiModel.Runtime.NNAPI)
                .build();

    }

    /**
     * 生成 tflite 图像分类模型
     *
     * @return 图像分类模型
     */
    public AiModel makeTFLiteImageClassification() {
        return new AiModel.Builder()
                .setName(AiModel.Model.IMAGE_CLASSIFICATION)
                .setFileName("mobilenet_v1_1.0_224.tflite")
                .setGraph(AiModel.Graph.TF_LITE)
                .setRuntime(AiModel.Runtime.GPU)
                .build();

    }

    /**
     * 生成 被量化过的 tflite 图像分类模型
     *
     * @return 被量化过的图像分类模型
     */
    public AiModel makeTFLiteImageClassificationQuantized() {
        return new AiModel.Builder()
                .setName(AiModel.Model.IMAGE_CLASSIFICATION)
                .setFileName("mobilenet_v1_1.0_224_quant.tflite")
                .setGraph(AiModel.Graph.TF_LITE)
                .setRuntime(AiModel.Runtime.NNAPI)
                .build();

    }

    /**
     * 生成 pytorch 图像分类模型
     *
     * @return 图像分类模型
     */
    public AiModel makePytorchImageClassification() {
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
    public AiModel makeSnpeImageClassification() {
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
     * @return 图像分类模型
     */
    public AiModel makeMaceImageClassification() {
        return new AiModel.Builder()
                .setName(AiModel.Model.IMAGE_CLASSIFICATION)
                .setFileName("mobilenet_v1")
                .setConfigName("mobilenet.json")
                .setGraph(AiModel.Graph.MACE)
                .setRuntime(AiModel.Runtime.GPU)
                .build();
    }

}
