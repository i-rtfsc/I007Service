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

package com.journeyOS.machinelearning.pytorch;

import android.app.Application;
import android.content.Context;

import com.journeyOS.common.SmartLog;
import com.journeyOS.machinelearning.AbstractClassifier;
import com.journeyOS.machinelearning.datas.ModelInfo;

import org.pytorch.Device;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * PyTorchClassifier
 *
 * @param <T> class
 */
public abstract class AbstractPyTorchClassifier<T> extends AbstractClassifier<T> {
    private static final String TAG = AbstractPyTorchClassifier.class.getSimpleName();

    protected String mModelName;
    protected Module model;
    protected String mVocabName;

    private boolean debug = false;
    private boolean isLoad = false;

    private static String assetFilePath(Context context, String assetName) throws IOException {
        SmartLog.d(TAG, "assetName = [" + assetName + "]");
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableLog(boolean debug) {
        this.debug = debug;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onApplyModelInfo(Application application, ModelInfo modelInfo) {
        if (isLoad) {
            SmartLog.v(TAG, "model has been loaded");
            return false;
        }

        mModelName = modelInfo.getFileName();
        mVocabName = modelInfo.getVocabName();

        model = LiteModuleLoader.loadModuleFromAsset(application.getAssets(), mModelName, Device.CPU);

        //TODO

        return isLoad;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean onReleaseModelInfo() {
        if (!isLoad) {
            SmartLog.v(TAG, "model hasn't load");
            return false;
        }

        //TODO
        model.destroy();

        isLoad = false;
        return true;
    }

}
