package com.journeyOS.i007Service.service

import com.journeyOS.i007Service.II007Service
import com.journeyOS.i007Service.base.Constant
import com.journeyOS.i007Service.base.utils.DebugUtils
import com.journeyOS.i007Service.core.service.ServiceManagerNative

class I007Service : II007Service.Stub {

    private val TAG = I007Service::class.java.simpleName

    companion object {
        private var instance: I007Service? = null
            get() {
                if (field == null) {
                    field = I007Service()
                }
                return field
            }

        fun get(): I007Service {
            return instance!!
        }
    }

    constructor() {
    }


    fun systemReady() {
        try {
            val iBinder = ServiceManagerNative.getService(Constant.I007_SERVICE)
            if (iBinder == null) {
                val i007Service = I007Service()
                ServiceManagerNative.addService(Constant.I007_SERVICE, i007Service)
            } else {
                DebugUtils.w(TAG, "service " + Constant.I007_SERVICE + " already added, it cannot be added once more...")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            DebugUtils.e(TAG, "Exception in add service " + Constant.I007_SERVICE + ": " + e.message)
        }
    }

    override fun isGame(packageName: String?) {
        //TODO
    }

    override fun addGame(source: String?, packageName: String?) {
        //TODO
    }

    override fun removeGame(source: String?, packageName: String?) {
        //TODO
    }

}