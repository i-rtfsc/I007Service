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

package com.journeyOS.core

import com.journeyOS.core.modules.battery.AppEngine
import com.journeyOS.core.modules.battery.BatteryEngine
import com.journeyOS.core.modules.cpu.CpuEngine


object EngineManager {
    private val TAG = EngineManager::class.java.simpleName as String

    /**
     * 前台变化为何种类型APP场景
     */
    const val SCENE_MODLUE_APP = (1 shl 1).toLong()
    /**
     * 屏幕亮灭场景
     */
    const val SCENE_MODLUE_LCD = (1 shl 2).toLong()
    /**
     * 网络变化场景
     */
    const val SCENE_MODLUE_NET = (1 shl 3).toLong()
    /**
     * 耳机插拔场景
     */
    const val SCENE_MODLUE_HEADSET = (1 shl 4).toLong()
    /**
     * 电池电量、温度等变化场景
     */
    const val SCENE_MODLUE_BATTERY = (1 shl 5).toLong()
    /**
     * 电池电量、温度等变化场景
     */
    const val SCENE_MODLUE_CPU = (1 shl 6).toLong()

    const val SCENE_MODLUE_ALL = (SCENE_MODLUE_APP
            or SCENE_MODLUE_LCD
            or SCENE_MODLUE_NET
            or SCENE_MODLUE_HEADSET
            or SCENE_MODLUE_BATTERY
            or SCENE_MODLUE_CPU)


    const val INTERVAL = 3 * 1000L

    private val mAppEngine = AppEngine()
    private val mBatteryEngine = BatteryEngine()
    private val mCpuEngine = CpuEngine()

    fun work(moduleId: Long) {
        if (moduleId and SCENE_MODLUE_APP != 0L) {
            mAppEngine.work()
        } else if (moduleId and SCENE_MODLUE_LCD != 0L) {

        } else if (moduleId and SCENE_MODLUE_NET != 0L) {

        } else if (moduleId and SCENE_MODLUE_HEADSET != 0L) {

        } else if (moduleId and SCENE_MODLUE_BATTERY != 0L) {
            mBatteryEngine.work()
        }
    }

    fun shutdown(moduleId: Long) {
        if (moduleId and SCENE_MODLUE_APP != 0L) {
            mAppEngine.shutdown()
        } else if (moduleId and SCENE_MODLUE_LCD != 0L) {

        } else if (moduleId and SCENE_MODLUE_NET != 0L) {

        } else if (moduleId and SCENE_MODLUE_HEADSET != 0L) {

        } else if (moduleId and SCENE_MODLUE_BATTERY != 0L) {
            mBatteryEngine.shutdown()
        }
    }

//    fun getBatteryEngine(): BatteryEngine {
//        return mBatteryEngine
//    }
//
//    fun getBatteryInfo(): BatteryInfo? {
//        return mBatteryEngine.getBatteryInfo()
//    }
//
//    fun handleBattery(observer: I007Observer?) {
//        mBatteryEngine.onResult(observer)
//    }
//
//    fun getCpuEngine(): CpuEngine {
//        return mCpuEngine
//    }
//
//    fun getCpuInfo(): CpuInfo? {
//        return mCpuEngine.getCpuInfo()
//    }
//
//    fun handleCpu(observer: I007Observer?) {
//        mCpuEngine.onResult(observer)
//    }

}