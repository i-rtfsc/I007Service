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


import com.journeyOS.core.Engine
import com.journeyOS.core.EngineManager
import com.journeyOS.i007Service.ClientSession
import com.journeyOS.i007Service.core.I007Observer
import com.journeyOS.i007Service.core.ProduceableSubject
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit


class CpuEngine : ProduceableSubject<CpuInfo>(), Engine {
    private var hasWork = false
    private var mCpuInfo = CpuInfo()
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable();

    override fun work() {
        hasWork = true
        mCompositeDisposable.add(Observable
                .interval(EngineManager.INTERVAL, TimeUnit.MILLISECONDS)
                .concatMap { t ->
                    create()
                }
                .subscribe { t: CpuInfo? -> t?.let { this.produce(it) } }
        )

        onResult()
    }

    override fun shutdown() {
        hasWork = false
        mCompositeDisposable.dispose()
    }

    override fun onResult() {
        if (!hasWork) {
            work()
        }
        subject().subscribe {
            ClientSession.notifyClient(EngineManager.SCENE_MODLUE_CPU, it)
        }
    }

    fun getCpuInfo(): CpuInfo? {
        return mCpuInfo
    }

    fun create(): Observable<CpuInfo> {
        return Observable
                .timer(EngineManager.INTERVAL, TimeUnit.MILLISECONDS)
                .map { t ->
                    getCpu()
                }
    }

    fun getCpu(): CpuInfo {
        val pid = -1
        val cpuMessage = CpuSnapshot.getCpuMessage(pid)
        val totalTime = (cpuMessage.total - cpuMessage.total) * 1.0f
        val idleTime = cpuMessage.idle - cpuMessage.idle
        val totalRatio = ((totalTime - idleTime) / totalTime).toDouble()
        val appRatio = (cpuMessage.app - cpuMessage.app) / totalTime
        val userRatio = (cpuMessage.user - cpuMessage.user) / totalTime
        val systemRatio = (cpuMessage.system - cpuMessage.system) / totalTime
        val ioWaitRatio = (cpuMessage.ioWait - cpuMessage.ioWait) / totalTime
        var cpuInfo = CpuInfo()
        cpuInfo.totalUseRatio = totalRatio
        cpuInfo.appCpuRatio = appRatio.toDouble()
        cpuInfo.userCpuRatio = userRatio.toDouble()
        cpuInfo.sysCpuRatio = systemRatio.toDouble()
        cpuInfo.ioWaitRatio = ioWaitRatio.toDouble()

        return cpuInfo
    }

}