package iss.nus.edu.sg.appfiles.mobile_ewaste

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import iss.nus.edu.sg.appfiles.mobile_ewaste.testutil.SessionTestUtil
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeSeeAllHistoryTest {

    private var scenario: ActivityScenario<MainActivity>? = null

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        SessionTestUtil.login(context, userId = 1)
    }

    @After
    fun tearDown() {
        scenario?.close()
        scenario = null
    }

    @Test
    fun seeAll_opensHistory_andHomeReselectReturnsHome() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.homeTitle)).check(matches(isDisplayed()))
        scenario!!.onActivity { activity ->
            activity.findViewById<android.widget.TextView>(R.id.recentSeeAll).performClick()
        }
        waitForDestination(R.id.historyFragment)

        // Home tab is likely already selected; re-select should still return home
        scenario!!.onActivity { activity ->
            activity.findViewById<BottomNavigationView>(R.id.bottomNav).selectedItemId =
                R.id.homeFragment
        }
        onView(withId(R.id.homeTitle)).check(matches(isDisplayed()))
    }

    private fun waitForDestination(destinationId: Int, timeoutMs: Long = 3000) {
        val start = System.currentTimeMillis()
        var lastDestination: Int? = null

        while (System.currentTimeMillis() - start < timeoutMs) {
            scenario!!.onActivity { activity ->
                val navHost =
                    activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                lastDestination = navHost.navController.currentDestination?.id
            }

            if (lastDestination == destinationId) {
                return
            }
            Thread.sleep(100)
        }

        assertEquals("Expected to navigate to destination", destinationId, lastDestination)
    }
}
