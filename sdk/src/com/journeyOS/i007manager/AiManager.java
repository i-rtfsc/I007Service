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

import android.os.IBinder;
import android.os.RemoteException;

import com.journeyOS.i007manager.base.ServerLifecycleManager;
import com.journeyOS.i007manager.base.ServiceConstants;
import com.journeyOS.i007manager.base.ServiceManagerNative;

/**
 * @author solo
 */
public class AiManager implements IBinder.DeathRecipient {
    private static final String TAG = AiManager.class.getSimpleName();
    private static volatile AiManager sInstance = null;
    private IAiManager mService = null;
    private ServerLifecycleManager mSlm = null;

    private AiManager() {
        mSlm = new ServerLifecycleManager();
        /**
         * 初始化之后先获取服务，检查服务是否存在。
         * 本来想在getService()之后判断服务存在就调 mSlm.notifyStarted() 通知客户端服务存在
         * 但这里是构造函数，没办法回调给客户端，改成
         */
        getService();
    }

    /**
     * 获取 AiManager 单例
     *
     * @return AiManager 实例
     */
    public static AiManager getInstance() {
        if (sInstance == null) {
            synchronized (AiManager.class) {
                if (sInstance == null) {
                    sInstance = new AiManager();
                }
            }
        }
        return sInstance;
    }

    private IAiManager getService() {
        if (mService != null) {
            return mService;
        }

        if (I007Core.getCore().isRunning()) {
            try {
                IBinder binder = ServiceManagerNative.getInstance().getService(ServiceConstants.SERVICE_AI);
                mService = IAiManager.Stub.asInterface(binder);
                if (mService == null) {
                    SmartLog.e(TAG, "ai manager service wan null, please check I007Core.getCore().startup");
                    mSlm.notifyDied();
                    return null;
                }
                mService.asBinder().linkToDeath(this, 0);
            } catch (IllegalArgumentException | RemoteException e) {
                SmartLog.e(TAG, "error = " + e);
            }
        } else {
            SmartLog.e(TAG, "ai not ready");
        }
        return mService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void binderDied() {
        SmartLog.e(TAG, "remote binder died");
        mService.asBinder().unlinkToDeath(this, 0);
        mService = null;
    }

    /**
     * 初始化模型
     *
     * @param aiModel 模型
     * @return 是否成功
     */
    public boolean initModel(AiModel aiModel) {
        IAiManager service = getService();
        boolean success = (service != null);
        if (success) {
            try {
                success = mService.initModel(aiModel);
            } catch (RemoteException | NullPointerException e) {
                success = false;
                SmartLog.e(TAG, "init model fail = " + e);
            }
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
        IAiManager service = getService();
        boolean success = (service != null);
        if (success) {
            try {
                success = mService.loadModel(aiModel);
            } catch (RemoteException | NullPointerException e) {
                success = false;
                SmartLog.e(TAG, "load model fail = " + e);
            }
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
        IAiManager service = getService();
        boolean success = (service != null);
        if (success) {
            try {
                success = service.unloadModel(aiModel);
            } catch (RemoteException | NullPointerException e) {
                success = false;
                SmartLog.e(TAG, "unload model fail = " + e);
            }
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
        IAiManager service = getService();
        boolean success = (service != null);
        if (success) {
            try {
                mService.recognize(aiModel, aiData, observer);
            } catch (RemoteException | NullPointerException e) {
                success = false;
                SmartLog.e(TAG, "recognize fail = " + e);
            }
        }

        return success;
    }

    /**
     * Registers a callback to be invoked on voice command result.
     *
     * @param listener The callback that will run.
     */
    public void registerListener(ServerLifecycle listener) {
        mSlm.registerListener(listener);
        if (getService() != null) {
            mSlm.notifyStarted();
        } else {
            mSlm.notifyDied();
        }
    }

    /**
     * Unregisters a previous callback.
     *
     * @param listener The callback that should be unregistered.
     * @see #registerListener
     */
    public void unregisterListener(ServerLifecycle listener) {
        mSlm.registerListener(listener);
    }

}
