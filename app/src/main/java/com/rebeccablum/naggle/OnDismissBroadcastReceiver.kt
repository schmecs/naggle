package com.rebeccablum.naggle

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class OnDismissBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.action
        Toast.makeText(context, "Broadcast received", Toast.LENGTH_LONG).show()
        intent?.extras?.getInt(NAG_ID)?.let {

        }
    }
}
