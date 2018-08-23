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

package com.journeyOS.i007.base.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.List;


public class AppUtils {
    private static final String TAG = AppUtils.class.getSimpleName();
    private static final String ACCESSIBILITY_SERVICES = "com.journeyOS.i007/com.journeyOS.i007.detect.ActivityService";

    //<uses-permission android:name="android.permission.WRITE_SECURE_SETTINGS" />
    @Deprecated
    public static void autoEnableAccessibilityService(Context context) {
        int enabled = 0;
        boolean found = false;
        try {
            enabled = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
            DebugUtils.d(TAG, "enabled = " + enabled);
        } catch (Settings.SettingNotFoundException e) {
            DebugUtils.e(TAG, "accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter sCS = new TextUtils.SimpleStringSplitter(':');
        if (enabled == 1) {
            String settingValue = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = sCS;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICES)) {
                        DebugUtils.d(TAG, "we've found the correct accessibility is switched on!");
                        found = true;
                    }
                }
            }
        } else {
            DebugUtils.v(TAG, "accessibility is disabled");
            Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 1);
        }

        if (!found) {
            String settingValue = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                Settings.Secure.putString(context.getContentResolver(),
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                        ACCESSIBILITY_SERVICES + ":" + settingValue);
            } else {
                Settings.Secure.putString(context.getContentResolver(),
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                        ACCESSIBILITY_SERVICES);
            }
        }
    }

    public static void openSettingsAccessibilityService(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isServiceEnabled(Context context) {
        int enabled = 0;
        boolean found = false;
        try {
            enabled = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
            DebugUtils.d(TAG, "enabled = " + enabled);
        } catch (Settings.SettingNotFoundException e) {
            DebugUtils.e(TAG, "accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter sCS = new TextUtils.SimpleStringSplitter(':');
        if (enabled == 1) {
            String settingValue = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = sCS;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICES)) {
                        DebugUtils.d(TAG, "we've found the correct accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            DebugUtils.v(TAG, "accessibility is disabled");
        }

        return found;
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getClass().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isServiceRunning(Context context, String serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
