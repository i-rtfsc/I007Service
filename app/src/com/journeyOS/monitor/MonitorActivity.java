package com.journeyOS.monitor;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.journeyOS.i007.I007Manager;
import com.journeyOS.i007.I007Manager.FACTORY;
import com.journeyOS.i007.base.util.JsonHelper;
import com.journeyOS.i007.data.AppInfo;
import com.journeyOS.i007.data.LcdInfo;
import com.journeyOS.i007.interfaces.II007Listener;


public class MonitorActivity extends Activity {
    private static final String TAG = MonitorActivity.class.getSimpleName();

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
        final long factors = I007Manager.SCENE_FACTOR_APP | I007Manager.SCENE_FACTOR_LCD;

        I007Manager.registerListener(factors, new II007Listener.Stub() {
            @Override
            public void onSceneChangedJson(long factorId, String msg) throws RemoteException {
                Log.d(TAG, "on scene changed factorId = [" + factorId + "], json msg = [" + msg + "]");
                FACTORY factory = I007Manager.getFactory(factorId);
                switch (factory) {
                    case APP:
                        AppInfo appInfo = JsonHelper.fromJson(msg, AppInfo.class);
                        boolean isGameState = I007Manager.isGame(appInfo.state);
                        Log.d(TAG, "on scene changed, is game by state = [" + isGameState + "], running packageName = [" + appInfo.packageName + "]");
                        break;
                    case LCD:
                        LcdInfo lcdInfo = JsonHelper.fromJson(msg, LcdInfo.class);
                        boolean isGame = I007Manager.isGame(lcdInfo.packageName);
                        Log.d(TAG, "on scene changed, is game by packageName = [" + isGame + "], running packageName = [" + lcdInfo.packageName + "]");
                        if ((lcdInfo.state & I007Manager.SCENE_FACTOR_LCD_STATE_ON) != 0) {
                            Log.d(TAG, "on scene changed, screen on");
                        } else if ((lcdInfo.state & I007Manager.SCENE_FACTOR_LCD_STATE_OFF) != 0) {
                            Log.d(TAG, "on scene changed, screen off");
                        }
                        break;
                }
            }
        });
    }

}
