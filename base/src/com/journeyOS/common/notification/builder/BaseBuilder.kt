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

package com.journeyOS.common.notification.builder

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat

/**
 * 通知基类
 * @author solo
 */
open class BaseBuilder(context: Context) : ContextWrapper(context) {
    private var mManager: NotificationManager? = null
        get() {
            if (field == null) {
                field = getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
            }
            return field
        }

    /**
     * 最基本的ui
     */
    private var smallIcon: Int = 0
    lateinit var contentTitle: CharSequence
    var contentText: CharSequence? = null

    private var headUp: Boolean = false
    protected var summaryText: CharSequence? = null

    /**
     * 最基本的控制管理，默认不能为0
     */
    var id: Int = 1

    private var bigIcon: Int = 0
    var ticker: CharSequence? = "您有新的消息"

    private var subText: CharSequence? = null
    var flag = NotificationCompat.FLAG_AUTO_CANCEL
    private var priority = NotificationCompat.PRIORITY_DEFAULT

    var soundUri: Uri? = null
    var vibratePatten: LongArray? = null
    var rgb: Int = 0
    var onMs: Int = 0
    var offMs: Int = 0

    /**
     * 默认只有走马灯提醒
     */
    var defaults = NotificationCompat.DEFAULT_LIGHTS
    var sound = true
    var vibrate = true
    var lights = true

    var lockScreenVisibility = NotificationCompat.VISIBILITY_SECRET

    var `when`: Long = 0

    /**
     * 事件
     */
    var contentIntent: PendingIntent? = null
    var deleteIntent: PendingIntent? = null
    var fullscreenIntent: PendingIntent? = null

    /**
     * 种类
     */
    var style: NotificationCompat.Style? = null

    var onGoing = false

    var foregroundService = false

    /**
     * 带按钮
     */
    private val btnActionBeans by lazy { arrayListOf<BtnActionBean>() }

    protected val cBuilder by lazy { NotificationCompat.Builder(this, packageName + priority) }

    fun setLockScreenVisibility(lockScreenVisibility: Int) =
        apply { this.lockScreenVisibility = lockScreenVisibility }

    fun setOnGoing(onGoing: Boolean = true) = apply { this.onGoing = onGoing }

    fun setForegroundService() = apply {
        foregroundService = true
        onGoing = true
    }

    open fun addBtn(icon: Int, text: CharSequence, pendingIntent: PendingIntent) = apply {
        if (btnActionBeans.size > 5) {
            val trimList = btnActionBeans.subList(0, 5)
            btnActionBeans.apply { clear() }.addAll(trimList)
        }
        btnActionBeans.add(BtnActionBean(icon, text, pendingIntent))
    }

    fun getcBuilder() = cBuilder

    fun setBase(icon: Int, contentTitle: CharSequence, contentText: CharSequence?) = apply {
        this.smallIcon = icon
        this.contentTitle = contentTitle
        this.contentText = contentText
    }

    fun setId(id: Int) = apply { this.id = id }

    fun setSummaryText(summaryText: CharSequence) = apply { this.summaryText = summaryText }

    fun setContentText(contentText: CharSequence) = apply { this.contentText = contentText }

    fun setPriority(priority: Int) = apply { this.priority = priority }

    fun setContentIntent(contentIntent: PendingIntent?) =
        apply { this.contentIntent = contentIntent }

    fun setDeleteIntent(deleteIntent: PendingIntent) = apply { this.deleteIntent = deleteIntent }

    /**
     * TODO
     */
    fun setFullScreenIntent(fullscreenIntent: PendingIntent) =
        apply { this.fullscreenIntent = fullscreenIntent }

    fun setSmallIcon(smallIcon: Int) = apply { this.smallIcon = smallIcon }

    fun setBigIcon(bigIcon: Int) = apply { this.bigIcon = bigIcon }

    fun setHeadup() = apply { this.headUp = true }

    fun setTicker(ticker: CharSequence) = apply { this.ticker = ticker }

    fun setSubtext(subText: CharSequence) = apply { this.subText = subText }

    fun setAction(sound: Boolean, vibrate: Boolean, lights: Boolean) = apply {
        this.sound = sound
        this.vibrate = vibrate
        this.lights = lights
    }


    open fun build(channelId: String) {
        cBuilder.setChannelId(channelId)
        /**
         * 该通知要启动的Intent
         */
        cBuilder.setContentIntent(contentIntent)
        /**
         * 设置顶部状态栏的小图标
         */
        if (smallIcon > 0) {
            cBuilder.setSmallIcon(smallIcon)
        }
        if (bigIcon > 0) {
            cBuilder.setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    bigIcon
                )
            )
        }

        /**
         * 在顶部状态栏中的提示信息
         */
        cBuilder.setTicker(ticker ?: "有新消息")

        /**
         * 设置通知中心的标题
         */
        cBuilder.setContentTitle(contentTitle)
        /**
         * 设置通知中心中的内容
         */
        contentText?.apply { cBuilder.setContentText(this) }

        cBuilder.setWhen(System.currentTimeMillis())
        //cBuilder.setStyle()

        /*
         * 将AutoCancel设为true后，当你点击通知栏的notification后，它会自动被取消消失,
         * 不设置的话点击消息后也不清除，但可以滑动删除
         */
        cBuilder.setAutoCancel(true)

        /**
         * 将Ongoing设为true 那么notification将不能滑动删除
         */
        // notifyBuilder.setOngoing(true);
        /*
         * 从Android4.1开始，可以通过以下方法，设置notification的优先级，
         * 优先级越高的，通知排的越靠前，优先级低的，不会在手机最顶部的状态栏显示图标
         */
        cBuilder.priority = priority

        //int defaults = 0;

        if (sound) defaults = defaults or Notification.DEFAULT_SOUND
        if (vibrate) defaults = defaults or Notification.DEFAULT_VIBRATE
        if (lights) defaults = defaults or Notification.DEFAULT_LIGHTS
        cBuilder.setDefaults(defaults)

        /**
         * 按钮
         */
        btnActionBeans.forEach { cBuilder.addAction(it.icon, it.text, it.pendingIntent) }

        /**
         * headUp
         */
        if (headUp) {
            cBuilder.priority = NotificationCompat.PRIORITY_MAX
            cBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
        } else {
            cBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
            cBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)
        }

        cBuilder.setOngoing(onGoing)
        cBuilder.setFullScreenIntent(fullscreenIntent, true)
//        cBuilder.setVisibility(lockScreenVisibility)
        /**
         * 设置小图标着色
         */
        cBuilder.color = Color.parseColor("#00D766")
    }

    fun show(
        importance: Int = NotificationManager.IMPORTANCE_MIN,
        channelName: String = resources.getString(applicationInfo.labelRes)
    ): Notification {
        build(createNotificationChannel(importance, channelName))
        return cBuilder.build().apply {
            if (foregroundService) {
                flags = Notification.FLAG_FOREGROUND_SERVICE
                return@apply
            }
            mManager?.notify(id, this)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(importance: Int, channelName: String): String {
        val channelId = "$packageName-$importance"
        val channel = NotificationChannel(channelId, channelName, importance)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        channel.setSound(null, null)
        mManager?.createNotificationChannel(channel)
        return channelId
    }

    data class BtnActionBean(
        var icon: Int,
        var text: CharSequence,
        var pendingIntent: PendingIntent
    )
}