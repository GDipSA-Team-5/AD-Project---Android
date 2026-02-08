package iss.nus.edu.sg.appfiles.mobile_ewaste

import iss.nus.edu.sg.appfiles.mobile_ewaste.navigation.SessionNavigationPolicy
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionNavigationPolicyTest {

    @Test
    fun destinationFor_returnsHome_whenLoggedInAndOnLoginScreen() {
        val target = SessionNavigationPolicy.destinationFor(
            loggedIn = true,
            currentDestinationId = R.id.loginFragment
        )

        assertEquals(R.id.homeFragment, target)
    }

    @Test
    fun destinationFor_returnsLogin_whenLoggedOutAndOnProtectedScreen() {
        val target = SessionNavigationPolicy.destinationFor(
            loggedIn = false,
            currentDestinationId = R.id.rewardsFragment
        )

        assertEquals(R.id.loginFragment, target)
    }

    @Test
    fun destinationFor_returnsNull_whenLoggedInAndNotOnLoginScreen() {
        val target = SessionNavigationPolicy.destinationFor(
            loggedIn = true,
            currentDestinationId = R.id.homeFragment
        )

        assertEquals(null, target)
    }

    @Test
    fun destinationFor_returnsNull_whenLoggedOutAndOnLoginScreen() {
        val target = SessionNavigationPolicy.destinationFor(
            loggedIn = false,
            currentDestinationId = R.id.loginFragment
        )

        assertEquals(null, target)
    }
}
