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

import com.journeyOS.i007Service.I007Manager;
import com.journeyOS.i007Service.base.utils.DebugUtils;
import com.journeyOS.i007Service.base.utils.Singleton;
import com.journeyOS.i007Service.core.I007Core;
import com.journeyOS.i007Service.core.NotifyManager;


public class PackageNameMonitor extends Monitor {
    public static final String ALBUM = "album";
    public static final String BROWSER = "browser";
    public static final String GAME = "game";
    public static final String IM = "im";
    public static final String MUSIC = "music";
    public static final String NEWS = "news";
    public static final String READER = "reader";
    public static final String VIDEO = "video";
    private static final String TAG = PackageNameMonitor.class.getSimpleName();
    private static final Singleton<PackageNameMonitor> gDefault = new Singleton<PackageNameMonitor>() {
        @Override
        protected PackageNameMonitor create() {
            return new PackageNameMonitor();
        }
    };
    private Context mContext;

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
        NotifyManager.getDefault().onFactorChanged(I007Manager.SCENE_FACTOR_APP, packageName);
    }

    @Override
    public void onStart() {
    }

}
