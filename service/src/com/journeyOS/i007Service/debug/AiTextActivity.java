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

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.journeyOS.i007Service.R;
import com.journeyOS.i007manager.AiData;
import com.journeyOS.i007manager.AiManager;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.i007manager.AiModelBuilder;
import com.journeyOS.i007manager.AiObserver;
import com.journeyOS.i007manager.AiResult;
import com.journeyOS.i007manager.SmartLog;
import com.journeyOS.platform.PlatformManager;

import java.util.List;

/**
 * @author solo
 */
public class AiTextActivity extends AppCompatActivity {
    private static final String TAG = "AiTextActivity";
    boolean supportTflite = false;
    AiManager mAm = null;
    AiModel mModel = AiModelBuilder.TextClassification.getTflite();
    private TextView resultTextView;
    private EditText inputEditText;
    private ScrollView scrollView;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_text_activity);

        resultTextView = findViewById(R.id.result_text_view);
        inputEditText = findViewById(R.id.input_text);
        scrollView = findViewById(R.id.scroll_view);

        supportTflite = PlatformManager.getInstance().supportTflite();
        SmartLog.d(TAG, "supportTflite = [" + supportTflite + "]");
        if (supportTflite) {
            mAm = AiManager.getInstance();
            mAm.initModel(mModel);
        }

        Button classifyButton = findViewById(R.id.button);
        classifyButton.setOnClickListener(
                (View v) -> {
                    classify(inputEditText.getText().toString());
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        mAm.loadModel(mModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        super.onStop();
        mAm.unloadModel(mModel);
    }

    private void classify(final String text) {
        AiData aiData = new AiData.Builder()
                .setChannel(11)
                .setText(text)
                .build();
        mAm.recognize(mModel, aiData, new AiObserver() {
            @Override
            public void handleResult(int channel, List<AiResult> results) throws RemoteException {
                // Show classification result on screen
                showResult(text, results);
            }
        });
    }

    /**
     * Show classification result on the screen.
     */
    private void showResult(final String inputText, final List<AiResult> results) {
        // Run on UI thread as we'll updating our app UI
        runOnUiThread(
                () -> {
                    String textToShow = "Input: " + inputText + "\nOutput:\n";
                    for (int i = 0; i < results.size(); i++) {
                        AiResult result = results.get(i);
                        textToShow += String.format("    %s: %s\n", result.getLabel(), result.getProbability());
                    }
                    textToShow += "---------\n";

                    // Append the result to the UI.
                    resultTextView.append(textToShow);

                    // Clear the input text.
                    inputEditText.getText().clear();

                    // Scroll to the bottom to show latest entry's classification result.
                    scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                });
    }

}
