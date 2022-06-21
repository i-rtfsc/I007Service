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
public class I007Manager {
    /**
     * 未知场景
     */
    public static final long SCENE_FACTOR_UNKNOWN = -1;
    /**
     * 前台变化为何种类型APP场景
     */
    public static final long SCENE_FACTOR_APP = 1 << 1;
    /**
     * 屏幕亮灭场景
     */
    public static final long SCENE_FACTOR_LCD = 1 << 2;
    /**
     * 网络变化场景
     */
    public static final long SCENE_FACTOR_NET = 1 << 3;
    /**
     * 耳机插拔场景
     */
    public static final long SCENE_FACTOR_HEADSET = 1 << 4;
    /**
     * 电池电量、温度等变化场景
     */
    public static final long SCENE_FACTOR_BATTERY = 1 << 5;

    private static final String TAG = I007Manager.class.getSimpleName();
    private static final AtomicReference<I007Manager> INSTANCE = new AtomicReference<>();
    private II007Manager mRemote;
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.e(TAG, "remote binder died");
            INSTANCE.set(null);
            mRemote.asBinder().unlinkToDeath(this, 0);
            mRemote = null;
            //TODO
        }
    };

    private I007Manager(II007Manager remote) {
        mRemote = remote;
    }

    /**
     * 获取I007Manager单例.
     *
     * @param context 上下文
     * @return I007Manager的单例
     */
    public static I007Manager getInstance(Context context) {
        I007Manager manager = INSTANCE.get();
        if (manager != null) {
            return manager;
        }

        synchronized (I007Manager.class) {
            manager = INSTANCE.get();
            if (manager == null) {
                if (I007Core.getCore().isRunning()) {
                    try {
                        II007Manager remote = II007Manager.Stub.asInterface(
                                ServiceManagerNative.getInstance().getService(ServiceConstants.SERVICE_I007));
                        if (remote == null) {
                            Log.e(TAG, "i007 manager service wan null, please check I007Core.getCore().startup");
                            return null;
                        }
                        manager = new I007Manager(remote);
                        manager.mRemote.asBinder().linkToDeath(manager.mDeathRecipient, 0);
                        INSTANCE.set(manager);
                    } catch (IllegalArgumentException | RemoteException e) {
                        Log.e(TAG, "error = ", e);
                    }
                } else {
                    Log.e(TAG, "i007 not ready");
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
        return Log.isLoggable("I007Service", android.util.Log.DEBUG);
    }

    /**
     * 注册事件监听
     *
     * @param observer 监听回调方
     * @return 是否成功
     */
    public boolean subscribeObserver(I007Observer observer) {
        boolean register;
        try {
            register = mRemote.registerListener(observer);
        } catch (RemoteException | NullPointerException e) {
            register = false;
            Log.e(TAG, "registerListener fail: ", e);
        }
        return register;
    }

    /**
     * 注销事件监听
     *
     * @param observer 回调
     * @return 是否成功
     */
    public boolean unsubscribeObserver(I007Observer observer) {
        boolean unregister;
        try {
            unregister = mRemote.unregisterListener(observer);
        } catch (RemoteException | NullPointerException e) {
            unregister = false;
            Log.e(TAG, "unregisterListener fail: ", e);
        }
        return unregister;
    }

    /**
     * 以覆盖（替换）的方式设置场景因子
     *
     * @param factors 场景因子
     * @return 是否成功
     */
    public boolean setFactor(long factors) {
        boolean result;
        try {
            result = mRemote.setFactor(factors);
        } catch (RemoteException | NullPointerException e) {
            result = false;
            Log.e(TAG, "setFactor fail: ", e);
        }
        return result;
    }

    /**
     * 新增场景因子（｜计算）
     *
     * @param factors 场景因子
     * @return 是否成功
     */
    public boolean updateFactor(long factors) {
        boolean result;
        try {
            result = mRemote.updateFactor(factors);
        } catch (RemoteException | NullPointerException e) {
            result = false;
            Log.e(TAG, "setFactor fail: ", e);
        }
        return result;
    }

    /**
     * 删除场景因子（^计算）
     *
     * @param factors 场景因子
     * @return 是否成功
     */
    public boolean removeFactor(long factors) {
        boolean result;
        try {
            result = mRemote.removeFactor(factors);
        } catch (RemoteException | NullPointerException e) {
            result = false;
            Log.e(TAG, "setFactor fail: ", e);
        }
        return result;
    }
}
