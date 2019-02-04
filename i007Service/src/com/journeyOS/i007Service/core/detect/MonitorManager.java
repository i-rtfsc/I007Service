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

package com.journeyOS.i007Service.core.detect;

import com.journeyOS.i007Service.base.utils.DebugUtils;

import java.util.ArrayList;


public class MonitorManager {
    private static final String TAG = MonitorManager.class.getSimpleName();

    private static final MonitorManager sInstance = new MonitorManager();

    private final ArrayList<Monitor> mMonitors = new ArrayList<Monitor>();

    private MonitorManager() {
    }

    public static MonitorManager getInstance() {
        return sInstance;
    }

    public void startMonitors() {
        final int size = mMonitors.size();
        for (int i = 0; i < size; i++) {
            final Monitor monitor = mMonitors.get(i);
            try {
                monitor.onStart();
            } catch (Exception e) {
                e.printStackTrace();
                DebugUtils.e(TAG, "Failed to start monitor " + monitor.getClass().getName());
            }
        }
    }

    private void addMonitor(Monitor monitor) {
        try {
            mMonitors.add(monitor);
        } catch (Throwable e) {
            e.printStackTrace();
            DebugUtils.e(TAG, "Failed to add monitor " + monitor.getClass().getName());
        }
    }

    public void init() {
        addMonitor(AccessibilityMonitor.getDefault());
        addMonitor(LCDMonitor.getDefault());
        addMonitor(NetworkMonitor.getDefault());
        addMonitor(HeadSetMonitor.getDefault());
        addMonitor(BatteryMonitor.getDefault());
        startMonitors();
    }
}
