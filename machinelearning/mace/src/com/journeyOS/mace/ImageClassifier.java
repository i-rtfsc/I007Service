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

package com.journeyOS.mace;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Trace;
import android.util.Pair;

import com.journeyOS.common.SmartLog;
import com.journeyOS.common.utils.FileUtils;
import com.journeyOS.common.utils.JsonHelper;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.i007manager.AiResult;
import com.journeyOS.mace.core.FloatTensor;
import com.journeyOS.machinelearning.helpers.BitmapHelper;
import com.journeyOS.machinelearning.helpers.TimeStat;
import com.journeyOS.machinelearning.tasks.TaskResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图像分类
 *
 * @author solo
 */
public class ImageClassifier extends MaceClassifier<Bitmap> {
    protected static final int TOP_K = 3;
    private static final String TAG = ImageClassifier.class.getSimpleName();
    private List<String> mImageClasses = new ArrayList<>();
    private BitmapHelper mBitmapHelper;
    private TimeStat mTimeStat = null;

    @Override
    protected void onExtraLoad(Application application, AiModel aiModel) {
        /**
         * 初始化 labels
         */
        String fileName = aiModel.getConfigName();
        String json = FileUtils.readFileFromAsset(application, fileName);
        ImageNetClasses imageNetClasses = JsonHelper.fromJson(json, ImageNetClasses.class);
        mImageClasses.addAll(imageNetClasses.labels);

        mBitmapHelper = new BitmapHelper();
        /**
         * 注释 使其为空，这样就不调用到其方法了
         * 需要调试的时候在打开
         */
        //mTimeStat = new TimeStat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TaskResult doRecognize(Bitmap data) {
        Bitmap bitmap = resizeIfNeed(data);
        data.recycle();
        List<AiResult> results = classify(bitmap);
        return new TaskResult(results);
    }

    /**
     * 输入的图片如果跟input shape不一样，则resize图片
     *
     * @param bitmap 图片
     * @return 图片
     */
    private Bitmap resizeIfNeed(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int length = width * height;
        if (mTensorSize != length) {
            SmartLog.w(TAG, "tensor width = [" + mWidth + "], tensor height = [" + mHeight + "], width = [" + width + "], height = [" + height + "]");
            return mBitmapHelper.resizeBitmap(bitmap, mWidth, mHeight);
        }
        return bitmap;
    }

    private List<AiResult> classify(Bitmap bitmap) {
        List<AiResult> results = new ArrayList<>();

        float[] rgbBitmapAsFloat;
        if (isGrayScale) {
            rgbBitmapAsFloat = mBitmapHelper.loadGrayScaleBitmapAsFloat(bitmap);
        } else {
            rgbBitmapAsFloat = mBitmapHelper.loadRgbBitmapAsFloat(bitmap);
        }

        if (mTimeStat != null) {
            mTimeStat.startInterval();
        }
        mInputTensor.write(rgbBitmapAsFloat, 0, rgbBitmapAsFloat.length);
        if (mTimeStat != null) {
            mTimeStat.stopInterval("i_tensor", 20, false);
        }

        final Map<String, FloatTensor> inputs = new HashMap<>();
        inputs.put(mInputLayer, mInputTensor);

        Trace.beginSection("runInference");
        startInterval();
        if (mTimeStat != null) {
            mTimeStat.startInterval();
        }
        FloatTensor outputTensor = mNeuralNetwork.execute(mInputTensor);
        if (mTimeStat != null) {
            mTimeStat.stopInterval("nn_exec ", 20, false);
        }
        stopInterval("Run mace-model inference");
        Trace.endSection();

        final float[] array = new float[outputTensor.getSize()];
        outputTensor.read(array, 0, array.length);

        for (Pair<Integer, Float> pair : topK(TOP_K, array)) {
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
