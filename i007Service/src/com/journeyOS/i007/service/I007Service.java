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
import android.text.TextUtils;

import com.journeyOS.i007.base.constants.ServiceNameConstants;
import com.journeyOS.i007.base.util.DebugUtils;
import com.journeyOS.i007.core.detect.PackageNameMonitor;
import com.journeyOS.i007.core.service.ServiceManagerNative;
import com.journeyOS.i007.database.App;
import com.journeyOS.i007.database.DatabaseManager;
import com.journeyOS.i007.interfaces.II007Service;

import java.util.concurrent.atomic.AtomicReference;


public class I007Service extends II007Service.Stub {

    private static final String TAG = I007Service.class.getSimpleName();

    private static final AtomicReference<I007Service> sService = new AtomicReference<>();

    private I007Service() {
        sService.set(this);
    }

    public static I007Service getService() {
        return sService.get();
    }

    public static void systemReady() {
        try {
            IBinder iBinder = ServiceManagerNative.getService(ServiceNameConstants.I007_SERVICE);
//            ServiceManagerNative.linkBinderDied(iBinder);
            if (iBinder == null) {
                I007Service i007Service = new I007Service();
                ServiceManagerNative.addService(ServiceNameConstants.I007_SERVICE, i007Service);
            } else {
                DebugUtils.w(TAG, "service " + ServiceNameConstants.I007_SERVICE + " already added, it cannot be added once more...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            DebugUtils.e(TAG, "Exception in add service " + ServiceNameConstants.I007_SERVICE + ": " + e.getMessage());
        }
    }

    @Override
    public boolean isGame(String packageName) throws RemoteException {
        App app = DatabaseManager.getDefault().queryApp(packageName);
        if (PackageNameMonitor.GAME.equals(app.type)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean addGame(String source, String packageName) throws RemoteException {
        if (TextUtils.isEmpty(source) || TextUtils.isEmpty(packageName)) {
            return false;
        }

        return DatabaseManager.getDefault().addApp(packageName, PackageNameMonitor.GAME);
    }

    @Override
    public boolean removeGame(String source, String packageName) throws RemoteException {
        if (TextUtils.isEmpty(source) || TextUtils.isEmpty(packageName)) {
            return false;
        }

        return DatabaseManager.getDefault().removeApp(packageName);
    }

}
