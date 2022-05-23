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

package com.journeyOS.i007Service.service.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.journeyOS.common.SmartLog;
import com.journeyOS.i007Service.service.DaemonService;
import com.journeyOS.i007Service.service.I007ManagerService;
import com.journeyOS.i007Service.service.core.base.BaseContentProvider;
import com.journeyOS.i007Service.service.core.base.ServiceCache;
import com.journeyOS.i007Service.service.core.base.ServiceFetcher;
import com.journeyOS.i007manager.base.ServiceConstants;
import com.journeyOS.i007manager.base.ServiceManagerNative;

/**
 * @author solo
 */
public class I007SystemServer extends BaseContentProvider {

    private static final String TAG = I007SystemServer.class.getSimpleName();

    private final ServiceFetcher mServiceFetcher = new ServiceFetcher();

    @Override
    public boolean onCreate() {
        Context context = getContext();
        SmartLog.d(TAG, "context = " + context);
        // I007ManagerService
        I007ManagerService.systemReady(context);
        I007ManagerService ims = I007ManagerService.getService();
        ServiceCache.addService(ServiceConstants.SERVICE_I007, ims);

        Intent intent = new Intent(getContext(), DaemonService.class);
        context.startForegroundService(intent);

        return true;
    }

    @Override
    public Bundle call(@NonNull String method, String arg, Bundle extras) {
        SmartLog.d(TAG, ", method = [" + method + "], arg = [" + arg + "], extras = [" + extras + "]");
        if (method.equals(ServiceManagerNative.INIT_SERVICE)) {
            // Ensure the server process created.
            SmartLog.d(TAG, ServiceManagerNative.INIT_SERVICE + " <- " + getCallingPackage());
            return null;
        } else {
            Bundle bundle = new Bundle();
            bundle.putBinder(ServiceManagerNative.EXTRA_BINDER, mServiceFetcher);
            SmartLog.d(TAG, "get mServiceFetcher <- " + getCallingPackage());
            return bundle;
        }
    }

}
