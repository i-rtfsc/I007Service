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

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Trace;
import android.util.Pair;

import com.journeyOS.common.SmartLog;
import com.journeyOS.common.utils.FileUtils;
import com.journeyOS.common.utils.JsonHelper;
import com.journeyOS.i007manager.AiModel;
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
public class ImageClassifier extends PytorchClassifier<Bitmap> {
    protected static final int TOP_K = 3;
    private static final String TAG = ImageClassifier.class.getSimpleName();
    private List<String> mImageClasses = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onExtraLoad(Application application, AiModel aiModel) {
        String json = FileUtils.readFileFromAsset(application, aiModel.getConfigName());
        ImageNetClasses imageNetClasses = JsonHelper.fromJson(json, ImageNetClasses.class);
        mImageClasses.addAll(imageNetClasses.labels);
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
        List<AiResult> results = new ArrayList<>(TOP_K);

        // preparing input tensor
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap, TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB, MemoryFormat.CHANNELS_LAST);

        Trace.beginSection("runInference");
        startInterval();
        // running the model
        final Tensor outputTensor = mModel.forward(IValue.from(inputTensor)).toTensor();
        stopInterval("Run pytorch model inference");
        Trace.endSection();

        // getting tensor content as java array of floats
        final float[] scores = outputTensor.getDataAsFloatArray();

        for (Pair<Integer, Float> pair : topK(TOP_K, scores)) {
            String label = mImageClasses.get(pair.first);
            float confidence = pair.second;
            SmartLog.d(TAG, " label = [" + label + "], confidence = [" + confidence + "]");
            results.add(new AiResult.Builder()
                    .setLabel(label)
                    .setConfidence(confidence)
                    .build()
            );
        }
        return results;
    }

    private class ImageNetClasses {
        int version;
        List<String> labels;
    }
}
