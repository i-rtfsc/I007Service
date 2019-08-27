package com.journeyOS.i007Service.service

import com.journeyOS.core.EngineManager
import com.journeyOS.i007Service.II007Engine
import com.journeyOS.i007Service.base.Constant
import com.journeyOS.i007Service.base.utils.DebugUtils
import com.journeyOS.i007Service.core.service.ServiceManagerNative

class I007Engine : II007Engine.Stub {

    private val TAG = I007Engine::class.java.simpleName

    companion object {
        private var instance: I007Engine? = null
            get() {
                if (field == null) {
                    field = I007Engine()
                }
                return field
            }

        fun get(): I007Engine {
            return instance!!
        }
    }

    constructor() {
    }


    fun systemReady() {
        try {
            val iBinder = ServiceManagerNative.getService(Constant.I007_ENGINE)
            if (iBinder == null) {
                val i007Engine = I007Engine()
                ServiceManagerNative.addService(Constant.I007_ENGINE, i007Engine)
            } else {
                DebugUtils.w(TAG, "service " + Constant.I007_ENGINE + " already added, it cannot be added once more...")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DebugUtils.e(TAG, "Exception in add service " + Constant.I007_ENGINE + ": " + e.message)
        }
    }

    override fun startEngine(module: Long) {
       EngineManager.work(module)
    }

    override fun shutdownEngine(module: Long) {
        EngineManager.shutdown(module)
    }

}