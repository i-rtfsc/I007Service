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

package com.journeyOS.core.modules.battery

import com.journeyOS.core.Engine
import com.journeyOS.core.EngineManager
import com.journeyOS.core.modules.app.AppInfo
import com.journeyOS.i007Service.ClientSession
import com.journeyOS.i007Service.core.I007Core
import com.journeyOS.i007Service.core.I007Observer
import com.journeyOS.i007Service.core.ProduceableSubject
import com.journeyOS.i007Service.core.accessibility.AccessibilityInfoObserver
import com.journeyOS.i007Service.core.accessibility.AccessibilityService
import com.journeyOS.i007Service.core.accessibility.ActivityListener
import com.journeyOS.i007Service.database.DatabaseManager
import com.journeyOS.i007Service.task.TaskManager

class AppEngine : ProduceableSubject<AppInfo>(), Engine, ActivityListener {

    companion object {
        const val DEFAULT = "default"
        const val ALBUM = "album"
        const val BROWSER = "browser"
        const val GAME = "game"
        const val IM = "im"
        const val MUSIC = "music"
        const val NEWS = "news"
        const val READER = "reader"
        const val VIDEO = "video"
    }

    var mAccessibilityInfoObserver: AccessibilityInfoObserver? = null

    private var mPackageName: String = "null"

    override fun work() {
        mAccessibilityInfoObserver = AccessibilityInfoObserver(I007Core.getCore().context)
        AccessibilityService.addDelegate(100, mAccessibilityInfoObserver);
        mAccessibilityInfoObserver?.addListener(this)

        onResult()
    }

    override fun shutdown() {

    }

    override fun onResult() {
        subject().subscribe {
            ClientSession.notifyClient(EngineManager.SCENE_MODLUE_APP, it)
        }
    }

    override fun activityResumed(packageName: String?, activity: String?) {
        packageName?.let {
            TaskManager.diskIOThread().execute {
                if (!mPackageName.equals(it)) {
                    val app = DatabaseManager.queryApp(it)
                    var appInfo = AppInfo()
                    appInfo.packageName = it
                    appInfo.type = app?.type ?: DEFAULT
                    this.produce(appInfo)
                }
                mPackageName = packageName;
            }
        }

    }

}