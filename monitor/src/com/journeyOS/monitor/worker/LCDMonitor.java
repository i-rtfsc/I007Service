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

package com.journeyOS.monitor.worker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.journeyOS.common.SmartLog;
import com.journeyOS.i007manager.I007Core;
import com.journeyOS.i007manager.I007Result;
import com.journeyOS.i007manager.I007Screen;
import com.journeyOS.monitor.Monitor;
import com.journeyOS.monitor.MonitorManager;

/**
 * @author solo
 */
public class LCDMonitor extends Monitor {
    private static final String TAG = LCDMonitor.class.getSimpleName();

    private volatile static LCDMonitor INSTANCE = null;
    private Context mContext;
    private ScreenBroadcastReceiver mReceiver;


    private LCDMonitor() {
        SmartLog.d(TAG, "init");
    }

    public static LCDMonitor getInstance() {
        if (INSTANCE == null) {
            synchronized (LCDMonitor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LCDMonitor();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    protected void onInit(long factoryId) {
        mContext = I007Core.getCore().getContext();
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenBroadcastReceiver();
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onStop() {
        mContext.unregisterReceiver(mReceiver);
    }

    private class ScreenBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int status = I007Screen.Status.UNKNOWN;
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                status = I007Screen.Status.ON;
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                status = I007Screen.Status.OFF;
            }

            I007Result.Builder source = MonitorManager.getInstance().getBuilder();
            I007Screen screen = new I007Screen.Builder()
                    .setStatus(status)
                    .build();
            I007Result.Builder target = source
                    .setFactoryId(mFactoryId)
                    .setScreen(screen);
            MonitorManager.getInstance().notifyResult(target);
        }
    }
}
