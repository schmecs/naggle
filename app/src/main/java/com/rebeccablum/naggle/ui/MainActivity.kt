package com.rebeccablum.naggle.ui

import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import com.rebeccablum.naggle.R
import com.rebeccablum.naggle.notif.NAG_ID

class MainActivity : FragmentActivity(R.layout.activity_main) {

    override fun onResume() {
        super.onResume()
        val nagId = intent?.getIntExtra(NAG_ID, -1)
        if (nagId != -1 && nagId != null) {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
            val navController = navHostFragment.navController
            val action = NagListFragmentDirections.startNagListFragment(nagId)
            intent?.putExtra(NAG_ID, -1)
            navController.navigate(action)
        }
        super.onNewIntent(intent)
    }
}
