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

package com.journeyOS.monitor.accessibility;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.accessibility.AccessibilityEvent;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author solo
 */
public final class AccessibilityInfoObserver implements ActivityListener, AccessibilityDelegate {
    private volatile String mLatestPackage = "";
    private volatile String mLatestActivity = "";
    private Context mContext;
    private PackageManager mPackageManager;

    private CopyOnWriteArrayList<ActivityListener> mListeners = new CopyOnWriteArrayList<>();

    public AccessibilityInfoObserver(Context context) {
        mContext = context;
        mPackageManager = mContext.getPackageManager();
    }

    /**
     * 注册监听
     *
     * @param listener 回调方
     */
    public void addListener(ActivityListener listener) {
        mListeners.add(listener);
    }

    /**
     * 注销监听
     *
     * @param listener 回调方
     * @return 是否成功
     */
    public boolean removeListener(ActivityListener listener) {
        return mListeners.remove(listener);
    }

    /**
     * 获取最后运行的包名
     *
     * @return 最后运行的包名
     */
    public String getLatestPackage() {
        return mLatestPackage;
    }

    /**
     * 获取最后运行的activity
     *
     * @return 最后运行的activity
     */
    public String getLatestActivity() {
        return mLatestActivity;
    }

    @Override
    public boolean onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            setLatestComponent(event.getPackageName(), event.getClassName());
        }
        return false;
    }

    @Override
    public Set<Integer> getEventTypes() {
        return ALL_EVENT_TYPES;
    }

    private void setLatestComponent(CharSequence latestPackage, CharSequence latestClass) {
        if (latestPackage == null || latestClass == null) {
            return;
        }

        String latestPackageStr = latestPackage.toString();
        String latestClassStr = latestClass.toString();
        if (latestClassStr.startsWith("android.view.") || latestClassStr.startsWith("android.widget.")) {
            return;
        }

        try {
            ComponentName componentName = new ComponentName(latestPackageStr, latestClassStr);
            mLatestActivity = mPackageManager.getActivityInfo(componentName, 0).name;
        } catch (PackageManager.NameNotFoundException ignored) {
            return;
        }
        mLatestPackage = latestPackage.toString();

        activityResumed(mLatestPackage, mLatestActivity);
    }

    @Override
    public void activityResumed(String packageName, String activity) {
        for (ActivityListener listener : mListeners) {
            listener.activityResumed(packageName, activity);
        }
    }
}
