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

package com.journeyOS.i007Service.service;

import android.content.Context;
import android.os.RemoteException;

import com.journeyOS.common.SmartLog;
import com.journeyOS.framework.clients.ClientSession;
import com.journeyOS.i007manager.I007Result;
import com.journeyOS.i007manager.II007Manager;
import com.journeyOS.i007manager.II007Observer;
import com.journeyOS.monitor.MonitorManager;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author solo
 */
public class I007ManagerService extends II007Manager.Stub implements MonitorManager.OnSceneListener {
    private static final String TAG = I007ManagerService.class.getSimpleName();

    private static final AtomicReference<I007ManagerService> sService = new AtomicReference<>();

    public static I007ManagerService getService() {
        return sService.get();
    }

    public static void systemReady(Context context) {
        SmartLog.d(TAG, "I007 manager service system ready.");
        if (sService.get() == null) {
            new I007ManagerService().onCreate(context);
        }
    }

    public void onCreate(Context context) {
        SmartLog.d(TAG, "I007 manager service running...");
        sService.set(this);
        MonitorManager.getInstance().registerListener(this);
    }

    @Override
    public boolean registerListener(II007Observer listener) throws RemoteException {
        return ClientSession.getInstance().insertToCategory(listener);
    }

    @Override
    public boolean unregisterListener(II007Observer listener) throws RemoteException {
        ClientSession.getInstance().removeFromCategory(listener);
        return true;
    }

    @Override
    public boolean setFactor(long factors) throws RemoteException {
        MonitorManager.getInstance().start(factors);
        return ClientSession.getInstance().setFactorToCategory(factors);
    }

    @Override
    public boolean updateFactor(long factors) throws RemoteException {
        MonitorManager.getInstance().start(factors);
        return ClientSession.getInstance().updateFactorToCategory(factors);
    }

    @Override
    public boolean removeFactor(long factors) throws RemoteException {
        if (ClientSession.getInstance().checkFactorFromCategory(factors)) {
            MonitorManager.getInstance().stop(factors);
        }

        return ClientSession.getInstance().removeFactorFromCategory(factors);
    }

    @Override
    public void onChanged(I007Result result) {
        ClientSession.getInstance().dispatchFactorEvent(result);
    }
}
