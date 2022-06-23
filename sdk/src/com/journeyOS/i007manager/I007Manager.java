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
public class I007Manager implements IBinder.DeathRecipient {
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
    private static volatile I007Manager sInstance = null;
    private II007Manager mService = null;
    private ServerLifecycleManager mSlm = null;

    private I007Manager() {
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
    public static I007Manager getInstance() {
        if (sInstance == null) {
            synchronized (I007Manager.class) {
                if (sInstance == null) {
                    sInstance = new I007Manager();
                }
            }
        }
        return sInstance;
    }

    private II007Manager getService() {
        if (mService != null) {
            return mService;
        }

        if (I007Core.getCore().isRunning()) {
            try {
                IBinder binder = ServiceManagerNative.getInstance().getService(ServiceConstants.SERVICE_I007);
                mService = II007Manager.Stub.asInterface(binder);
                if (mService == null) {
                    SmartLog.e(TAG, "i007 manager service wan null, please check I007Core.getCore().startup");
                    mSlm.notifyDied();
                    return null;
                }
                mService.asBinder().linkToDeath(this, 0);
            } catch (IllegalArgumentException | RemoteException e) {
                SmartLog.e(TAG, "error = " + e);
            }
        } else {
            SmartLog.e(TAG, "i007 not ready");
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
     * 注册事件监听
     *
     * @param observer 监听回调方
     * @return 是否成功
     */
    public boolean subscribeObserver(I007Observer observer) {
        II007Manager service = getService();
        boolean register = (service != null);
        if (register) {
            try {
                register = service.registerListener(observer);
            } catch (RemoteException | NullPointerException e) {
                register = false;
                SmartLog.e(TAG, "subscribe observer fail = " + e);
            }
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
        II007Manager service = getService();
        boolean unregister = (service != null);
        if (unregister) {
            try {
                unregister = service.unregisterListener(observer);
            } catch (RemoteException | NullPointerException e) {
                unregister = false;
                SmartLog.e(TAG, "unsubscribe observer fail = " + e);
            }
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
        II007Manager service = getService();
        boolean result = (service != null);
        if (result) {
            try {
                result = service.setFactor(factors);
            } catch (RemoteException | NullPointerException e) {
                result = false;
                SmartLog.e(TAG, "setFactor fail = " + e);
            }
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
        II007Manager service = getService();
        boolean result = (service != null);
        if (result) {
            try {
                result = service.updateFactor(factors);
            } catch (RemoteException | NullPointerException e) {
                result = false;
                SmartLog.e(TAG, "updateFactor fail = " + e);
            }
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
        II007Manager service = getService();
        boolean result = (service != null);
        if (result) {
            try {
                result = service.removeFactor(factors);
            } catch (RemoteException | NullPointerException e) {
                result = false;
                SmartLog.e(TAG, "removeFactor fail = " + e);
            }
        }

        return result;
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
