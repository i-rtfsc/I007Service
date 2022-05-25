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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.journeyOS.common.SmartLog;
import com.journeyOS.i007manager.I007Core;
import com.journeyOS.i007manager.I007Net;
import com.journeyOS.i007manager.I007Result;
import com.journeyOS.monitor.Monitor;
import com.journeyOS.monitor.MonitorManager;

/**
 * 网络监听器
 *
 * @author solo
 */
public class NetworkMonitor extends Monitor {
    private static final String TAG = NetworkMonitor.class.getSimpleName();
    private volatile static NetworkMonitor INSTANCE = null;
    private NetworkBroadcastReceiver mReceiver;

    private NetworkMonitor() {
        SmartLog.d(TAG, "init");
    }

    /**
     * 获取 NetworkMonitor 单例
     *
     * @return NetworkMonitor
     */
    public static NetworkMonitor getInstance() {
        if (INSTANCE == null) {
            synchronized (NetworkMonitor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NetworkMonitor();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    protected void onInit(long factoryId) {
    }

    @Override
    protected void onStart() {
        Context context = I007Core.getCore().getContext();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new NetworkBroadcastReceiver();
        context.registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onStop() {
        Context context = I007Core.getCore().getContext();
        context.unregisterReceiver(mReceiver);
    }

    private class NetworkBroadcastReceiver extends BroadcastReceiver {

        private boolean isAvailable(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo net = cm.getActiveNetworkInfo();
            if (net != null && net.isAvailable()) {
                return true;
            } else {
                return false;
            }
        }

        private int getNetworkType(Context context) {
            int type = -1;
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo net = cm.getActiveNetworkInfo();
                if (net != null) {
                    type = net.getType();
                }
            }

            switch (type) {
                case ConnectivityManager.TYPE_WIFI:
                    return I007Net.Type.NET_WIFI;
                case ConnectivityManager.TYPE_MOBILE:
                case ConnectivityManager.TYPE_MOBILE_DUN:
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    int teleType = tm.getNetworkType();
                    switch (teleType) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return I007Net.Type.NET_2G;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return I007Net.Type.NET_3G;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return I007Net.Type.NET_4G;
                        case TelephonyManager.NETWORK_TYPE_NR:
                            return I007Net.Type.NET_5G;
                        default:
                            return I007Net.Type.NET_UNKNOWN;
                    }
                default:
                    return I007Net.Type.NET_UNKNOWN;
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                boolean isAvailable = isAvailable(context);
                int type = getNetworkType(context);

                I007Result.Builder source = MonitorManager.getInstance().getBuilder();

                I007Net net = new I007Net.Builder()
                        .setAvailable(isAvailable)
                        .setType(type)
                        .build();

                I007Result.Builder target = source
                        .setFactoryId(mFactoryId)
                        .setNet(net);

                MonitorManager.getInstance().notifyResult(target);
            }
        }
    }
}
