/*
 * Copyright (c) 2019 anqi.huang@outlook.com
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

package com.journeyOS.i007Service.core.notification;

import android.service.notification.StatusBarNotification;

import com.journeyOS.i007Service.core.ServiceLifecycleListener;

import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationListenerService extends android.service.notification.NotificationListenerService {

    private CopyOnWriteArrayList<NotificationListener> mNotificationListeners = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<ServiceLifecycleListener> mLifecycleListeners = new CopyOnWriteArrayList<>();
    private static NotificationListenerService sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        for (ServiceLifecycleListener listener : mLifecycleListeners) {
            listener.onRunning();
        }
    }

    public static NotificationListenerService getInstance() {
        return sInstance;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        for (NotificationListener listener : mNotificationListeners) {
            listener.onNotification(sbn, Notification.create(
                    sbn.getNotification(), sbn.getPackageName()));
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        for (NotificationListener listener : mNotificationListeners) {
            listener.onNotificationRemoved(sbn, null);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        for (NotificationListener listener : mNotificationListeners) {
            listener.onNotificationRemoved(sbn, rankingMap);
        }
    }

    public void addListener(NotificationListener listener) {
        mNotificationListeners.add(listener);
    }

    public boolean removeListener(NotificationListener listener) {
        return mNotificationListeners.remove(listener);
    }


    public void addLifecycleListener(ServiceLifecycleListener listener) {
        mLifecycleListeners.add(listener);
    }

    public boolean removeLifecycleListener(ServiceLifecycleListener listener) {
        return mLifecycleListeners.remove(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (ServiceLifecycleListener listener : mLifecycleListeners) {
            listener.onStoping();
        }
        sInstance = null;
    }
}
