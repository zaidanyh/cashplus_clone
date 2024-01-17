package com.pasukanlangit.id.core.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors {
    private val networkIO: Executor
    private val diskIO: Executor
    private val mainThread: Executor

    init {
        this.networkIO = Executors.newSingleThreadExecutor()
        this.diskIO = Executors.newFixedThreadPool(3)
        this.mainThread = MainThreadExecutor()
    }

    fun getNetworkIO(): Executor {
        return networkIO
    }

    fun getDiskIO(): Executor {
        return diskIO
    }

    fun mainThread(): Executor {
        return mainThread
    }

    private class MainThreadExecutor: Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable?) {
            command?.let { mainThreadHandler.post(it) }
        }
    }
}