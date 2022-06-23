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

    /**
     * 获取 I007Core 单例
     *
     * @return I007Core 实例
     */
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

    /**
     * 启动 I007 System Server
     *
     * @param context 上下文
     */
    public void startup(Context context) {
        if (DEBUG) {
            SmartLog.d(TAG, "startup, call from = [" + context.getPackageName() + "], isRunning = [" + isRunning + "]");
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

    /**
     * I007 System Server 是否正在运行
     *
     * @return 是否运行
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 内部设置I007 System Server的状态，内部不要调用此接口
     *
     * @param running 运行状态
     */
    @Deprecated
    public void setRunning(boolean running) {
        isRunning = running;
    }

    /**
     * Registers a callback to be invoked on voice command result.
     * 在startup函数之前调用，才有回调过去
     *
     * @param listener The callback that will run.
     */
    public void registerListener(ServerLifecycle listener) {
        ServiceManagerNative.getInstance().registerListener(listener);
    }

    /**
     * Unregisters a previous callback.
     *
     * @param listener The callback that should be unregistered.
     * @see #registerListener
     */
    public void unregisterListener(ServerLifecycle listener) {
        ServiceManagerNative.getInstance().unregisterListener(listener);
    }

}
