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

package com.journeyOS.i007Service

import com.journeyOS.core.EngineManager
import com.journeyOS.i007Service.base.utils.AppUtils
import com.journeyOS.i007Service.core.I007Core
import com.journeyOS.i007Service.core.service.ServiceManagerNative

object I007Manager {

    /**
     * 前台变化为何种类型APP场景
     */
    const val SCENE_MODLUE_APP = EngineManager.SCENE_MODLUE_APP
    /**
     * 屏幕亮灭场景
     */
    const val SCENE_MODLUE_LCD = EngineManager.SCENE_MODLUE_LCD
    /**
     * 网络变化场景
     */
    const val SCENE_MODLUE_NET = EngineManager.SCENE_MODLUE_NET
    /**
     * 耳机插拔场景
     */
    const val SCENE_MODLUE_HEADSET = EngineManager.SCENE_MODLUE_HEADSET
    /**
     * 电池电量、温度等变化场景
     */
    const val SCENE_MODLUE_BATTERY = EngineManager.SCENE_MODLUE_BATTERY
    /**
     * 电池电量、温度等变化场景
     */
    const val SCENE_MODLUE_CPU = EngineManager.SCENE_MODLUE_CPU

    const val SCENE_MODLUE_ALL = EngineManager.SCENE_MODLUE_ALL

    /**
     * 监听场景变化
     *
     * @param listener 回调
     */
//    @Throws(Throwable::class)
    fun registerListener(listener: II007Listener) {
        val register = ServiceManagerNative.getI007Register()
        register?.registerListener(listener)
    }

    /**
     * 解注册
     *
     * @param listener 回调
     */
    fun unregisterListener(listener: II007Listener) {
        val register = ServiceManagerNative.getI007Register()
        register?.unregisterListener(listener)
    }

    fun startEngine(module: Long) {
        var service = ServiceManagerNative.getII007Engine()
        service.startEngine(module)
    }

    fun shutdownEngine(module: Long) {
        var service = ServiceManagerNative.getII007Engine()
        service.shutdownEngine(module)
    }

    /**
     * Accessibility服务是否打开
     *
     * @return true:Accessibility服务打开
     */
    fun isServiceEnabled(): Boolean {
        return AppUtils.isServiceEnabled(I007Core.getCore().context)
    }

    /**
     * 跳转到设置的Accessibility管理界面
     */
    fun openSettingsAccessibilityService() {
        AppUtils.openSettingsAccessibilityService(I007Core.getCore().context)
    }
}