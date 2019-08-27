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

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.MessageFormat

object CpuSnapshot {
    private val BUFFER_SIZE = 1024

    private val CPU_LOAD_PATH = "/proc/stat"
    private val PROC_STAT_PATH = "/proc/{0}/stat"

    private fun getCpuInfo(filePath: String): String {
        var cpuReader: BufferedReader? = null
        try {
            cpuReader = BufferedReader(InputStreamReader(FileInputStream(filePath)), BUFFER_SIZE)
            return cpuReader.readLine() ?: return ""
        } catch (e: Throwable) {
            return ""
        } finally {
            cpuReader?.close()
        }
    }

    private fun getCpuRate(): String {
        return getCpuInfo(CPU_LOAD_PATH)
    }

    private fun getCpuRateOfApp(pid: Int): String {
        val statPath = MessageFormat.format(PROC_STAT_PATH, pid.toString())
        return getCpuInfo(statPath)
    }

    @Throws(Throwable::class)
    private fun parse(cpuRate: String, pidCpuRate: String): CpuMessage {
        val cpuInfoArray = cpuRate.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (cpuInfoArray.size < 9) {
            throw IllegalStateException("cpu info array size must great than 9")
        }
        var cpuMessage = CpuMessage()
        val user = java.lang.Long.parseLong(cpuInfoArray[2])
        val nice = java.lang.Long.parseLong(cpuInfoArray[3])
        val system = java.lang.Long.parseLong(cpuInfoArray[4])
        val idle = java.lang.Long.parseLong(cpuInfoArray[5])
        val ioWait = java.lang.Long.parseLong(cpuInfoArray[6])
        val total = (user + nice + system + idle + ioWait
                + java.lang.Long.parseLong(cpuInfoArray[7])
                + java.lang.Long.parseLong(cpuInfoArray[8]))

//        val pidCpuInfoList = pidCpuRate.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//        if (pidCpuInfoList.size < 17) {
//            throw IllegalStateException("pid cpu info array size must great than 17")
//        }
//        val appCpuTime = (java.lang.Long.parseLong(pidCpuInfoList[13])
//                + java.lang.Long.parseLong(pidCpuInfoList[14])
//                + java.lang.Long.parseLong(pidCpuInfoList[15])
//                + java.lang.Long.parseLong(pidCpuInfoList[16]))
//
        cpuMessage.user = user
        cpuMessage.system = system
        cpuMessage.idle = idle
        cpuMessage.ioWait = ioWait
        cpuMessage.total = total
//        cpuMessage.app = appCpuTime

        return cpuMessage
    }

    fun getCpuMessage(pid: Int): CpuMessage {
        val cpuRate = getCpuRate()
        val pidCpuRate = getCpuRateOfApp(pid)
        return parse(cpuRate, pidCpuRate)
    }

    class CpuMessage {
        var user: Long = 0
        var system: Long = 0
        var idle: Long = 0
        var ioWait: Long = 0
        var total: Long = 0
        var app: Long = 0
    }
}
