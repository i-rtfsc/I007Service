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

package com.journeyOS.i007Service.core.service;

import android.app.Application;
import android.os.IBinder;
import android.os.RemoteException;

import com.journeyOS.i007Service.II007Engine;
import com.journeyOS.i007Service.II007Register;
import com.journeyOS.i007Service.II007Service;
import com.journeyOS.i007Service.base.Constant;
import com.journeyOS.i007Service.base.utils.DebugUtils;
import com.journeyOS.i007Service.core.I007Core;
import com.journeyOS.i007Service.core.ServiceCache;
import com.journeyOS.i007Service.core.daemon.DaemonService;

/**
 * The manager of all services which is running in I007Service Process
 */
public class ServiceManagerNative {
    private static final String TAG = ServiceManagerNative.class.getSimpleName();
    private static Application sContext = null;
    private static II007Register m007Register = null;
    private static II007Service m007Service = null;
    private static II007Engine m007Engine = null;

    public static void running() {
        if (!I007Core.getCore().isRunning()) {
            throw new IllegalStateException("I007 Core has not been run!");
        }
        sContext = I007Core.getCore().getContext();

        DaemonService.running(sContext);
    }

    private static void linkBinderDied(final IBinder binder) {
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                binder.unlinkToDeath(this, 0);
                DebugUtils.e(TAG, "Ops, the server has crashed.");
//                android.os.Process.killProcess(android.os.Process.myPid());
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
            IBinder obj = getService(Constant.I007_REGISTER);
            linkBinderDied(obj);
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
            IBinder obj = getService(Constant.I007_SERVICE);
            linkBinderDied(obj);
            if (obj != null) {
                m007Service = II007Service.Stub.asInterface(obj);
            }
            return m007Service;
        } catch (Throwable e) {

        }
        return null;
    }

    public static II007Engine getII007Engine() {
        try {
            if (m007Engine != null) {
                return m007Engine;
            }
            IBinder obj = getService(Constant.I007_ENGINE);
            linkBinderDied(obj);
            if (obj != null) {
                m007Engine = II007Engine.Stub.asInterface(obj);
            }
            return m007Engine;
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
