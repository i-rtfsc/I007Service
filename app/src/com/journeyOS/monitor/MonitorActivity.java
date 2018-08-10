package com.journeyOS.monitor;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.journeyOS.i007.I007Manager;
import com.journeyOS.i007.I007Manager.FACTORY;
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
            public void onSceneChanged(long factorId, long state, String packageName) throws RemoteException {
                Log.d(TAG, "onSceneChanged() called with: factorId = [" + factorId + "], state = [" + state + "], packageName = [" + packageName + "]");

                FACTORY factory = I007Manager.getFactory(factorId);
                switch (factory) {
                    case APP:
                        boolean isGameState = I007Manager.isGame(state);
                        boolean isGame = I007Manager.isGame(packageName);
                        Log.d(TAG, "onSceneChanged() called with: isGameState = [" + isGameState + "], isGame = [" + isGame + "], packageName = [" + packageName + "]");
                        break;
                    case LCD:
                        if ((state & I007Manager.SCENE_FACTOR_LCD_STATE_ON) != 0) {
                            Log.d(TAG, "onSceneChanged screen on");
                        } else if ((state & I007Manager.SCENE_FACTOR_LCD_STATE_OFF) != 0) {
                            Log.d(TAG, "onSceneChanged screen off");
                        }
                        break;
                }

            }

            @Override
            public void onSceneChangedJson(long factorId, String msg) throws RemoteException {
                Log.d(TAG, "onSceneChangedJson() called with: factorId = [" + factorId + "], msg = [" + msg + "]");
            }

        });
    }

}
