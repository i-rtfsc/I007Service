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

package com.journeyOS.monitor;

import android.util.Log;

import com.journeyOS.i007manager.I007Manager;
import com.journeyOS.i007manager.I007Result;
import com.journeyOS.i007manager.SmartLog;
import com.journeyOS.monitor.worker.AccessibilityBaseMonitor;
import com.journeyOS.monitor.worker.BatteryBaseMonitor;
import com.journeyOS.monitor.worker.LcdBaseMonitor;
import com.journeyOS.monitor.worker.NetworkBaseMonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author solo
 */
public final class MonitorManager {
    private static final String TAG = MonitorManager.class.getSimpleName();
    private static volatile MonitorManager sInstance = null;
    private I007Result.Builder mBuilder = new I007Result.Builder();
    private ConcurrentHashMap<Long, BaseMonitor> mMonitors = new ConcurrentHashMap<>();
    private List<OnSceneListener> mListeners = new ArrayList<OnSceneListener>();

    private MonitorManager() {
    }

    /**
     * 获取MonitorManager单例
     *
     * @return MonitorManager实例
     */
    public static MonitorManager getInstance() {
        if (sInstance == null) {
            synchronized (MonitorManager.class) {
                if (sInstance == null) {
                    sInstance = new MonitorManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取 I007Result.Builder
     *
     * @return I007Result.Builder
     */
    public I007Result.Builder getBuilder() {
        return mBuilder;
    }

    private void setBuilder(I007Result.Builder builder) {
        this.mBuilder = builder;
    }

    /**
     * 因为当前业务需求，暂时不暴露接口出去（改正start的时候init）
     *
     * @param factors 场景因子
     * @return 成功或者失败
     */
    protected boolean init(long factors) {
        if ((factors & I007Manager.SCENE_FACTOR_APP) != 0) {
            BaseMonitor monitor = mMonitors.get(I007Manager.SCENE_FACTOR_APP);
            if (monitor == null) {
                monitor = AccessibilityBaseMonitor.getInstance();
                monitor.init(I007Manager.SCENE_FACTOR_APP);
                mMonitors.put(I007Manager.SCENE_FACTOR_APP, monitor);
            }
        }

        if ((factors & I007Manager.SCENE_FACTOR_LCD) != 0) {
            BaseMonitor monitor = mMonitors.get(I007Manager.SCENE_FACTOR_LCD);
            if (monitor == null) {
                monitor = LcdBaseMonitor.getInstance();
                monitor.init(I007Manager.SCENE_FACTOR_LCD);
                mMonitors.put(I007Manager.SCENE_FACTOR_LCD, monitor);
            }
        }

        if ((factors & I007Manager.SCENE_FACTOR_NET) != 0) {
            BaseMonitor monitor = mMonitors.get(I007Manager.SCENE_FACTOR_NET);
            if (monitor == null) {
                monitor = NetworkBaseMonitor.getInstance();
                monitor.init(I007Manager.SCENE_FACTOR_NET);
                mMonitors.put(I007Manager.SCENE_FACTOR_NET, monitor);
            }
        }

        if ((factors & I007Manager.SCENE_FACTOR_HEADSET) != 0) {
            //TODO
            SmartLog.i(TAG, "I007Manager.SCENE_FACTOR_HEADSET");
        }

        if ((factors & I007Manager.SCENE_FACTOR_BATTERY) != 0) {
            BaseMonitor monitor = mMonitors.get(I007Manager.SCENE_FACTOR_BATTERY);
            if (monitor == null) {
                monitor = BatteryBaseMonitor.getInstance();
                monitor.init(I007Manager.SCENE_FACTOR_BATTERY);
                mMonitors.put(I007Manager.SCENE_FACTOR_BATTERY, monitor);
            }
        }
        return true;
    }

    /**
     * 启动monitor
     *
     * @param factors 场景因子
     * @return 是否成功
     */
    public boolean start(long factors) {
        init(factors);
        boolean result = false;
        if ((factors & I007Manager.SCENE_FACTOR_APP) != 0) {
            BaseMonitor monitor = mMonitors.get(I007Manager.SCENE_FACTOR_APP);
            result = monitor.start();
        }

        if ((factors & I007Manager.SCENE_FACTOR_LCD) != 0) {
            BaseMonitor monitor = mMonitors.get(I007Manager.SCENE_FACTOR_LCD);
            result = monitor.start();
        }

        if ((factors & I007Manager.SCENE_FACTOR_NET) != 0) {
            BaseMonitor monitor = mMonitors.get(I007Manager.SCENE_FACTOR_NET);
            result = monitor.start();
        }

        if ((factors & I007Manager.SCENE_FACTOR_HEADSET) != 0) {
            //TODO
            SmartLog.i(TAG, "I007Manager.SCENE_FACTOR_HEADSET");
        }

        if ((factors & I007Manager.SCENE_FACTOR_BATTERY) != 0) {
            BaseMonitor monitor = mMonitors.get(I007Manager.SCENE_FACTOR_BATTERY);
            result = monitor.start();
        }
        return result;
    }

    /**
     * 暂停monitor
     *
     * @param factors 场景因子
     * @return 是否成功
     */
    public boolean stop(long factors) {
        boolean result = false;
        if ((factors & I007Manager.SCENE_FACTOR_APP) != 0) {
            BaseMonitor monitor = mMonitors.get(I007Manager.SCENE_FACTOR_APP);
            result = monitor.stop();
        }

        if ((factors & I007Manager.SCENE_FACTOR_LCD) != 0) {
            BaseMonitor monitor = mMonitors.get(I007Manager.SCENE_FACTOR_LCD);
            result = monitor.stop();
        }

        if ((factors & I007Manager.SCENE_FACTOR_NET) != 0) {
            BaseMonitor monitor = mMonitors.get(I007Manager.SCENE_FACTOR_NET);
            result = monitor.stop();
        }

        if ((factors & I007Manager.SCENE_FACTOR_HEADSET) != 0) {
            //TODO
            SmartLog.i(TAG, "I007Manager.SCENE_FACTOR_HEADSET");
        }

        if ((factors & I007Manager.SCENE_FACTOR_BATTERY) != 0) {
            BaseMonitor monitor = mMonitors.get(I007Manager.SCENE_FACTOR_BATTERY);
            result = monitor.stop();
        }
        return result;
    }

    /**
     * 通知结果
     *
     * @param builder I007Result.Builder
     */
    public synchronized void notifyResult(I007Result.Builder builder) {
        setBuilder(builder);
        I007Result result = builder.build();
        for (OnSceneListener listener : mListeners) {
            listener.onChanged(result);
        }
    }

    /**
     * Registers a callback to be invoked on voice command result.
     *
     * @param listener The callback that will run.
     */
    public void registerListener(OnSceneListener listener) {
        if (listener == null) {
            Log.d(TAG, "listener should not be null");
            return;
        }

        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    /**
     * Unregisters a previous callback.
     *
     * @param listener The callback that should be unregistered.
     * @see #registerListener
     */
    public void unregisterListener(OnSceneListener listener) {
        if (listener == null) {
            Log.d(TAG, "listener should not be null");
            return;
        }

        if (mListeners.contains(listener)) {
            mListeners.remove(listener);
        }
    }

    /**
     * 场景回调
     */
    public interface OnSceneListener {
        /**
         * 结果回调函数
         *
         * @param result 结果
         */
        void onChanged(I007Result result);
    }
}