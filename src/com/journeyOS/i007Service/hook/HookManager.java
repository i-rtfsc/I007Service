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

package com.journeyOS.i007Service.hook;

import android.app.Application;
import android.content.Context;

import com.journeyOS.i007Service.hook.listeners.MethodInvokeListener;

public class HookManager {
    private static final String TAG = HookManager.class.getSimpleName();

    private static Application sContext = null;

    public static void applyHooks(Application context) {
        sContext = context;
    }

    private static void applyHooks() {
        if (sContext == null) {
            throw new IllegalArgumentException("you has not apply hooks!");
        }
    }

    public static void hookActivityManager(MethodInvokeListener listener) {
        applyHooks();
        try {
            ActivityManagerHook immh = new ActivityManagerHook(sContext);
            immh.onHook(sContext.getClassLoader());
            immh.setOnActivityMethodListener(listener);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * hook InputMethorManager
     *
     * @param listener 回调
     */
    public static void hookInputMethodManager(MethodInvokeListener listener) {
        applyHooks();
        try {
            sContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            InputMethodManagerHook immh = new InputMethodManagerHook(sContext);
            immh.onHook(sContext.getClassLoader());
            immh.setOnInputMethodListener(listener);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void hookClipboardManager(MethodInvokeListener listener) {
        applyHooks();
        try {
            sContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipboardManagerHook cmh = new ClipboardManagerHook(sContext);
            cmh.onHook(sContext.getClassLoader());
            cmh.setOnClipboardMethodListener(listener);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void hookNotificationManager(MethodInvokeListener listener) {
        applyHooks();
        try {
            sContext.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationManagerHook nmh = new NotificationManagerHook(sContext);
            nmh.onHook(sContext.getClassLoader());
            nmh.setOnNotificationMethodListener(listener);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
