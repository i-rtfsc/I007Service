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

class BatteryInfo {
    // int类型，状态，定义值是BatteryManager.BATTERY_STATUS_XXX / 正在充电、充满等等
    var status: Int = 0
    // int类型，健康，定义值是BatteryManager.BATTERY_HEALTH_XXX。
    var health: Int = 0
    // boolean类型
    var present: Boolean = false
    // int类型，电池剩余容量
    var level: Int = 0
    //int类型，电池最大值。通常为100。
    var scale: Int = 0
    //int类型，连接的电源插座，定义值是BatteryManager.BATTERY_PLUGGED_XXX。
    var plugged: Int = 0
    //int类型，电压 mV
    var voltage: Int = 0
    //int类型，电流
    var electricity: Int = 0
    //int类型，温度，0.1度单位。例如 表示197的时候，意思为19.7度。
    var temperature: Int = 0
    //String类型，电池类型，例如，Li-ion等等。
    var technology: String? = null

    override fun toString(): String {
        return "BatteryInfo(status=$status, health=$health, present=$present, level=$level, scale=$scale, plugged=$plugged, voltage=$voltage, temperature=$temperature, technology=$technology)"
    }

}