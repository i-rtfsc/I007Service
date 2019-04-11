package com.journeyOS.i007Service.core.detect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.journeyOS.i007Service.I007Manager;
import com.journeyOS.i007Service.base.utils.Singleton;
import com.journeyOS.i007Service.core.I007Core;
import com.journeyOS.i007Service.core.NotifyManager;
import com.journeyOS.i007Service.core.SceneUtils;

public class BatteryMonitor extends Monitor {
    private static final Singleton<BatteryMonitor> gDefault = new Singleton<BatteryMonitor>() {
        @Override
        protected BatteryMonitor create() {
            return new BatteryMonitor();
        }
    };
    private Context mContext;

    private BatteryMonitor() {
        mContext = I007Core.getCore().getContext();
    }

    public static BatteryMonitor getDefault() {
        return gDefault.get();
    }

    @Override
    public void onStart() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(new BatteryBroadcastReceiver(), filter);

    }

    private String convert(int status, int level, int health, int temperature, int pluggedIn) {
        StringBuilder sb = new StringBuilder();
        sb.append(status).append(String.format("%03d", level)).append(health).append(SceneUtils.SEPARATOR)
                .append(String.format("%03d", temperature)).append(pluggedIn).append("#");
        return sb.toString();
    }

    private class BatteryBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int level = (int) (100f
                        * intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                        / intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100));
                int pluggedIn = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);

                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                        BatteryManager.BATTERY_STATUS_UNKNOWN);

                int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
                int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);

                String data = convert(status, level, health, temperature, pluggedIn);

                NotifyManager.getDefault().onFactorChanged(I007Manager.SCENE_FACTOR_BATTERY, data);
            }
        }
    }

}
