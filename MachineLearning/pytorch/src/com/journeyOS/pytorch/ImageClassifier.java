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

package com.journeyOS.pytorch;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.os.Trace;

import com.journeyOS.common.SmartLog;
import com.journeyOS.common.utils.FileUtils;
import com.journeyOS.common.utils.JsonHelper;
import com.journeyOS.i007manager.AiResult;
import com.journeyOS.machinelearning.tasks.TaskResult;

import org.pytorch.IValue;
import org.pytorch.MemoryFormat;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 图像分类
 *
 * @author solo
 */
public class ImageClassifier extends BaseClassifier<Bitmap> {
    private static final String TAG = ImageClassifier.class.getSimpleName();
    private List<String> mImageClasses = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onLoadConfig(Context context, String fileName) {
        String json = FileUtils.readFileFromAsset(context, fileName);
        ImageNetClasses imageNetClasses = JsonHelper.fromJson(json, ImageNetClasses.class);
        mImageClasses.addAll(imageNetClasses.labels);
        return mImageClasses.size() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TaskResult doRecognize(Bitmap data) {
        List<AiResult> results = classify(data);
        return new TaskResult(results);
    }

    private List<AiResult> classify(Bitmap bitmap) {
        // preparing input tensor
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap, TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB, MemoryFormat.CHANNELS_LAST);

        Trace.beginSection("runInference");
        long startTimeForReference = SystemClock.uptimeMillis();
        // running the model
        final Tensor outputTensor = mModel.forward(IValue.from(inputTensor)).toTensor();
        long endTimeForReference = SystemClock.uptimeMillis();
        Trace.endSection();
        SmartLog.v(TAG, "Run pytorch-model inference, time = " + (endTimeForReference - startTimeForReference));

        // getting tensor content as java array of floats
        final float[] scores = outputTensor.getDataAsFloatArray();

        // searching for the index with maximum score
        float maxScore = -Float.MAX_VALUE;
        int maxScoreIdx = -1;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxScoreIdx = i;
            }
        }

        String className = mImageClasses.get(maxScoreIdx);

        //top 1
        List<AiResult> results = new ArrayList<>(1);
        results.add(new AiResult.Builder()
                .setLabel(className)
                .setConfidence(maxScore)
                .build());

        return results;
    }


    private class ImageNetClasses {
        int version;
        List<String> labels;
    }
}
