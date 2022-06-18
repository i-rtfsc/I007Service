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

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.SystemClock;
import android.os.Trace;

import com.journeyOS.common.SmartLog;
import com.journeyOS.i007manager.AiResult;
import com.journeyOS.machinelearning.tasks.TaskResult;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions;
import org.tensorflow.lite.task.vision.classifier.Classifications;

import java.util.ArrayList;
import java.util.List;

/**
 * 图像分类
 *
 * @author solo
 */
public class ImageClassifier extends BaseClassifier<Bitmap> {
    protected static final int MAX_RESULTS = 3;
    private static final String TAG = ImageClassifier.class.getSimpleName();
    private List<String> mImageClasses = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected TaskResult doRecognize(Bitmap data) {
        List<AiResult> results = classify(data);
        return new TaskResult(results);
    }

    @Override
    protected int getTopN() {
        return MAX_RESULTS;
    }

    private List<AiResult> classify(Bitmap bitmap) {
        // Logs this method so that it can be analyzed with systrace.
        Trace.beginSection("recognizeImage");

        TensorImage inputImage = TensorImage.fromBitmap(bitmap);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int cropSize = min(width, height);

        ImageProcessingOptions imageOptions =
                ImageProcessingOptions.builder()
                        // Set the ROI to the center of the image.
                        .setRoi(new Rect(
                                (width - cropSize) / 2,
                                (height - cropSize) / 2,
                                (width + cropSize) / 2,
                                (height + cropSize) / 2)
                        )
                        .build();

        // Runs the inference call.
        Trace.beginSection("runInference");
        long startTimeForReference = SystemClock.uptimeMillis();
        List<Classifications> classifications = imageClassifier.classify(inputImage, imageOptions);
        long endTimeForReference = SystemClock.uptimeMillis();
        Trace.endSection();
        SmartLog.v(TAG, "Run tf-model inference, time = " + (endTimeForReference - startTimeForReference));

        Trace.endSection();

        //top MAX_RESULTS
        List<AiResult> results = new ArrayList<>(MAX_RESULTS);
        for (Category category : classifications.get(0).getCategories()) {
            String className = category.getLabel();
            float score = category.getScore();
            results.add(new AiResult.Builder()
                    .setLabel(className)
                    .setConfidence(score)
                    .build());
        }

        return results;
    }

}
