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

package com.journeyOS.i007Service.service.base

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.journeyOS.common.notification.NotificationHelper
import com.journeyOS.i007Service.R
import com.journeyOS.i007manager.SmartLog

open class I007BaseService : Service() {
    companion object {
        const val TAG = "I007BaseService"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        SmartLog.d(TAG, "i007 base service create")
    }

    override fun onDestroy() {
        super.onDestroy()
        SmartLog.d(TAG, "i007 base service destroy")
    }

    /**
     * 显示正在播放的notification
     */
    fun showForegroundService() = NotificationHelper
        .buildForegroundService(
            R.drawable.svg_service,
            getString(R.string.service_running),
            null,
            null
        )
        .setForegroundService()
        .setLockScreenVisibility(Notification.VISIBILITY_SECRET)
        .setPriority(NotificationManager.IMPORTANCE_NONE)
        .show()


    /**
     * 释放service
     */
    fun releaseService() {
        stopForeground(true)
        stopSelf()
    }

}