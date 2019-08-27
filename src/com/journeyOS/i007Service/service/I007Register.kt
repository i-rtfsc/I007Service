package com.journeyOS.i007Service.service

import com.journeyOS.i007Service.ClientSession
import com.journeyOS.i007Service.II007Listener
import com.journeyOS.i007Service.II007Register
import com.journeyOS.i007Service.base.Constant
import com.journeyOS.i007Service.base.utils.DebugUtils
import com.journeyOS.i007Service.core.service.ServiceManagerNative

class I007Register : II007Register.Stub {

    private val TAG = I007Register::class.java.simpleName

    companion object {
        private var instance: I007Register? = null
            get() {
                if (field == null) {
                    field = I007Register()
                }
                return field
            }

        fun get(): I007Register {
            return instance!!
        }
    }

    constructor() {
    }


    fun systemReady() {
        try {
            val iBinder = ServiceManagerNative.getService(Constant.I007_REGISTER)
            if (iBinder == null) {
                val i007Service = I007Register()
                ServiceManagerNative.addService(Constant.I007_REGISTER, i007Service)
            } else {
                DebugUtils.w(TAG, "service " + Constant.I007_REGISTER + " already added, it cannot be added once more...")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DebugUtils.e(TAG, "Exception in add service " + Constant.I007_REGISTER + ": " + e.message)
        }

    }

    override fun registerListener(listener: II007Listener?) {
        if (listener == null) {
            DebugUtils.i(TAG, "listener is null")
            return
        }

        ClientSession.insertListener(listener)
    }

    override fun unregisterListener(listener: II007Listener?) {
        if (listener == null) {
            DebugUtils.i(TAG, "listener is null")
            return
        }

        ClientSession.removeListener(listener)
    }

}