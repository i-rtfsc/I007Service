package com.journeyOS.i007Service.task

import com.journeyOS.liteprovider.utils.LogUtils
import java.util.concurrent.*


object TaskManager {

    private val TIME_OUT = (6 * 1000).toLong()

    lateinit var mCoreExecutors: CoreExecutors

    init {
        mCoreExecutors = CoreExecutors()
    }

    fun diskIOThread(): Executor {
        return mCoreExecutors.diskIO()
    }

    fun networkIOThread(): Executor {
        return mCoreExecutors.networkIO()
    }

    fun mainThread(): Executor {
        return mCoreExecutors.mainThread()
    }

    /**
     * get data from background
     *
     * @param task background task
     * @return data
     */
    @Synchronized
    fun runBackgroundTask(task: Callable<Any>): Any? {
        val executor = diskIOThread() as ExecutorService
        val ft = FutureTask(task)
        try {
            executor.submit(ft)
            return ft.get(TIME_OUT, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            ft.cancel(true)
            LogUtils.d("interrupted exception = $e")
            e.printStackTrace()
        } catch (e: ExecutionException) {
            ft.cancel(true)
            LogUtils.d("execution exception = $e")
            e.printStackTrace()
        } catch (e: TimeoutException) {
            ft.cancel(true)
            LogUtils.d("timeout exception = $e")
            e.printStackTrace()
        }

        return null
    }
}