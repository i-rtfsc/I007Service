/*
 * Copyright (c) 2018 anqi.huang@outlook.com
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

package com.journeyOS.i007.core.service;

import android.app.Application;
import android.os.IBinder;
import android.os.RemoteException;

import com.journeyOS.i007.base.constants.ServiceNameConstants;
import com.journeyOS.i007.base.util.DebugUtils;
import com.journeyOS.i007.core.ServiceCache;
import com.journeyOS.i007.core.daemon.DaemonService;
import com.journeyOS.i007.interfaces.II007Register;
import com.journeyOS.i007.interfaces.II007Service;

/**
 * The manager of all services which is running in I007Service Process
 */
public class ServiceManagerNative {
    private static final String TAG = ServiceManagerNative.class.getSimpleName();
    private static II007Register m007Register = null;
    private static II007Service m007Service = null;

    public static void running(Application context) {
        DaemonService.running(context);
    }

    private static void linkBinderDied(final IBinder binder) {
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                binder.unlinkToDeath(this, 0);
                DebugUtils.e(TAG, "Ops, the server has crashed.");
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        };
        try {
            binder.linkToDeath(deathRecipient, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static II007Register getI007Register() {
        try {
            if (m007Register != null) {
                return m007Register;
            }
            IBinder obj = ServiceManagerNative.getService(ServiceNameConstants.I007_REGISTER);
            ServiceManagerNative.linkBinderDied(obj);
            if (obj != null) {
                m007Register = II007Register.Stub.asInterface(obj);
            }
            return m007Register;
        } catch (Throwable e) {

        }
        return null;
    }

    public static II007Service getI007Service() {
        try {
            if (m007Service != null) {
                return m007Service;
            }
            IBinder obj = ServiceManagerNative.getService(ServiceNameConstants.I007_SERVICE);
            ServiceManagerNative.linkBinderDied(obj);
            if (obj != null) {
                m007Service = II007Service.Stub.asInterface(obj);
            }
            return m007Service;
        } catch (Throwable e) {

        }
        return null;
    }

    public static IBinder getService(String name) {
        if (name != null) {
            return ServiceCache.getService(name);
        }
        return null;
    }

    public static void addService(String name, IBinder service) {
        if (name != null && service != null) {
            ServiceCache.addService(name, service);
        }
    }

    public static void removeService(String name) {
        if (name != null) {
            ServiceCache.removeService(name);
        }
    }
}
