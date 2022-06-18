package com.journeyOS.i007Service.debug;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.journeyOS.common.SmartLog;
import com.journeyOS.common.utils.FileUtils;
import com.journeyOS.common.utils.JsonHelper;
import com.journeyOS.i007Service.R;
import com.journeyOS.i007manager.AiData;
import com.journeyOS.i007manager.AiManager;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.i007manager.AiObserver;
import com.journeyOS.i007manager.AiResult;
import com.journeyOS.platform.PlatformManager;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AiTextActivity extends AppCompatActivity {
    private static final String TAG = "AiTextActivity";
    boolean supportML = false;
    AiManager mAm = null;
    AiModel mModel = new AiModel.Builder()
            .setName(AiModel.Model.TEXT_CLASSIFICATION)
            .setGraph(AiModel.Graph.TF_LITE)
            .setRuntime(AiModel.Runtime.GPU)
            .build();
    private TextView resultTextView;
    private EditText inputEditText;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_text_activity);

        resultTextView = findViewById(R.id.result_text_view);
        inputEditText = findViewById(R.id.input_text);
        scrollView = findViewById(R.id.scroll_view);

        supportML = PlatformManager.getInstance().supportMachineLearning();
        SmartLog.d(TAG, "supportMachineLearning = [" + supportML + "]");
        if (supportML) {
            mAm = AiManager.getInstance(getApplicationContext());
            mAm.initModel(mModel);
        }

        Button classifyButton = findViewById(R.id.button);
        classifyButton.setOnClickListener(
                (View v) -> {
                    classify(inputEditText.getText().toString());
                });
        test();
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

    private void classify(final String text) {
        AiData aiData = new AiData.Builder()
                .setChannel(11)
                .setType(AiData.TEXT)
                .setWord(text)
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
                        textToShow += String.format("    %s: %s\n", result.getLabel(), result.getConfidence());
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

    private void test() {
        Bean bean = new Bean();
        bean.labels = Arrays.asList(ImageNetClasses.IMAGENET_CLASSES);
        bean.version = 1;
        String json = JsonHelper.toJson(bean);

        File file = new File(getApplicationContext().getFilesDir(), "labels.json");
        SmartLog.d(TAG, "test() called" + file.getAbsolutePath());
        FileUtils.write2File(file, json);
    }
}
