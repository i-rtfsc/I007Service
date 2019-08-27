package com.journeyOS.i007Service

import android.os.Binder

object ClientSession {

    private val mListener: HashMap<Int, II007Listener> = HashMap()

    private val mModule: HashMap<Long, Int> = HashMap()

    fun insertListener(listener: II007Listener) {
        val callingPid = Binder.getCallingPid()
        mListener.put(callingPid, listener)
    }

    fun removeListener(listener: II007Listener) {
        mListener.forEach {
            if (it.value == listener) {
                mListener.remove(it.key)
            }
        }
    }

    fun insertModule(module: Long) {
        val callingPid = Binder.getCallingPid()
        mModule.put(module, callingPid)
    }

    fun removeModule(module: Long) {
        mModule.remove(module)
    }

    fun notifyClient(module: Long, result: Any) {
        val callingPid = mModule.get(module)
        var listener = mListener.get(callingPid)
        listener?.let {
            listener.onSceneChanged(module, result.toString())
        }
    }


}