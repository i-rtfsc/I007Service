/*
 * Copyright (c) 2018 anqi.huang@outlook.com
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

package com.journeyOS.i007.core;

import android.util.ArrayMap;

import com.journeyOS.i007.base.util.DebugUtils;
import com.journeyOS.i007.base.util.Singleton;
import com.journeyOS.i007.core.clients.ClientSession;

import java.util.Map;


public class NotifyManager<T> {
    private static final String TAG = NotifyManager.class.getSimpleName();
    private static String sPackageName;
    private Object mLock = new Object();

    /**
     * 第一组：DLN4E
     * 1）第一个字符：表示APP类型
     * 2）第二个字符：表示屏幕亮还是灭
     * 3）第三个字符：表示是否有网
     * 4）第四个字符：表示网络类型：Wi-Fi、4G、3G、2G
     * 5）第五个字符：表示是否连接耳机
     * 第二组：11001
     * 1）第一个字符：表示电池状态
     *  - BatteryManager.BATTERY_STATUS_UNKNOWN
     *  - BatteryManager.BATTERY_STATUS_CHARGING
     *  - BatteryManager.BATTERY_STATUS_DISCHARGING
     *  - BatteryManager.BATTERY_STATUS_FULL
     *  - BatteryManager.BATTERY_STATUS_NOT_CHARGING
     * 2）第二个到第四字符：表示电池电量
     * 3）最后一个表示电池健康度：
     *  - BatteryManager.BATTERY_HEALTH_UNKNOWN
     *  - BatteryManager.BATTERY_HEALTH_GOOD
     *  - BatteryManager.BATTERY_HEALTH_OVERHEAT
     *  - BatteryManager.BATTERY_HEALTH_DEAD
     *  - BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE
     *  - BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE
     *  - BatteryManager.BATTERY_HEALTH_COLD
     * 第二组：0362#
     * 1）第一个到第三字符：表示电池温度
     * 2）第四个字符：表示当前手机使用的是哪里的电源
     *  - BatteryManager.BATTERY_PLUGGED_AC
     *  - BatteryManager.BATTERY_PLUGGED_USB
     */
    private static final String DEFAULLT_STATE = "DLN4E-11001-0362#";
    private static String sState = DEFAULLT_STATE;

    public static final int POS_FISRT = 0;
    public static final int POS_SECOND = 1;
    public static final int POS_THIRD = 2;

    public static final int POS_FISRT_APP = 0;
    public static final int POS_FISRT_LCD = 1;
    public static final int POS_FISRT_NET = 2;
    public static final int POS_FISRT_NETTYPE = 3;
    public static final int POS_FISRT_HEADSET = 4;

    public static final int POS_SECOND_BATTERY_STATUS = 0;
    public static final int POS_SECOND_BATTERY_LEVEL_START = 1;
    public static final int POS_SECOND_BATTERY_LEVEL_END = 3 + 1;
    public static final int POS_SECOND_BATTERY_HEALTH = 4;
    public static final int POS_THIRD_BATTERY_TEMPERATURE_START = 0;
    public static final int POS_THIRD_BATTERY_TEMPERATURE_END = 2 + 1;
    public static final int POS_THIRD_BATTERY_PLUGGED = 3;

    private static final Singleton<NotifyManager> gDefault = new Singleton<NotifyManager>() {
        @Override
        protected NotifyManager create() {
            return new NotifyManager();
        }
    };

    private NotifyManager() {
    }

    public static NotifyManager getDefault() {
        return gDefault.get();
    }

    public int onFactorChanged(long factoryId, T data) {
        DebugUtils.d(TAG, "onFactorChanged() called with: factoryId = [" + factoryId + "], data = [" + data + "]");
        synchronized (mLock) {
            try {
                DebugUtils.d(TAG, "on factor changed, before state = "+sState);
                sState = SceneUtils.parseData(factoryId, (String)data, sState);
                DebugUtils.d(TAG, "on factor changed, before state = "+sState);
                ClientSession clientSession = ClientSession.getDefault();
                if (clientSession != null) {
                    clientSession.dispatchFactorEvent(factoryId, sState, sPackageName);
                    if (mMapListener != null) {
                        NotifyListener listener = mMapListener.get(factoryId);
                        if (listener != null) {
                            listener.onFactorChanged(factoryId, data);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }


    public String getPackageName() {
        return sPackageName;
    }

    public void setPackageName(String packageName) {
        this.sPackageName = packageName;
    }

    private Map<Long, NotifyListener> mMapListener = new ArrayMap<>();

    public void setOnNotifyListener(long factoryId, NotifyListener listener) {
        mMapListener.put(factoryId, listener);
    }

    public interface NotifyListener<T> {
        void onFactorChanged(long factoryId, T data);
    }
}
