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

package com.journeyOS.tflite;

import static java.lang.Math.min;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Trace;
import android.util.Pair;

import com.journeyOS.common.utils.FileUtils;
import com.journeyOS.common.utils.JsonHelper;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.i007manager.AiResult;
import com.journeyOS.i007manager.SmartLog;
import com.journeyOS.machinelearning.tasks.TaskResult;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * 图像分类
 *
 * @author solo
 */
public class ImageClassifier extends TfliteClassifier<Bitmap> {
    protected static final int TOP_K = 3;
    private static final String TAG = ImageClassifier.class.getSimpleName();

    private List<String> mImageClasses = new ArrayList<>();

    /**
     * Image size along the x axis.
     */
    private int mImageSizeX;

    /**
     * Image size along the y axis.
     */
    private int mImageSizeY;

    /**
     * Input image TensorBuffer.
     */
    private TensorImage mInputImageBuffer;

    /**
     * Output probability TensorBuffer.
     */
    private TensorBuffer mOutputProbabilityBuffer;

    /**
     * Processer to apply post processing of the output probability.
     */
    private TensorProcessor mProbabilityProcessor;

    @Override
    protected boolean onExtraLoad(Application application, AiModel aiModel) {
        /**
         * Reads type and shape of input and output tensors, respectively.
         */
        int imageTensorIndex = 0;
        /**
         * {1, height, width, 3}
         */
        int[] imageShape = mTFLite.getInputTensor(imageTensorIndex).shape();
        mImageSizeY = imageShape[1];
        mImageSizeX = imageShape[2];

        DataType imageDataType = mTFLite.getInputTensor(imageTensorIndex).dataType();
        int probabilityTensorIndex = 0;
        /**
         * {1, NUM_CLASSES}
         */
        int[] probabilityShape = mTFLite.getOutputTensor(probabilityTensorIndex).shape();
        DataType probabilityDataType = mTFLite.getOutputTensor(probabilityTensorIndex).dataType();

        /**
         * Creates the input tensor.
         */
        mInputImageBuffer = new TensorImage(imageDataType);

        /**
         * Creates the output tensor and its processor.
         */
        mOutputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);

        /**
         * Creates the post processor for the output probability.
         */
        mProbabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

        String fileName = aiModel.getConfigName();
        String json = FileUtils.readFileFromAsset(application, fileName);
        ImageNetClasses imageNetClasses = JsonHelper.fromJson(json, ImageNetClasses.class);
        mImageClasses.addAll(imageNetClasses.labels);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TaskResult doRecognize(Bitmap data) {
        List<AiResult> results = classify(data);
        return new TaskResult(results);
    }

    private TensorImage loadImage(final Bitmap bitmap, int sensorOrientation) {
        /**
         * Loads bitmap into a TensorImage.
         */
        mInputImageBuffer.load(bitmap);

        /**
         * Creates processor for the TensorImage.
         */
        int cropSize = min(bitmap.getWidth(), bitmap.getHeight());
        int numRotation = sensorOrientation / 90;

        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(mImageSizeX, mImageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(new Rot90Op(numRotation))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(mInputImageBuffer);
    }

    private List<AiResult> classify(Bitmap bitmap) {
        List<AiResult> results = new ArrayList<>();

        /**
         * Logs this method so that it can be analyzed with systrace.
         */
        Trace.beginSection("recognizeImage");

        mInputImageBuffer = loadImage(bitmap, 1);

        Trace.beginSection("runInference");
        startInterval();
        /**
         * Runs the inference call.
         */
        mTFLite.run(mInputImageBuffer.getBuffer(), mOutputProbabilityBuffer.getBuffer().rewind());
        long time = stopInterval("Run tf-lite-model inference");
        Trace.endSection();

        Trace.endSection();

        /**
         * getting tensor content as java array of floats
         */
        final float[] scores = mProbabilityProcessor.process(mOutputProbabilityBuffer).getFloatArray();

        for (Pair<Integer, Float> pair : topK(TOP_K, scores)) {
            String label = mImageClasses.get(pair.first);
            float probability = pair.second;
            SmartLog.d(TAG, " label = [" + label + "], probability = [" + probability + "]");
            results.add(new AiResult.Builder()
                    .setLabel(label)
                    .setProbability(probability)
                    .setTime(time)
                    .build()
            );
        }

        return results;
    }

    private TensorOperator getPreprocessNormalizeOp() {
        if (isQuantized) {
            /**
             * The quantized model does not require normalization, thus set mean as 0.0f, and std as 1.0f to
             * bypass the normalization.
             */
            final float IMAGE_MEAN = 0.0f;
            final float IMAGE_STD = 1.0f;
            return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
        } else {

            /** Float MobileNet requires additional normalization of the used input. */
            final float IMAGE_MEAN = 127.5f;

            final float IMAGE_STD = 127.5f;
            return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
        }
    }

    private TensorOperator getPostprocessNormalizeOp() {
        if (isQuantized) {
            /** Quantized MobileNet requires additional dequantization to the output probability. */
            final float PROBABILITY_MEAN = 0.0f;

            final float PROBABILITY_STD = 255.0f;
            return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
        } else {
            /**
             * Float model does not need dequantization in the post-processing. Setting mean and std as 0.0f
             * and 1.0f, repectively, to bypass the normalization.
             */
            final float PROBABILITY_MEAN = 0.0f;
            final float PROBABILITY_STD = 1.0f;
            return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
        }
    }

    private class ImageNetClasses {
        int version;
        List<String> labels;
    }


}
