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

import android.content.Context;
import android.util.Log;

import com.journeyOS.i007manager.base.ServiceManagerNative;

import java.lang.ref.WeakReference;

/**
 * I007 Service Core
 *
 * @author solo
 */
public final class I007Core {
    private static final String TAG = I007Core.class.getSimpleName();
    private static final boolean DEBUG = true;

    private static volatile I007Core sInstance = null;

    private WeakReference<Context> mReference;
    private boolean isRunning = false;

    private I007Core() {
    }

    public static I007Core getCore() {
        if (sInstance == null) {
            synchronized (I007Core.class) {
                if (sInstance == null) {
                    sInstance = new I007Core();
                }
            }
        }
        return sInstance;
    }

    public void startup(Context context) {
        if (DEBUG) {
            Log.d(TAG, "startup, call from = [" + context.getPackageName() + "], isRunning = [" + isRunning + "]");
        }

        if (!isRunning) {
            mReference = new WeakReference<>(context);
            ServiceManagerNative.getInstance().startup(context);
            isRunning = true;
        }
    }

    /**
     * 获取上下文
     *
     * @return Context
     */
    public Context getContext() {
        return mReference.get();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
