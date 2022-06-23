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

package com.journeyOS.i007manager;

import android.text.TextUtils;
import android.util.Log;

/**
 * smart log
 *
 * @author solo
 */
public class SmartLog {
    private static final String TAG = "I007Service";
    private static final int METHOD_NAME = 4;

    /**
     * 通过命令 adb shell setprop log.tag.I007Service D 打开log
     *
     * @return 是否打开log
     */
    public static boolean isDebug() {
        return Log.isLoggable(TAG, android.util.Log.DEBUG);
    }

    private static String getMethodName() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[METHOD_NAME];
        return stackTraceElement.getMethodName() + "()";
    }

    //e > w > i > d > v

    /**
     * error log
     *
     * @param tag     tag
     * @param message log信息
     */
    public static void e(String tag, String message) {
        Log.e(replaceTag(tag), getMethodName() + " " + message);
    }

    /**
     * warning log
     *
     * @param tag     tag
     * @param message log信息
     */
    public static void w(String tag, String message) {
        Log.w(replaceTag(tag), getMethodName() + " " + message);
    }

    /**
     * info log
     *
     * @param tag     tag
     * @param message log信息
     */
    public static void i(String tag, String message) {
        Log.i(replaceTag(tag), getMethodName() + " " + message);
    }

    /**
     * debug log
     *
     * @param tag     tag
     * @param message log信息
     */
    public static void d(String tag, String message) {
        if (isDebug()) {
            Log.d(replaceTag(tag), getMethodName() + " " + message);
        } else {
            Log.d(replaceTag(tag), message);
        }
    }

    /**
     * verbose log
     *
     * @param tag     tag
     * @param message log信息
     */
    public static void v(String tag, String message) {
        if (isDebug()) {
            Log.v(replaceTag(tag), getMethodName() + " " + message);
        } else {
            Log.v(replaceTag(tag), message);
        }
    }

    private static String replaceTag(String src) {
        if (TextUtils.isEmpty(TAG)) {
            return src;
        } else {
            return TAG + "-" + src;
        }
    }

}
