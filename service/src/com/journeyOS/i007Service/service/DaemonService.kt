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

package com.journeyOS.i007Service.service

import android.content.Intent
import com.journeyOS.common.SmartLog
import com.journeyOS.framework.service.ServiceCache
import com.journeyOS.i007Service.service.base.I007BaseService
import java.io.FileDescriptor
import java.io.PrintWriter
import java.util.*

/**
 * adb shell dumpsys activity service com.journeyOS.i007Service/.service.DaemonService
 */
class DaemonService : I007BaseService() {
    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        SmartLog.d(TAG, " intent = $intent, flags = $flags, startId = $startId")
        val notificationId = UUID.randomUUID().hashCode()
        startForeground(notificationId, showForegroundService())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun dump(fd: FileDescriptor?, writer: PrintWriter?, args: Array<out String>?) {
        super.dump(fd, writer, args)
        SmartLog.d(TAG, "dump() called with: fd = $fd, writer = $writer, args = $args")
        ServiceCache.dumpService()
    }
}