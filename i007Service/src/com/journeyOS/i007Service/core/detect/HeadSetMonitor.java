package com.journeyOS.i007Service.core.detect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.journeyOS.i007Service.I007Manager;
import com.journeyOS.i007Service.base.utils.DebugUtils;
import com.journeyOS.i007Service.base.utils.Singleton;
import com.journeyOS.i007Service.core.I007Core;
import com.journeyOS.i007Service.core.NotifyManager;

public class HeadSetMonitor extends Monitor {
    private static final String TAG = HeadSetMonitor.class.getSimpleName();
    private static final String CONNECT_INTENT_KEY_STATE = "state";
    private static final Singleton<HeadSetMonitor> gDefault = new Singleton<HeadSetMonitor>() {
        @Override
        protected HeadSetMonitor create() {
            return new HeadSetMonitor();
        }
    };
    private Context mContext;

    private HeadSetMonitor() {
        mContext = I007Core.getCore().getContext();
    }

    public static HeadSetMonitor getDefault() {
        return gDefault.get();
    }

    @Override
    public void onStart() {
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter != null && BluetoothProfile.STATE_CONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
                setHeadSetState(true);
            } else {
                setHeadSetState(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        mContext.registerReceiver(new HeadSetPlugBroadcastReceiver(), filter);

    }

    private void setHeadSetState(boolean isPlugged) {
        NotifyManager.getDefault().onFactorChanged(I007Manager.SCENE_FACTOR_HEADSET, isPlugged ? I007Manager.SCENE_HEADSET_STATE_ON : I007Manager.SCENE_HEADSET_STATE_OFF);
    }

    private class HeadSetPlugBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                boolean connected = intent.getIntExtra(CONNECT_INTENT_KEY_STATE, 0) != 0;
                setHeadSetState(connected);
            } else if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, BluetoothAdapter.ERROR);
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                try {
                    if (adapter != null && BluetoothProfile.STATE_CONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)
                            || state == BluetoothProfile.STATE_CONNECTED) {
                        setHeadSetState(true);
                    } else {
                        setHeadSetState(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
//                setHeadSetState(false);
                DebugUtils.d(TAG, "audio becoming noisy");
            }
        }
    }
}

