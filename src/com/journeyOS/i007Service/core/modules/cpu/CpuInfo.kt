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

package com.journeyOS.core.modules.cpu


class CpuInfo {
    // 总的cpu使用率(user + system+io+其他)
    var totalUseRatio: Double = 0.toDouble()
    // app的cpu使用率
    var appCpuRatio: Double = 0.toDouble()
    // 用户进程cpu使用率
    var userCpuRatio: Double = 0.toDouble()
    // 系统进程cpu使用率
    var sysCpuRatio: Double = 0.toDouble()
    // io等待时间占比
    var ioWaitRatio: Double = 0.toDouble()
    // cpu频率
    var cpuFreq: Double = 0.toDouble()

    override fun toString(): String {
        return "CpuInfo(totalUseRatio=$totalUseRatio, appCpuRatio=$appCpuRatio, userCpuRatio=$userCpuRatio, sysCpuRatio=$sysCpuRatio, ioWaitRatio=$ioWaitRatio, cpuFreq=$cpuFreq)"
    }

}
