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

package com.journeyOS.i007.core.detect;

import android.content.Context;

import com.journeyOS.i007.I007Manager;
import com.journeyOS.i007.base.util.DebugUtils;
import com.journeyOS.i007.base.util.Singleton;
import com.journeyOS.i007.core.I007Core;
import com.journeyOS.i007.core.NotifyManager;
import com.journeyOS.i007.data.AppInfo;
import com.journeyOS.i007.database.App;
import com.journeyOS.i007.database.DatabaseManager;


public class PackageNameMonitor extends Monitor {
    private static final String TAG = PackageNameMonitor.class.getSimpleName();
    private Context mContext;

    public static final String ALBUM = "album";
    public static final String BROWSER = "browser";
    public static final String GAME = "game";
    public static final String IM = "im";
    public static final String MUSIC = "music";
    public static final String NEWS = "news";
    public static final String READER = "reader";
    public static final String VIDEO = "video";

    private static final Singleton<PackageNameMonitor> gDefault = new Singleton<PackageNameMonitor>() {
        @Override
        protected PackageNameMonitor create() {
            return new PackageNameMonitor();
        }
    };

    private PackageNameMonitor() {
        mContext = I007Core.getCore().getContext();
    }

    public static PackageNameMonitor getDefault() {
        return gDefault.get();
    }

    public void activityResumed(String packageName) {
        DebugUtils.d(TAG, "activityResumed() called with: packageName = [" + packageName + "]");
        if (packageName == null) {
            return;
        }
        notifyApp(packageName);
    }

    private void notifyApp(String packageName) {
        App app = DatabaseManager.getDefault().queryApp(packageName);
        AppInfo appInfo = new AppInfo();
        appInfo.factorId = I007Manager.SCENE_FACTOR_APP;
        appInfo.packageName = packageName;
        if (ALBUM.equals(app.type)) {
            appInfo.state = I007Manager.SCENE_FACTOR_APP_STATE_ALBUM;
        } else if (BROWSER.equals(app.type)) {
            appInfo.state = I007Manager.SCENE_FACTOR_APP_STATE_BROWSER;
        } else if (GAME.equals(app.type)) {
            appInfo.state = I007Manager.SCENE_FACTOR_APP_STATE_GAME;
        } else if (IM.equals(app.type)) {
            appInfo.state = I007Manager.SCENE_FACTOR_APP_STATE_IM;
        } else if (MUSIC.equals(app.type)) {
            appInfo.state = I007Manager.SCENE_FACTOR_APP_STATE_MUSIC;
        } else if (NEWS.equals(app.type)) {
            appInfo.state = I007Manager.SCENE_FACTOR_APP_STATE_NEWS;
        } else if (READER.equals(app.type)) {
            appInfo.state = I007Manager.SCENE_FACTOR_APP_STATE_READER;
        } else if (VIDEO.equals(app.type)) {
            appInfo.state = I007Manager.SCENE_FACTOR_APP_STATE_VIDEO;
        } else {
            appInfo.state = I007Manager.SCENE_FACTOR_APP_STATE_DEFAULT;
        }

        NotifyManager.getDefault().onFactorChanged(I007Manager.SCENE_FACTOR_APP, appInfo);
    }

    @Override
    public void onStart() {
    }

}
