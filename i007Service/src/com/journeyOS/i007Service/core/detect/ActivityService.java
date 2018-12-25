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

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.journeyOS.i007Service.I007Manager;
import com.journeyOS.i007Service.base.utils.DebugUtils;
import com.journeyOS.i007Service.core.NotifyManager;
import com.journeyOS.i007Service.database.DatabaseManager;


public class ActivityService extends AccessibilityService {
    private static final String TAG = ActivityService.class.getSimpleName();
    private static String sPackageName;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        DebugUtils.d(TAG, "onAccessibilityEvent() called with: event = [" + event + "]");
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            I007Manager.keepAlive();
            String packageName = event.getPackageName().toString();
            DebugUtils.d(TAG, "on activiy lister, current package name = " + packageName);
            if (packageName == null) return;

            boolean isBLApp = DatabaseManager.getDefault().isBLApp(packageName);
            DebugUtils.d(TAG, "on activiy lister, this app was black list = [" + isBLApp + "]");
            if (isBLApp) {
                return;
            }

            NotifyManager.getDefault().setPackageName(packageName);

            if (!packageName.equals(sPackageName)) {
                PackageNameMonitor.getDefault().activityResumed(packageName);
            }
            sPackageName = packageName;
        }
    }

    @Override
    public void onInterrupt() {

    }
}
