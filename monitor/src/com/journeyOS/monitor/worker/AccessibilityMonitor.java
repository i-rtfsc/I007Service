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

package com.journeyOS.monitor.worker;

import android.content.Context;

import com.journeyOS.common.SmartLog;
import com.journeyOS.common.task.TaskManager;
import com.journeyOS.database.DataRepository;
import com.journeyOS.database.source.local.app.App;
import com.journeyOS.database.source.local.base.DBConfigs;
import com.journeyOS.i007manager.I007App;
import com.journeyOS.i007manager.I007Core;
import com.journeyOS.i007manager.I007Result;
import com.journeyOS.monitor.Monitor;
import com.journeyOS.monitor.MonitorManager;
import com.journeyOS.monitor.accessibility.AccessibilityInfoObserver;
import com.journeyOS.monitor.accessibility.AccessibilityService;
import com.journeyOS.monitor.accessibility.ActivityListener;

/**
 * Accessibility监听器
 *
 * @author solo
 */
public class AccessibilityMonitor extends Monitor implements ActivityListener {
    private static final String TAG = AccessibilityMonitor.class.getSimpleName();
    private volatile static AccessibilityMonitor INSTANCE = null;
    private AccessibilityInfoObserver mAccessibilityInfoObserver;
    private Context mContext = null;
    private String mPackageName = null;
    private String mActivity = null;

    private AccessibilityMonitor() {
        SmartLog.d(TAG, "init");
        mContext = I007Core.getCore().getContext();
    }

    /**
     * 获取AccessibilityMonitor单例
     *
     * @return AccessibilityMonitor实例
     */
    public static AccessibilityMonitor getInstance() {
        if (INSTANCE == null) {
            synchronized (AccessibilityMonitor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AccessibilityMonitor();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void onInit(long factoryId) {
        Context context = I007Core.getCore().getContext();
        mAccessibilityInfoObserver = new AccessibilityInfoObserver(context);
    }

    @Override
    public void onStart() {
        addAccessibilityServiceDelegates();
    }

    @Override
    public void onStop() {
        //TODO
    }

    private void addAccessibilityServiceDelegates() {
        AccessibilityService.addDelegate(100, mAccessibilityInfoObserver);
        mAccessibilityInfoObserver.addListener(this);
    }

    @Override
    public void activityResumed(String packageName, String activity) {
        SmartLog.d(TAG, "on activity changed, packageName = [" + packageName + "], activity = [" + activity + "]");

        /**
         * 如果是activity就通知，那会非常频繁
         * 改成APP切换才通知
         */
        if (packageNameChanged(packageName)) {
            TaskManager.getDefault().submit(new Runnable() {
                @Override
                public void run() {
                    int type = I007App.Type.DEFAULT;
                    App dbApp = DataRepository.getInstance(mContext).getApp(packageName);
                    if (dbApp != null) {
                        type = parseType(dbApp.getType());
                    }

                    I007Result.Builder source = MonitorManager.getInstance().getBuilder();

                    I007App app = new I007App.Builder()
                            .setType(type)
                            .setPackageName(packageName)
                            .setActivity(activity)
                            .build();

                    I007Result.Builder target = source
                            .setFactoryId(mFactoryId)
                            .setApp(app);

                    MonitorManager.getInstance().notifyResult(target);
                }
            });
        }
        mPackageName = packageName;
        mActivity = activity;
    }

    private boolean packageNameChanged(String packageName) {
        return packageName != null && !packageName.equals(mPackageName);
    }

    private boolean activityChanged(String activity) {
        return activity != null && !activity.equals(mActivity);
    }

    private int parseType(String type) {
        switch (type) {
            case DBConfigs.AppType.ALBUM:
                return I007App.Type.ALBUM;
            case DBConfigs.AppType.BROWSER:
                return I007App.Type.BROWSER;
            case DBConfigs.AppType.GAME:
                return I007App.Type.GAME;
            case DBConfigs.AppType.IM:
                return I007App.Type.IM;
            case DBConfigs.AppType.MUSIC:
                return I007App.Type.MUSIC;
            case DBConfigs.AppType.NEWS:
                return I007App.Type.NEWS;
            case DBConfigs.AppType.READER:
                return I007App.Type.READER;
            case DBConfigs.AppType.VIDEO:
                return I007App.Type.VIDEO;
            default:
                return I007App.Type.DEFAULT;
        }
    }

}
