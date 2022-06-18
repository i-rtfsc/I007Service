package com.journeyOS.i007Service.debug;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.journeyOS.common.SmartLog;
import com.journeyOS.i007Service.R;
import com.journeyOS.i007manager.AiData;
import com.journeyOS.i007manager.AiImage;
import com.journeyOS.i007manager.AiManager;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.i007manager.AiObserver;
import com.journeyOS.i007manager.AiResult;
import com.journeyOS.platform.PlatformManager;

import java.io.IOException;
import java.util.List;

public class AiImageActivity extends AppCompatActivity {
    private static final String TAG = "AiTextActivity";
    Bitmap bitmap = null;
    boolean supportML = false;
    AiManager mAm = null;
    AiModel mModel = new AiModel.Builder()
            .setName(AiModel.Model.IMAGE_CLASSIFICATION)
            .setConfigName(AiModel.Config.PY_TORCH_LABELS)
            .setGraph(AiModel.Graph.PY_TORCH)
            .setRuntime(AiModel.Runtime.GPU)
            .build();
    private TextView mResultTextView;
    private ImageView mImageView;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_image_activity);

        mResultTextView = findViewById(R.id.tv_result);
        mImageView = findViewById(R.id.image);

        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open("image.jpg"));
            mImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        supportML = PlatformManager.getInstance().supportMachineLearning();
        SmartLog.d(TAG, "supportMachineLearning = [" + supportML + "]");
        if (supportML) {
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
                .setType(AiData.IMAGE)
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
}
