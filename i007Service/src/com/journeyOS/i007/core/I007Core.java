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

import android.app.Application;
import android.content.Context;
import android.os.Looper;

import com.journeyOS.i007.base.util.Singleton;
import com.journeyOS.i007.config.AppConfig;
import com.journeyOS.i007.core.detect.MonitorManager;
import com.journeyOS.i007.core.service.ServiceManagerNative;


public class I007Core {
    private static final String TAG = I007Core.class.getSimpleName();

    private boolean mIsRunning = false;
    private Application mContext;

    private static final Singleton<I007Core> gDefault = new Singleton<I007Core>() {
        @Override
        protected I007Core create() {
            return new I007Core();
        }
    };

    private I007Core() {
    }

    public static I007Core getCore() {
        return gDefault.get();
    }

    public void running(Application context) throws Throwable {
        if (!mIsRunning) {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                throw new IllegalStateException("I007Core.running() must called in main thread!");
            }
            this.mContext = context;
            AppConfig.initialize(mContext);
            ServiceManagerNative.running(mContext);
            MonitorManager.getInstance().init(mContext);
            mIsRunning = true;
        }
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public Application getApplication() {
        return mContext;
    }

    public Context getContext() {
        return mContext;
    }
}
