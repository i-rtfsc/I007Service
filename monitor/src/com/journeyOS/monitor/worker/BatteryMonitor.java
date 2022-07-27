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
import android.os.BatteryManager;

import com.journeyOS.i007manager.I007Battery;
import com.journeyOS.i007manager.I007Core;
import com.journeyOS.i007manager.I007Result;
import com.journeyOS.i007manager.SmartLog;
import com.journeyOS.monitor.BaseMonitor;
import com.journeyOS.monitor.MonitorManager;

/**
 * 电池监听器
 *
 * @author solo
 */
public final class BatteryMonitor extends BaseMonitor {
    private static final String TAG = BatteryMonitor.class.getSimpleName();
    private static volatile BatteryMonitor sInstance = null;
    private Context mContext;
    private BatteryBroadcastReceiver mReceiver;

    private BatteryMonitor() {
        SmartLog.d(TAG, "init");
    }

    /**
     * 获取BatteryMonitor单例
     *
     * @return BatteryMonitor
     */
    public static BatteryMonitor getInstance() {
        if (sInstance == null) {
            synchronized (BatteryMonitor.class) {
                if (sInstance == null) {
                    sInstance = new BatteryMonitor();
                }
            }
        }
        return sInstance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onInit(long factoryId) {
        mContext = I007Core.getCore().getContext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mReceiver = new BatteryBroadcastReceiver();
        mContext.registerReceiver(mReceiver, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        mContext.unregisterReceiver(mReceiver);
    }

    private class BatteryBroadcastReceiver extends BroadcastReceiver {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int level = (int) (100f * intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) / intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100));
                int pluggedIn = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);

                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                        BatteryManager.BATTERY_STATUS_UNKNOWN);

                int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
                int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);

                I007Result.Builder source = MonitorManager.getInstance().getBuilder();

                I007Battery battery = new I007Battery.Builder()
                        .setLevel(level)
                        .setStatus(status)
                        .setTemperature(temperature)
                        .setPluggedIn(pluggedIn)
                        .setHealth(health)
                        .build();

                I007Result.Builder target = source
                        .setFactoryId(mFactoryId)
                        .setBattery(battery);

                MonitorManager.getInstance().notifyResult(target);
            }

        }
    }
}
