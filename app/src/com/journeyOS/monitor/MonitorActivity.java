package com.journeyOS.monitor;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.journeyOS.i007.DataResource.FACTORY;
import com.journeyOS.i007.I007Manager;
import com.journeyOS.i007.interfaces.II007Listener;


public class MonitorActivity extends Activity {
    private static final String TAG = "Monitor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_activity);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
            }
        });

        findViewById(R.id.listener).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener();
            }
        });
    }


    private void init() {
        if (!I007Manager.isServiceEnabled()) {
            I007Manager.openSettingsAccessibilityService();
        }
    }

    private void listener() {
        final long factors = I007Manager.SCENE_FACTOR_APP
                | I007Manager.SCENE_FACTOR_LCD
                | I007Manager.SCENE_FACTOR_NET
                | I007Manager.SCENE_FACTOR_HEADSET
                | I007Manager.SCENE_FACTOR_BATTERY;

        I007Manager.registerListener(factors, new II007Listener.Stub() {
            @Override
            public void onSceneChanged(long factorId, String status, String packageName) throws RemoteException {
                Log.d(TAG, "on scene changed factorId = [" + factorId + "], status = [" + status + "]");
                FACTORY factory = I007Manager.getFactory(factorId);
                switch (factory) {
                    case APP:
                        Log.d(TAG, "app has been changed, packageName = [" + packageName + "]" +
                                ", app type = [" + I007Manager.getApp(status) + "]" +
                                ", is game = [" + I007Manager.isGame2(status) + "]");
                        break;
                    case LCD:
                        Log.d(TAG, "lcd has been changed, packageName = [" + packageName + "]" +
                                ", isScreenOn screen status = [" + I007Manager.isScreenOn(status) + "]");
                        break;
                    case NET:
                        Log.d(TAG, "net has been changed, packageName = [" + packageName + "]" +
                                ", net status = [" + I007Manager.getNetWork(status) + "]");
                        break;
                    case HEADSET:
                        Log.d(TAG, "headset has been changed, packageName = [" + packageName + "]" +
                                ", headset status = [" + I007Manager.isHeadSetPlug(status) + "]");
                        break;
                    case BATTERY:
                        Log.d(TAG, "battery has been changed, packageName = [" + packageName + "]" +
                                ", battery status = [" + I007Manager.getBatteryStatus(status) + "]" +
                                ", battery level = [" + I007Manager.getBatteryLevel(status) + "]" +
                                ", battery temperature = [" + I007Manager.getBatteryTemperature(status) + "]" +
                                ", battery health = [" + I007Manager.getBatteryHealth(status) + "]" +
                                ", battery plugged = [" + I007Manager.getBatteryPlugged(status) + "]");
                        break;
                }
            }
        });

    }

}
