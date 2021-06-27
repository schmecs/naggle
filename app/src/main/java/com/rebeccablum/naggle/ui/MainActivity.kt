package com.rebeccablum.naggle.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import com.rebeccablum.naggle.R
import com.rebeccablum.naggle.notif.ACTION_EDIT_NAG
import com.rebeccablum.naggle.notif.NAG_ID

const val NO_DESTINATION = -2

class MainActivity : FragmentActivity(R.layout.activity_main) {

    private val editNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_EDIT_NAG) {
                goToNag(intent.getIntExtra(NAG_ID, NO_DESTINATION))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ACTION_EDIT_NAG)
        registerReceiver(editNotificationReceiver, filter)
    }

    override fun onPause() {
        unregisterReceiver(editNotificationReceiver)
        super.onPause()
    }

    private fun goToNag(nagId: Int) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        val action = NagListFragmentDirections.startNagListFragment(nagId)
        navController.navigate(action)
    }
}
