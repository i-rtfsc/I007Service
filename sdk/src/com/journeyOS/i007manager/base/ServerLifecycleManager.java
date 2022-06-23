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

package com.journeyOS.i007manager.base;

import com.journeyOS.i007manager.ServerLifecycle;
import com.journeyOS.i007manager.SmartLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author solo
 */
public class ServerLifecycleManager {
    private static final String TAG = ServerLifecycleManager.class.getSimpleName();

    private List<ServerLifecycle> mSlc = new ArrayList<ServerLifecycle>();

    public ServerLifecycleManager() {
    }

    /**
     * Registers a callback to be invoked on voice command result.
     *
     * @param listener The callback that will run.
     */
    public void registerListener(ServerLifecycle listener) {
        if (listener == null) {
            SmartLog.d(TAG, "listener should not be null");
            return;
        }

        if (!mSlc.contains(listener)) {
            mSlc.add(listener);
        }
    }

    /**
     * Unregisters a previous callback.
     *
     * @param listener The callback that should be unregistered.
     * @see #registerListener
     */
    public void unregisterListener(ServerLifecycle listener) {
        if (listener == null) {
            SmartLog.d(TAG, "listener should not be null");
            return;
        }

        if (mSlc.contains(listener)) {
            mSlc.remove(listener);
        }
    }

    /**
     * 通知客户端服务运行
     */
    public void notifyStarted() {
        notifyClient(false);
    }

    /**
     * 通知客户端服务死亡
     */
    public void notifyDied() {
        notifyClient(true);
    }

    /**
     * 通知客户端
     *
     * @param died 是否死亡
     */
    private void notifyClient(boolean died) {
        for (ServerLifecycle listener : mSlc) {
            if (died) {
                SmartLog.d(TAG, "notify client service died");
                listener.onDied();
            } else {
                SmartLog.d(TAG, "notify client service started");
                listener.onStarted();
            }
        }
    }

}
