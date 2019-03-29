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

package com.journeyOS.i007Service.core.detect;

import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.journeyOS.i007Service.I007Manager;
import com.journeyOS.i007Service.base.utils.DebugUtils;
import com.journeyOS.i007Service.base.utils.Singleton;
import com.journeyOS.i007Service.core.I007Core;
import com.journeyOS.i007Service.core.NotifyManager;
import com.journeyOS.i007Service.core.accessibility.AccessibilityInfoObserver;
import com.journeyOS.i007Service.core.accessibility.AccessibilityNotificationObserver;
import com.journeyOS.i007Service.core.accessibility.AccessibilityService;
import com.journeyOS.i007Service.core.accessibility.ActivityListener;
import com.journeyOS.i007Service.core.notification.Notification;
import com.journeyOS.i007Service.core.notification.NotificationListener;
import com.journeyOS.i007Service.database.DatabaseManager;


public class AccessibilityMonitor extends Monitor implements ActivityListener, NotificationListener {
    private static final String TAG = AccessibilityMonitor.class.getSimpleName();

    public static final String ALBUM = "album";
    public static final String BROWSER = "browser";
    public static final String GAME = "game";
    public static final String IM = "im";
    public static final String MUSIC = "music";
    public static final String NEWS = "news";
    public static final String READER = "reader";
    public static final String VIDEO = "video";

    private final AccessibilityInfoObserver mAccessibilityInfoObserver;
    private final AccessibilityNotificationObserver mNotificationObserver;

    private static String sPackageName = null;

    private static final Singleton<AccessibilityMonitor> gDefault = new Singleton<AccessibilityMonitor>() {
        @Override
        protected AccessibilityMonitor create() {
            return new AccessibilityMonitor();
        }
    };
    private Context mContext;

    private AccessibilityMonitor() {
        mContext = I007Core.getCore().getContext();
        mAccessibilityInfoObserver = new AccessibilityInfoObserver(mContext);
        mNotificationObserver = new AccessibilityNotificationObserver(mContext);
    }

    public static AccessibilityMonitor getDefault() {
        return gDefault.get();
    }

    public void activityResumed(String packageName) {
        DebugUtils.d(TAG, "activityResumed() called with: packageName = [" + packageName + "]");
        if (packageName == null) {
            return;
        }
        NotifyManager.getDefault().onFactorChanged(I007Manager.SCENE_FACTOR_APP, packageName);
    }

    @Override
    public void onStart() {
        addAccessibilityServiceDelegates();
    }

    private void addAccessibilityServiceDelegates() {
        AccessibilityService.addDelegate(100, mAccessibilityInfoObserver);
        mAccessibilityInfoObserver.addListener(this);
        AccessibilityService.addDelegate(200, mNotificationObserver);
        mNotificationObserver.addNotificationListener(this);
    }

    @Override
    public void activityResumed(String packageName, String activity) {
        DebugUtils.d(TAG, "on activiy lister, packageName = [" + packageName + "], activity = [" + activity + "]");

        boolean isBLApp = DatabaseManager.getDefault().isBLApp(packageName);
        DebugUtils.d(TAG, "on activiy lister, this app was black list = [" + isBLApp + "]");
        if (isBLApp) {
            return;
        }

        NotifyManager.getDefault().setPackageName(packageName);

        if (!packageName.equals(sPackageName)) {
            AccessibilityMonitor.getDefault().activityResumed(packageName);
        }
        sPackageName = packageName;
    }

    @Override
    public void onNotification(StatusBarNotification sbn, Notification notification) {
        DebugUtils.d(TAG, "on notification lister, notification = [" + notification + "]");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap) {
        DebugUtils.d(TAG, "on notification remove lister, notification = [" + sbn + "]");
    }
}
