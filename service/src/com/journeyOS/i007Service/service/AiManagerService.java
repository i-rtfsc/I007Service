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

package com.journeyOS.i007Service.service;

import android.content.Context;
import android.os.RemoteException;

import com.journeyOS.common.SmartLog;
import com.journeyOS.i007Service.AppConfig;
import com.journeyOS.i007manager.AiData;
import com.journeyOS.i007manager.AiModel;
import com.journeyOS.i007manager.AiResult;
import com.journeyOS.i007manager.IAiManager;
import com.journeyOS.i007manager.IAiObserver;
import com.journeyOS.machinelearning.MachineLearningManager;
import com.journeyOS.machinelearning.tasks.ITaskResultHandler;
import com.journeyOS.machinelearning.tasks.TaskResult;
import com.journeyOS.platform.PlatformManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author solo
 */
public final class AiManagerService extends IAiManager.Stub {
    private static final String TAG = AiManagerService.class.getSimpleName();

    private static final AtomicReference<AiManagerService> SERVICE = new AtomicReference<>();
    private boolean supportMachineLearning = false;

    public static AiManagerService getService() {
        return SERVICE.get();
    }

    /**
     * 启动 ai manager service
     *
     * @param context 上下文
     */
    public static void systemReady(Context context) {
        SmartLog.d(TAG, "ai manager service system ready.");
        if (SERVICE.get() == null) {
            new AiManagerService().onCreate(context);
        }
    }

    /**
     * 启动 ai manager service
     *
     * @param context 上下文
     */
    public void onCreate(Context context) {
        SmartLog.d(TAG, "ai manager service running...");
        SERVICE.set(this);
        supportMachineLearning = PlatformManager.getInstance().supportMachineLearning();
        SmartLog.d(TAG, "support machine learning = " + supportMachineLearning);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean initModel(AiModel aiModel) throws RemoteException {
        if (supportMachineLearning) {
            MachineLearningManager.getInstance().init(AppConfig.getInstance().getApplication());
            return MachineLearningManager.getInstance().initWorker(aiModel);
        }

        return false;
    }

    @Override
    public boolean loadModel(AiModel aiModel) throws RemoteException {
        if (supportMachineLearning) {
            return MachineLearningManager.getInstance().loadModel(aiModel);
        }

        return false;
    }

    @Override
    public boolean unloadModel(AiModel aiModel) throws RemoteException {
        if (supportMachineLearning) {
            return MachineLearningManager.getInstance().releaseModel(aiModel);
        }

        return false;
    }

    @Override
    public void recognize(AiModel aiModel, AiData aiData, IAiObserver observer) throws RemoteException {
        if (supportMachineLearning) {
            MachineLearningManager.getInstance().executeTask(aiModel, aiData, getCallingPid(), new ITaskResultHandler() {
                @Override
                public void handleResult(TaskResult result) {
                    try {
                        observer.handleResult(aiData.getChannel(), (List<AiResult>) result.getResult());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
