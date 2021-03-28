package com.wainow.island.ui.list.common

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor


internal class MainThreadExecutor : Executor {
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable?) {
        command?.let { mHandler.post(it) }
    }
}