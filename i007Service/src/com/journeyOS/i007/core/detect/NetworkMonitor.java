package com.journeyOS.i007.core.detect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.journeyOS.i007.I007Manager;
import com.journeyOS.i007.base.util.Singleton;
import com.journeyOS.i007.core.I007Core;
import com.journeyOS.i007.core.NotifyManager;

public class NetworkMonitor extends Monitor {
    private Context mContext;

    private static final Singleton<NetworkMonitor> gDefault = new Singleton<NetworkMonitor>() {
        @Override
        protected NetworkMonitor create() {
            return new NetworkMonitor();
        }
    };

    private NetworkMonitor() {
        mContext = I007Core.getCore().getContext();
    }

    public static NetworkMonitor getDefault() {
        return gDefault.get();
    }

    @Override
    public void onStart() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(new NetworkBroadcastReceiver(), filter);

    }

    public boolean isAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net != null && net.isAvailable()) {
            return true;
        } else {
            return false;
        }
    }

    public String getNetworkType(Context context) {
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
                return I007Manager.SCENE_NET_STATE_TYPE_WIFI;
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
                        return I007Manager.SCENE_NET_STATE_TYPE_2G;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return I007Manager.SCENE_NET_STATE_TYPE_3G;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return I007Manager.SCENE_NET_STATE_TYPE_4G;
                    default:
                        return I007Manager.SCENE_NET_STATE_TYPE_UNKNOWN;
                }
            default:
                return I007Manager.SCENE_NET_STATE_TYPE_UNKNOWN;
        }
    }


    private class NetworkBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                boolean isAvailable = isAvailable(context);
                String type = getNetworkType(context);

                StringBuilder sb = new StringBuilder();
                sb.append(isAvailable ? I007Manager.SCENE_NET_STATE_ON : I007Manager.SCENE_NET_STATE_OFF).append(type);

                NotifyManager.getDefault().onFactorChanged(I007Manager.SCENE_FACTOR_NET, sb.toString());
            }
        }
    }


}
