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

package com.journeyOS.i007Service.service.system;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.journeyOS.common.SmartLog;
import com.journeyOS.framework.service.ServiceCache;
import com.journeyOS.framework.service.ServiceFetcher;
import com.journeyOS.i007Service.service.AiManagerService;
import com.journeyOS.i007Service.service.I007ManagerService;
import com.journeyOS.i007Service.service.base.I007BaseService;
import com.journeyOS.i007manager.base.ServiceConstants;

import java.util.UUID;

/**
 * @author solo
 */
public class I007SystemServer extends I007BaseService {

    private static final String TAG = I007SystemServer.class.getSimpleName();
    private IBinder mIBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        startService(getApplicationContext());
        mIBinder = new ServiceFetcher();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SmartLog.d(TAG, "intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");
        int notificationId = UUID.randomUUID().hashCode();
        startForeground(notificationId, showForegroundService());
        return super.onStartCommand(intent, flags, startId);
    }

    private void startService(Context context) {
        // I007ManagerService
        I007ManagerService.systemReady(context);
        I007ManagerService ims = I007ManagerService.getService();
        ServiceCache.addService(ServiceConstants.SERVICE_I007, ims);

        // AiManagerService
        AiManagerService.systemReady(context);
        AiManagerService ams = AiManagerService.getService();
        ServiceCache.addService(ServiceConstants.SERVICE_AI, ams);
    }

}
