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

package com.journeyOS.i007Service.hook.compat;

import android.os.Build;

import java.lang.reflect.Field;

public class IIActivityManagerCompat {
    //http://androidxref.com/9.0.0_r3/xref/frameworks/base/core/java/android/app/IActivityManager.aidl
    public static final String METHOD_START_ACTIVITY = "startActivity";
    public static final String METHOD_ACTIVITY_PAUSED = "activityPaused";
    public static final String METHOD_ACTIVITY_STOPPED = "activityStopped";
    public static final String METHOD_FINISH_ACTIVITY = "finishActivity";

    public static final String METHOD_START_SERVICE = "startService";
    public static final String METHOD_BIND_SERVICE = "bindService";
    public static final String METHOD_STOP_SERVICE = "stopService";
    public static final String METHOD_START_SERVICE_FOREGROUND = "setServiceForeground";

    public static Object getObject() throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        Field gDefaultField = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Class<?> activityManager = Class.forName("android.app.ActivityManager");
            gDefaultField = activityManager.getDeclaredField("IActivityManagerSingleton");
        } else {
            Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
            gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
        }

        gDefaultField.setAccessible(true);
        Object value = gDefaultField.get(null);

        return value;
    }

    public static Field getInstance() throws ClassNotFoundException, NoSuchFieldException {
        Class<?> singletonClz = Class.forName("android.util.Singleton");
        Field instanceField = singletonClz.getDeclaredField("mInstance");
        instanceField.setAccessible(true);
        return instanceField;
    }

}
