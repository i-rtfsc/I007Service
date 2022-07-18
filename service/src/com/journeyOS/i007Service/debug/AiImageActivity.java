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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.journeyOS.i007Service.R;
import com.journeyOS.i007manager.AiData;
import com.journeyOS.i007manager.AiImage;
import com.journeyOS.i007manager.AiManager;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.i007manager.AiModelBuilder;
import com.journeyOS.i007manager.AiObserver;
import com.journeyOS.i007manager.AiResult;
import com.journeyOS.i007manager.SmartLog;
import com.journeyOS.platform.PlatformManager;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author solo
 */
public class AiImageActivity extends AppCompatActivity {
    private static final String TAG = "AiTextActivity";
    private Context mContext;

    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    private Executor executor = Executors.newSingleThreadExecutor();

    private static final long DELAY_TIME = 200;
    private HandlerThread mBackgroundHandlerThread = null;
    private Handler mBackgroundHandler = null;
    private Runnable mHandleCapturePicRunnable = null;

    private final Object lock = new Object();
    private boolean isCapturePic = false;

    private boolean supportML = false;
    private boolean isLoadModel = false;
    private AiManager mAm = null;
    private AiModel mModel = null;
    private static final int CHANNEL_ID = 10001;

    private boolean isUserRun = false;

    private PreviewView mPreviewView;
    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.ai_image_activity);
        mContext = getApplicationContext();

        mPreviewView = findViewById(R.id.previewView);
        mResultTextView = findViewById(R.id.tv_result);

        if (allPermissionsGranted()) {
            startCamera(); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        mModel = AiModelBuilder.ImageClassification.getMace(AiModelBuilder.ImageClassification.MaceModel.MOBILENET_V1);
        supportML = PlatformManager.getInstance().supportMace();
        SmartLog.d(TAG, "supportMachineLearning = [" + supportML + "]");
        if (supportML) {
            mAm = AiManager.getInstance();
            mAm.initModel(mModel);
        }

        mResultTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUserRun = true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        isLoadModel = mAm.loadModel(mModel);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isLoadModel = !(mAm.unloadModel(mModel));
        stopBackgroundThread();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                handleCapturePic(image);
            }
        });

        ImageCapture.Builder builder = new ImageCapture.Builder();
        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();

        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis, imageCapture);
    }

    private void handleCapturePic(ImageProxy image) {
        SmartLog.d(TAG, "isUserRun = [" + isUserRun + "]");
        if (!isLoadModel || !isUserRun) {
            image.close();
            return;
        }

        @SuppressLint("UnsafeOptInUsageError")
        Bitmap bitmap = toBitmap(image.getImage());

        AiImage aiImage = new AiImage.Builder()
                .setBitmap(bitmap)
                .build();
        AiData aiData = new AiData.Builder()
                .setChannel(CHANNEL_ID)
                .setImage(aiImage)
                .build();
        mAm.recognize(mModel, aiData, new AiObserver() {
            @Override
            public void handleResult(int channel, List<AiResult> results) throws RemoteException {
                // Show classification result on screen
                showResult(results);

                mHandleCapturePicRunnable = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (lock) {
//                            if (isCapturePic) {
//                            }
                            //https://developer.android.com/training/camerax/analyze
                            image.close();
                        }
                        mBackgroundHandler.postDelayed(mHandleCapturePicRunnable, DELAY_TIME);
                    }
                };
                startCapturePic();

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
                    String textToShow = String.format("time=%d(ms)\n", results.get(0).getTime());
                    for (int i = 0; i < results.size(); i++) {
                        AiResult result = results.get(i);
                        textToShow += String.format("    %s(%s)\n", result.getLabel(), result.getProbability());
                    }
                    textToShow += "\n";
                    // Append the result to the UI.
                    SmartLog.d(TAG, "showResult() called with: results = [" + textToShow + "]");
                    mResultTextView.setText(textToShow);
                });
    }

    private Bitmap toBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startCapturePic() {
        if (mBackgroundHandler == null && mBackgroundHandlerThread == null) {
            mBackgroundHandlerThread = new HandlerThread("captureBackground");
            mBackgroundHandlerThread.start();
            mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());
            synchronized (lock) {
                isCapturePic = true;
            }
            mBackgroundHandler.post(mHandleCapturePicRunnable);
        }
    }

    private void stopBackgroundThread() {
        if (mBackgroundHandler != null && mBackgroundHandlerThread != null) {
            try {
                mBackgroundHandlerThread.quitSafely();
                mBackgroundHandlerThread.join();
                mBackgroundHandler = null;
                mBackgroundHandlerThread = null;
                synchronized (lock) {
                    isCapturePic = false;
                }
            } catch (Exception e) {
                SmartLog.e(TAG, "stopBackgroundThread" + e);
            }
        }
    }

}
