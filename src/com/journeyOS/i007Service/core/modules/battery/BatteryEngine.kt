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

import android.content.Intent
import android.content.IntentFilter
import com.journeyOS.core.Engine
import com.journeyOS.core.EngineManager
import com.journeyOS.i007Service.ClientSession
import com.journeyOS.i007Service.core.I007Core
import com.journeyOS.i007Service.core.I007Observer
import com.journeyOS.i007Service.core.ProduceableSubject

class BatteryEngine : ProduceableSubject<BatteryInfo>(), Engine {
    private var hasBatteryRegister = false
    private var mBatteryChangeReceiver: BatteryChangeReceiver? = null

    override fun work() {
        if (!hasBatteryRegister || mBatteryChangeReceiver == null) {
            hasBatteryRegister = true
            mBatteryChangeReceiver = BatteryChangeReceiver()
            mBatteryChangeReceiver?.setProducer(this)
            I007Core.getCore().context.registerReceiver(mBatteryChangeReceiver, BatteryIntentFilterHolder.BATTERY_INTENT_FILTER)
        }

        onResult()
    }

    override fun shutdown() {
        if (hasBatteryRegister && mBatteryChangeReceiver != null) {
            I007Core.getCore().context.unregisterReceiver(mBatteryChangeReceiver)
            mBatteryChangeReceiver = null
            hasBatteryRegister = false
        }
    }

    override fun onResult() {
        subject().subscribe {
            ClientSession.notifyClient(EngineManager.SCENE_MODLUE_BATTERY, it)
        }
    }

    fun getBatteryInfo(): BatteryInfo? {
        return mBatteryChangeReceiver?.getBatteryInfo()
    }

    object BatteryIntentFilterHolder {
        val BATTERY_INTENT_FILTER = IntentFilter()

        init {
            BATTERY_INTENT_FILTER.addAction(Intent.ACTION_BATTERY_CHANGED)
            BATTERY_INTENT_FILTER.addAction(Intent.ACTION_BATTERY_LOW)
            BATTERY_INTENT_FILTER.addAction(Intent.ACTION_BATTERY_OKAY)
        }
    }
}