package com.demo.slice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * A handler to handle events from [DemoSliceProvider].
 */
class DemoReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, p1: Intent?) {
        // TODO Get [Intent]s from [DemoSliceProvider].
    }

    companion object {
        fun send(context: Context) {
            context.applicationContext.sendBroadcast(Intent(context, DemoReceiver::class.java))
        }
    }
}