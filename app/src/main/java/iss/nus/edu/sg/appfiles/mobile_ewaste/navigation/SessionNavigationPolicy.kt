package iss.nus.edu.sg.appfiles.mobile_ewaste.navigation

import iss.nus.edu.sg.appfiles.mobile_ewaste.R

object SessionNavigationPolicy {
    fun destinationFor(loggedIn: Boolean, currentDestinationId: Int?): Int? {
        return when {
            loggedIn && currentDestinationId == R.id.loginFragment -> R.id.homeFragment
            !loggedIn && currentDestinationId != null && currentDestinationId != R.id.loginFragment ->
                R.id.loginFragment
            else -> null
        }
    }
}
