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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import com.journeyOS.i007manager.I007Core;
import com.journeyOS.i007manager.I007HeadSet;
import com.journeyOS.i007manager.I007Result;
import com.journeyOS.i007manager.SmartLog;
import com.journeyOS.monitor.BaseMonitor;
import com.journeyOS.monitor.MonitorManager;

/**
 * 电池监听器
 *
 * @author solo
 */
public final class HeadSetMonitor extends BaseMonitor {
    private static final String TAG = HeadSetMonitor.class.getSimpleName();
    private static final String CONNECT_INTENT_KEY_STATE = "state";
    private static volatile HeadSetMonitor sInstance = null;
    private Context mContext;
    private HeadSetBroadcastReceiver mReceiver;
    private int mState = I007HeadSet.HEADSET_OFF;

    private HeadSetMonitor() {
        SmartLog.d(TAG, "init");
    }

    /**
     * 获取 HeadSetMonitor 单例
     *
     * @return HeadSetMonitor
     */
    public static HeadSetMonitor getInstance() {
        if (sInstance == null) {
            synchronized (HeadSetMonitor.class) {
                if (sInstance == null) {
                    sInstance = new HeadSetMonitor();
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
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter != null && BluetoothProfile.STATE_CONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
                mState = I007HeadSet.HEADSET_ON;
            } else {
                mState = I007HeadSet.HEADSET_OFF;
            }
            notifyResult(mState);
        } catch (Exception e) {
            e.printStackTrace();
        }

        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        mReceiver = new HeadSetBroadcastReceiver();
        mContext.registerReceiver(mReceiver, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        mContext.unregisterReceiver(mReceiver);
    }

    private void notifyResult(int state) {
        I007Result.Builder source = MonitorManager.getInstance().getBuilder();

        I007HeadSet headSet = new I007HeadSet.Builder()
                .setStatus(state)
                .build();

        I007Result.Builder target = source
                .setFactoryId(mFactoryId)
                .setHeadSet(headSet);

        MonitorManager.getInstance().notifyResult(target);
    }

    private class HeadSetBroadcastReceiver extends BroadcastReceiver {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                boolean connected = intent.getIntExtra(CONNECT_INTENT_KEY_STATE, 0) != 0;
                SmartLog.d(TAG, "headset plug = [" + connected + "]");
                if (connected) {
                    mState = I007HeadSet.HEADSET_ON;
                } else {
                    mState = I007HeadSet.HEADSET_OFF;
                }
                notifyResult(mState);
            } else if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, BluetoothAdapter.ERROR);
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                try {
                    boolean connected = adapter != null && BluetoothProfile.STATE_CONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET) || state == BluetoothProfile.STATE_CONNECTED;
                    if (connected) {
                        mState = I007HeadSet.HEADSET_ON;
                    } else {
                        mState = I007HeadSet.HEADSET_OFF;
                    }
                    SmartLog.d(TAG, "connection state changed = [" + mState + "], state = [" + state + "]");
                    notifyResult(mState);
                } catch (Exception e) {
                    e.printStackTrace();
                    SmartLog.d(TAG, "exception while get action_connection_state_changed");
                }

            } else if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                mState = I007HeadSet.HEADSET_OFF;
                SmartLog.d(TAG, "audio becoming noisy");
            }
        }

    }
}
