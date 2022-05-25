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

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.journeyOS.i007manager.I007Core;

/**
 * @author solo
 */
public class ServiceManagerNative {
    public static final String INIT_SERVICE = "i007_init";
    public static final String EXTRA_BINDER = "i007_binder";
    private static final String TAG = ServiceManagerNative.class.getSimpleName();
    private static final String SERVICE_SYNC_AUTHORITY = "com.journeyOS.i007manager.syncprovider";
    private static IServiceFetcher sFetcher;

    public synchronized static IServiceFetcher getServiceFetcher() {
        if (sFetcher == null) {
            Context context = I007Core.getCore().getContext();
            Bundle response = new ProviderCaller.Builder(context)
                    .authority(SERVICE_SYNC_AUTHORITY)
                    .methodName("@")
                    .call();
            if (response != null) {
                IBinder binder = response.getBinder(EXTRA_BINDER);
                linkBinderDied(binder);
                sFetcher = IServiceFetcher.Stub.asInterface(binder);
            }
        }
        return sFetcher;
    }

    private static void linkBinderDied(final IBinder binder) {
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                binder.unlinkToDeath(this, 0);
                Log.e(TAG, "oops, the server has crashed.");
                I007Core.getCore().setRunning(false);
                sFetcher = null;
            }
        };
        try {
            binder.linkToDeath(deathRecipient, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取服务
     *
     * @param name 服务名称
     * @return binder对象
     */
    public static IBinder getService(String name) {
        IServiceFetcher fetcher = getServiceFetcher();
        if (fetcher != null) {
            try {
                return fetcher.getService(name);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "GetService(" + name + ") return null.");
        return null;
    }

    /**
     * 新增服务
     *
     * @param name    服务名称
     * @param service binder对象
     */
    public static void addService(String name, IBinder service) {
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
    public static void removeService(String name) {
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
     * 启动服务init服务
     *
     * @param context
     */
    public static void startup(Context context) {
        new ProviderCaller.Builder(context)
                .authority(SERVICE_SYNC_AUTHORITY)
                .methodName(INIT_SERVICE)
                .call();
    }
}
