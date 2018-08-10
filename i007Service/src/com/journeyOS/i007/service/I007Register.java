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

package com.journeyOS.i007.service;

import android.os.IBinder;
import android.os.RemoteException;

import com.journeyOS.i007.base.constants.ServiceNameConstants;
import com.journeyOS.i007.base.util.DebugUtils;
import com.journeyOS.i007.core.clients.ClientSession;
import com.journeyOS.i007.core.service.ServiceManagerNative;
import com.journeyOS.i007.interfaces.II007Listener;
import com.journeyOS.i007.interfaces.II007Register;

import java.util.concurrent.atomic.AtomicReference;


public class I007Register extends II007Register.Stub {

    private static final String TAG = I007Register.class.getSimpleName();

    private static final AtomicReference<I007Register> sService = new AtomicReference<>();

    public static I007Register getService() {
        return sService.get();
    }

    private I007Register() {
        sService.set(this);
    }

    public static void systemReady() {
        try {
            IBinder iBinder = ServiceManagerNative.getService(ServiceNameConstants.I007_REGISTER);
            if (iBinder == null) {
                I007Register i007Service = new I007Register();
                ServiceManagerNative.addService(ServiceNameConstants.I007_REGISTER, i007Service);
            } else {
                DebugUtils.w(TAG, "service " + ServiceNameConstants.I007_REGISTER + " already added, it cannot be added once more...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            DebugUtils.e(TAG, "Exception in add service " + ServiceNameConstants.I007_REGISTER + ": " + e.getMessage());
        }
    }

    @Override
    public boolean registerListener(long factors, II007Listener listener) throws RemoteException {
        if (listener == null || factors == 0) {
            DebugUtils.i(TAG, "listener or factors is null");
            return false;
        }

        try {
            ClientSession.getDefault().insertToCategory(factors, listener);
        } catch (Exception e) {
            DebugUtils.e(TAG, "Exception in register listener : " + factors + ", " + listener);
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void unregisterListener(II007Listener listener) throws RemoteException {
        if (listener == null) {
            return;
        }

        try {
            ClientSession.getDefault().removeFromCategory(listener);
        } catch (Exception e) {
            e.printStackTrace();
            DebugUtils.w(TAG, "Exception in unregisterLister : " + listener);
        }
    }
}
