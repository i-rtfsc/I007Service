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

package com.journeyOS.i007manager;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.journeyOS.i007manager.base.ServiceConstants;
import com.journeyOS.i007manager.base.ServiceManagerNative;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author solo
 */
public class AiManager {
    private static final String TAG = AiManager.class.getSimpleName();

    private static final AtomicReference<AiManager> INSTANCE = new AtomicReference<>();

    private IAiManager mService;
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.e(TAG, "remote binder died");
            INSTANCE.set(null);
            mService.asBinder().unlinkToDeath(this, 0);
            mService = null;
            //TODO
        }
    };

    private AiManager(IAiManager remote) {
        mService = remote;
    }

    /**
     * 获取 AiManager 单例.
     *
     * @param context 上下文
     * @return AiManager 的单例
     */
    public static AiManager getInstance(Context context) {
        AiManager manager = INSTANCE.get();
        if (manager != null) {
            return manager;
        }

        synchronized (AiManager.class) {
            manager = INSTANCE.get();
            if (manager == null) {
                if (I007Core.getCore().isRunning()) {
                    try {
                        IAiManager remote = IAiManager.Stub.asInterface(
                                ServiceManagerNative.getInstance().getService(ServiceConstants.SERVICE_AI));
                        if (remote == null) {
                            Log.e(TAG, "ai manager service wan null, please check I007Core.getCore().startup");
                            return null;
                        }
                        manager = new AiManager(remote);
                        manager.mService.asBinder().linkToDeath(manager.mDeathRecipient, 0);
                        INSTANCE.set(manager);
                    } catch (IllegalArgumentException | RemoteException e) {
                        Log.e(TAG, "error = ", e);
                    }
                } else {
                    Log.e(TAG, "ai not ready");
                }
            }
        }
        return manager;
    }

    /**
     * 通过命令 adb shell setprop log.tag.I007Service D 打开log
     *
     * @return 是否打开log
     */
    public boolean isDebug() {
        return Log.isLoggable("I007Service", Log.DEBUG);
    }

    /**
     * 初始化模型
     *
     * @param aiModel 模型
     * @return 是否成功
     */
    public boolean initModel(AiModel aiModel) {
        boolean success;
        try {
            success = mService.initModel(aiModel);
        } catch (RemoteException | NullPointerException e) {
            success = false;
            Log.e(TAG, "init model fail: ", e);
        }
        return success;
    }

    /**
     * 加载模型
     *
     * @param aiModel 模型
     * @return 是否成功
     */
    public boolean loadModel(AiModel aiModel) {
        boolean success;
        try {
            success = mService.loadModel(aiModel);
        } catch (RemoteException | NullPointerException e) {
            success = false;
            Log.e(TAG, "load model fail: ", e);
        }
        return success;
    }

    /**
     * 卸载模型
     *
     * @param aiModel 模型
     * @return 是否成功
     */
    public boolean unloadModel(AiModel aiModel) {
        boolean success;
        try {
            success = mService.unloadModel(aiModel);
        } catch (RemoteException | NullPointerException e) {
            success = false;
            Log.e(TAG, "unload model fail: ", e);
        }
        return success;
    }

    /**
     * 识别
     *
     * @param aiModel  模型信息
     * @param aiData   需要识别的数据
     * @param observer 回调
     * @return 是否成功
     */
    public boolean recognize(AiModel aiModel, AiData aiData, AiObserver observer) {
        boolean success = true;
        try {
            mService.recognize(aiModel, aiData, observer);
        } catch (RemoteException | NullPointerException e) {
            success = false;
            Log.e(TAG, "unload model fail: ", e);
        }
        return success;
    }

}
