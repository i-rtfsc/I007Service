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

package com.journeyOS.i007manager.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.journeyOS.i007manager.I007Core;
import com.journeyOS.i007manager.ServerLifecycle;
import com.journeyOS.i007manager.SmartLog;

/**
 * @author solo
 */
public class ServiceManagerNative implements ServiceConnection {
    /**
     * i007 服务包名
     */
    public static final String I007_SERVICE_PACKAGE = "com.journeyOS.i007Service";
    private static final String TAG = ServiceManagerNative.class.getSimpleName();
    private static final String I007_SERVICE_SERVICE_AIDL = "com.journeyOS.i007Service.I007SystemServer";
    private static volatile ServiceManagerNative sInstance = null;
    private IServiceFetcher sFetcher;
    private boolean isRunning = false;

    private ServerLifecycleManager mSlm = null;

    private ServiceManagerNative() {
        mSlm = new ServerLifecycleManager();
    }

    /**
     * 获取 ServiceManagerNative 单例
     *
     * @return ServiceManagerNative 实例
     */
    public static ServiceManagerNative getInstance() {
        if (sInstance == null) {
            synchronized (ServiceManagerNative.class) {
                if (sInstance == null) {
                    sInstance = new ServiceManagerNative();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        SmartLog.d(TAG, "I007 System Server running...");
        try {
            sFetcher = IServiceFetcher.Stub.asInterface(service);
            isRunning = true;
            I007Core.getCore().setRunning(true);
            linkBinderDied(service);
            mSlm.notifyStarted();
        } catch (IllegalArgumentException e) {
            SmartLog.e(TAG, "start I007 System Server error = " + e);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        SmartLog.d(TAG, "I007 System Server has crashed...");
        isRunning = false;
        sFetcher = null;
        I007Core.getCore().setRunning(false);
        mSlm.notifyDied();
    }

    /**
     * 启动服务init服务
     *
     * @param context
     */
    public void startup(Context context) {
        if (!isRunning) {
            Intent intentService = new Intent();
            intentService.setPackage(I007_SERVICE_PACKAGE);
            intentService.setAction(I007_SERVICE_SERVICE_AIDL);
            boolean result = context.bindService(intentService, this, Context.BIND_AUTO_CREATE);
            SmartLog.d(TAG, "bind I007 System Server result = [" + result + "]");
        }
    }

    /**
     * 获取服务
     *
     * @return IServiceFetcher
     */
    private IServiceFetcher getServiceFetcher() {
        if (sFetcher == null) {
            //TODO
            /**
             * 每次调接口的时候如果服务不存在，都告诉客户端是否合理？
             */
            mSlm.notifyDied();
        }
        return sFetcher;
    }

    /**
     * 获取服务
     *
     * @param name 服务名称
     * @return binder对象
     */
    public IBinder getService(String name) {
        IServiceFetcher fetcher = getServiceFetcher();
        if (fetcher != null) {
            try {
                return fetcher.getService(name);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        SmartLog.e(TAG, "GetService(" + name + ") return null.");
        return null;
    }

    /**
     * 新增服务
     *
     * @param name    服务名称
     * @param service binder对象
     */
    public void addService(String name, IBinder service) {
        IServiceFetcher fetcher = getServiceFetcher();
        if (fetcher != null) {
            try {
                fetcher.addService(name, service);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除（关闭）服务
     *
     * @param name 服务名称
     */
    public void removeService(String name) {
        IServiceFetcher fetcher = getServiceFetcher();
        if (fetcher != null) {
            try {
                fetcher.removeService(name);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Registers a callback to be invoked on voice command result.
     *
     * @param listener The callback that will run.
     */
    public void registerListener(ServerLifecycle listener) {
        mSlm.registerListener(listener);
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

    private void linkBinderDied(final IBinder binder) {
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                binder.unlinkToDeath(this, 0);
                SmartLog.e(TAG, "oops, the server has crashed.");
                I007Core.getCore().setRunning(false);
                sFetcher = null;
                mSlm.notifyDied();
            }
        };
        try {
            binder.linkToDeath(deathRecipient, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
