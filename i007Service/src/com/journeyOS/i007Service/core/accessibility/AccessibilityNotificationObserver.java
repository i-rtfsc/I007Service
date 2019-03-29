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

package com.journeyOS.i007Service.core.accessibility;

import android.content.Context;
import android.service.notification.NotificationListenerService.RankingMap;
import android.service.notification.StatusBarNotification;
import android.view.accessibility.AccessibilityEvent;

import com.journeyOS.i007Service.base.utils.DebugUtils;
import com.journeyOS.i007Service.core.notification.Notification;
import com.journeyOS.i007Service.core.notification.NotificationListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class AccessibilityNotificationObserver implements NotificationListener, AccessibilityDelegate {
    public class Toast {
        public final List<String> texts;
        public final String packageName;

        public Toast(String packageName, List<CharSequence> texts) {
            this.texts = new ArrayList<>(texts.size());
            for (CharSequence t : texts) {
                if (t != null) {
                    this.texts.add(t.toString());
                }
            }
            this.packageName = packageName;
        }

        public String getText() {
            if (texts.isEmpty()) {
                return null;
            }
            CharSequence text = texts.get(0);
            if (text == null) {
                return null;
            }
            return text.toString();
        }

        public List<String> getTexts() {
            return texts;
        }

        public String getPackageName() {
            return packageName;
        }

        @Override
        public String toString() {
            return "Toast{" +
                    "texts=" + texts +
                    ", packageName='" + packageName + '\'' +
                    '}';
        }
    }

    public interface ToastListener {
        void onToast(Toast toast);
    }

    private static final Set<Integer> EVENT_TYPES = Collections.singleton(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);

    private static final String TAG = "NotificationObserver";
    private static final String[] EMPTY = new String[0];

    private Context mContext;
    private CopyOnWriteArrayList<NotificationListener> mNotificationListeners = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<ToastListener> mToastListeners = new CopyOnWriteArrayList<>();

    public AccessibilityNotificationObserver(Context context) {
        mContext = context;
    }

    public void addNotificationListener(NotificationListener listener) {
        mNotificationListeners.add(listener);
    }

    public boolean removeNotificationListener(NotificationListener listener) {
        return mNotificationListeners.remove(listener);
    }


    public void addToastListener(ToastListener listener) {
        mToastListeners.add(listener);
    }

    public boolean removeToastListener(ToastListener listener) {
        return mToastListeners.remove(listener);
    }

    @Override
    public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
        if (event.getParcelableData() instanceof Notification) {
            android.app.Notification notification = (android.app.Notification) event.getParcelableData();
            DebugUtils.d(TAG, "onNotification: " + notification + "; " + event);
            onNotification(null, Notification.create(notification, event.getPackageName().toString()));
        } else {
            List<CharSequence> list = event.getText();
            DebugUtils.d(TAG, "onNotification: " + list + "; " + event);
            if (event.getPackageName().equals(mContext.getPackageName())) {
                return false;
            }
            if (list != null) {
                onToast(event, new Toast(event.getPackageName().toString(), list));
            }
        }

        return false;
    }

    private void onToast(AccessibilityEvent event, Toast toast) {
        for (ToastListener listener : mToastListeners) {
            try {
                listener.onToast(toast);
            } catch (Exception e) {
                DebugUtils.e(TAG, "Error onNotification: " + toast + " Listener: " + listener, e);
            }
        }
    }

    @Override
    public Set<Integer> getEventTypes() {
        return EVENT_TYPES;
    }

    @Override
    public void onNotification(StatusBarNotification sbn, Notification notification) {
        for (NotificationListener listener : mNotificationListeners) {
            try {
                listener.onNotification(sbn, notification);
            } catch (Exception e) {
                DebugUtils.e(TAG, "Error onNotification: " + notification + " Listener: " + listener, e);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        for (NotificationListener listener : mNotificationListeners) {
            try {
                listener.onNotificationRemoved(sbn, rankingMap);
            } catch (Exception e) {
                DebugUtils.e(TAG, "Error onNotificationRemoved: " + sbn + " Listener: " + listener, e);
            }
        }
    }
}
