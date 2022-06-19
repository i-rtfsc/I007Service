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
import android.content.Context;

import com.journeyOS.common.SmartLog;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.machinelearning.Classifier;

import org.pytorch.Device;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * BaseClassifier 主要用来加载、卸载 pytorch 模型
 *
 * @param <T> 模版类
 * @author solo
 */
public abstract class PytorchClassifier<T> extends Classifier<T> {
    private static final String TAG = PytorchClassifier.class.getSimpleName();

    protected Module mModel;

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onStart(Application application, AiModel aiModel) {
        if (isStarted()) {
            SmartLog.e(TAG, "already started");
            return true;
        }

        boolean success = false;
        String modelName = aiModel.getFileName();
        try {
            startInterval();
            mModel = LiteModuleLoader.load(assetFilePath(application, modelName), null, Device.CPU);
            stopInterval("load pytorch model");
            success = true;
        } catch (IOException e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onStop() {
        if (!isStarted()) {
            SmartLog.e(TAG, "already stopped");
            return true;
        }
        mModel.destroy();
        return true;
    }

    /**
     * Copies specified asset to the file in /files app directory and returns this file absolute path.
     *
     * @return absolute file path
     */
    private String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

}
