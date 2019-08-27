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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import com.journeyOS.i007Service.base.utils.DebugUtils
import com.journeyOS.i007Service.core.Producer
import io.reactivex.schedulers.Schedulers

class BatteryChangeReceiver : BroadcastReceiver() {
    private var mBatteryManager: BatteryManager? = null
    private var mProducer: Producer<BatteryInfo>? = null
    private var mBatteryInfo: BatteryInfo = BatteryInfo()

    fun setProducer(producer: Producer<BatteryInfo>) {
        mProducer = producer
    }

    fun getBatteryInfo(): BatteryInfo {
        return mBatteryInfo
    }

    override fun onReceive(context: Context, batteryInfoIntent: Intent) {
        Schedulers.computation().scheduleDirect(Runnable {
            try {
                if (mBatteryManager == null) {
                    mBatteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                }

                mBatteryInfo.status = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
                mBatteryInfo.health = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
                mBatteryInfo.present = batteryInfoIntent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)
                mBatteryInfo.level = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                mBatteryInfo.scale = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 0)
                mBatteryInfo.plugged = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
                mBatteryInfo.voltage = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                mBatteryInfo.temperature = batteryInfoIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
                mBatteryInfo.technology = batteryInfoIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
                val electricity = mBatteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                        ?: 0
                mBatteryInfo.electricity = electricity / 1000

                mProducer?.produce(mBatteryInfo)
            } catch (e: Throwable) {
                DebugUtils.e(e.toString())
            }
        })

    }
}
