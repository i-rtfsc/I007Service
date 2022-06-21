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

package com.journeyOS.i007Service.debug;

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

import com.journeyOS.common.SmartLog;
import com.journeyOS.i007Service.R;
import com.journeyOS.i007manager.AiData;
import com.journeyOS.i007manager.AiImage;
import com.journeyOS.i007manager.AiManager;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.i007manager.AiModelMaker;
import com.journeyOS.i007manager.AiObserver;
import com.journeyOS.i007manager.AiResult;
import com.journeyOS.platform.PlatformManager;

import java.io.IOException;
import java.util.List;

/**
 * @author solo
 */
public class AiImageActivity extends AppCompatActivity {
    private static final String TAG = "AiTextActivity";
    Context mContext;
    Bitmap bitmap = null;
    boolean supportML = false;
    AiManager mAm = null;
    AiModel mModel = AiModelMaker.getInstance().makeMaceImageClassification();
    private TextView mResultTextView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_image_activity);
        mContext = getApplicationContext();
        mResultTextView = findViewById(R.id.tv_result);
        mImageView = findViewById(R.id.image);

        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open("image.jpg"));
            mImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        supportML = PlatformManager.getInstance().supportMace();
        SmartLog.d(TAG, "supportMachineLearning = [" + supportML + "]");
        if (supportML) {
            checkCameraPermission();

            mAm = AiManager.getInstance(getApplicationContext());
            mAm.initModel(mModel);
        }

        Button classifyButton = findViewById(R.id.button);
        classifyButton.setOnClickListener(
                (View v) -> {
                    classify(bitmap);
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAm.loadModel(mModel);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAm.unloadModel(mModel);
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
                        textToShow += String.format("    %s(%s)\n", result.getLabel(), result.getConfidence());
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
}
