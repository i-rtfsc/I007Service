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

import com.journeyOS.i007.base.util.DebugUtils;
import com.journeyOS.i007.base.util.JsonHelper;
import com.journeyOS.i007.base.util.Singleton;
import com.journeyOS.i007.core.clients.ClientSession;


public class NotifyManager<T> {
    private static final String TAG = NotifyManager.class.getSimpleName();
    private static String sPackageName;
    private Object mLock = new Object();

    private static final Singleton<NotifyManager> gDefault = new Singleton<NotifyManager>() {
        @Override
        protected NotifyManager create() {
            return new NotifyManager();
        }
    };

    private NotifyManager() {
    }

    public static NotifyManager getDefault() {
        return gDefault.get();
    }

    public int onFactorChanged(long factoryId, T data) {
        DebugUtils.d(TAG, "onFactorChanged() called with: factoryId = [" + factoryId + "], data = [" + data + "]");
        synchronized (mLock) {
            try {
                ClientSession clientSession = ClientSession.getDefault();
                clientSession.dispatchFactorEvent(factoryId, JsonHelper.toJson(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public String getPackageName() {
        return sPackageName;
    }

    public void setPackageName(String packageName) {
        this.sPackageName = packageName;
    }
}
