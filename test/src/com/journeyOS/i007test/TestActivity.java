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

package com.journeyOS.i007test;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.journeyOS.i007manager.AiData;
import com.journeyOS.i007manager.AiImage;
import com.journeyOS.i007manager.AiManager;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.i007manager.AiModelBuilder;
import com.journeyOS.i007manager.AiObserver;
import com.journeyOS.i007manager.AiResult;
import com.journeyOS.i007manager.I007Core;
import com.journeyOS.i007manager.ServerLifecycle;
import com.journeyOS.i007manager.SmartLog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author solo
 */
public class TestActivity extends AppCompatActivity implements ServerLifecycle {
    private static final String TAG = "TestActivity";
    Context mContext;
    Bitmap bitmap = null;
    boolean supportML = false;
    AiManager mAm = null;
    AiModel mModel = null;
    private TextView mResultTextView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mContext = getApplicationContext();
        mResultTextView = findViewById(R.id.tv_result);
        mImageView = findViewById(R.id.image);
        I007Core.getCore().registerListener(this);
        I007Core.getCore().startup(this);

        mModel = AiModelBuilder.ImageClassification.getMace(AiModelBuilder.ImageClassification.MaceModel.MOBILENET_V1);
        SmartLog.d(TAG, "model = [" + mModel.toString() + "]");
        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open("fan.png"));
            bitmap = resizeBitmap(bitmap, 224, 224);
            bitmap = compressQuality(bitmap);

            mImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        checkCameraPermission();


        Button classifyButton = findViewById(R.id.button);
        classifyButton.setOnClickListener(
                (View v) -> {
                    classify(bitmap);
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAm != null) {
            mAm.initModel(mModel);
            mAm.loadModel(mModel);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        I007Core.getCore().unregisterListener(this);
        if (mAm != null) {
            mAm.unloadModel(mModel);
        }
    }

    /**
     * 缩放图片
     *
     * @param source 图片
     * @param width  图片新的宽
     * @param height 图片新的高
     * @return 缩放后的图片
     */
    public Bitmap resizeBitmap(Bitmap source, int width, int height) {
        if (source.getHeight() == height && source.getWidth() == width) {
            return source;
        }
        int maxLength = Math.min(width, height);
        try {
            source = source.copy(source.getConfig(), true);
            if (source.getHeight() <= source.getWidth()) {
                /**
                 * if image already smaller than the required height
                 */
                if (source.getHeight() <= maxLength) {
                    return source;
                }

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int targetWidth = (int) (maxLength * aspectRatio);

                return Bitmap.createScaledBitmap(source, targetWidth, maxLength, false);
            } else {
                /**
                 * if image already smaller than the required height
                 */
                if (source.getWidth() <= maxLength) {
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (maxLength * aspectRatio);

                return Bitmap.createScaledBitmap(source, maxLength, targetHeight, false);
            }
        } catch (Exception e) {
            return source;
        }
    }

    private Bitmap compressQuality(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SmartLog.i(TAG, "压缩前图片的大小" + bm.getByteCount() + "byte");
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Bitmap newBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        SmartLog.i(TAG, "压缩后图片的大小" + newBitmap.getByteCount()
                + "byte, 宽度为" + newBitmap.getWidth() + "高度为" + newBitmap.getHeight()
                + "bytes.length=  " + (bytes.length / 1024) + "KB");

        return newBitmap;
    }

    private void classify(final Bitmap bitmap) {
        AiImage aiImage = new AiImage.Builder()
                .setBitmap(bitmap)
                .build();
        AiData aiData = new AiData.Builder()
                .setChannel(11)
                .setImage(aiImage)
                .build();
        mAm.recognize(mModel, aiData, new AiObserver() {
            @Override
            public void handleResult(int channel, List<AiResult> results) throws RemoteException {
                // Show classification result on screen
                showResult(results);
            }
        });
    }

    /**
     * Show classification result on the screen.
     */
    private void showResult(final List<AiResult> results) {
        // Run on UI thread as we'll updating our app UI
        runOnUiThread(
                () -> {
                    String textToShow = "\n";
                    for (int i = 0; i < results.size(); i++) {
                        AiResult result = results.get(i);
                        textToShow += String.format("    %s(%s)\n", result.getLabel(), result.getProbability());
                    }
                    textToShow += "---------\n";
                    // Append the result to the UI.
                    mResultTextView.append(textToShow);
                });
    }

    boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
    }

    @Override
    public void onStarted() {
        SmartLog.d(TAG, "onStarted() called");
        mAm = AiManager.getInstance();
        mAm.initModel(mModel);
        mAm.loadModel(mModel);
    }

    @Override
    public void onDied() {
        SmartLog.d(TAG, "onDied() called");
    }
}
