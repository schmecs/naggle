package com.rebeccablum.naggle.ui

import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import com.rebeccablum.naggle.R
import com.rebeccablum.naggle.notif.NAG_ID

const val NO_DESTINATION = -2

class MainActivity : FragmentActivity(R.layout.activity_main) {

    override fun onResume() {
        super.onResume()
        val nagId = intent?.getIntExtra(NAG_ID, -2)
        if (nagId != -2 && nagId != null) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
            val navController = navHostFragment.navController
            val action = NagListFragmentDirections.startNagListFragment(nagId)
            navController.navigate(action)
        }
        intent?.removeExtra(NAG_ID)
    }
}
