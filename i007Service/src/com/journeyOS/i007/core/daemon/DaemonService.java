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

package com.journeyOS.i007.core.daemon;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.journeyOS.i007.I007Manager;
import com.journeyOS.i007.base.util.DebugUtils;
import com.journeyOS.i007.core.I007Core;
import com.journeyOS.i007.service.I007Register;
import com.journeyOS.i007.service.I007Service;
import com.journeyOS.litetask.TaskScheduler;


public class DaemonService extends Service {
    private static final String TAG = "Daemon-S";
    private static final boolean DEBUG = true;


    public static void running(Context context) {
        if (DEBUG) DebugUtils.d(TAG, "daemon service will be running!");
        context.startService(new Intent(context, DaemonService.class));
        I007Service.systemReady();
        I007Register.systemReady();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DEBUG) DebugUtils.d(TAG, "daemon service die!");
//        running(this);//can't don't andorid version > 8.0
        I007Manager.keepAlive(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Notification notification = new Notification();
            notification.flags = Notification.FLAG_NO_CLEAR
                    | Notification.FLAG_ONGOING_EVENT
                    | Notification.FLAG_FOREGROUND_SERVICE;
            startForeground(0, notification);
        } catch (Throwable e) {
            // Ignore
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }
}
