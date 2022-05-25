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

package com.journeyOS.common.notification

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import com.journeyOS.common.notification.builder.SingleLineBuilder
import com.journeyOS.i007manager.I007Core


/**
 * 通知Helper
 * @author solo
 */
object NotificationHelper {

    private val mContext by lazy {
        I007Core.getCore().context
    }

    private val mManager by lazy {
        mContext.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * 创造一个供前台服务使用的Notification对象【不需要id】
     * 在startForeground第一个参数系统会作为notification的id
     */
    fun buildForegroundService(
        smallIcon: Int,
        contentTitle: CharSequence,
        contentText: CharSequence?,
        contentIntent: PendingIntent?
    ) = SingleLineBuilder(mContext).apply {
        setBase(smallIcon, contentTitle, contentText)
            .setContentIntent(contentIntent)
    }

    fun cancel(id: Int) = mManager.cancel(id)
    fun cancelAll() = mManager.cancelAll()
}