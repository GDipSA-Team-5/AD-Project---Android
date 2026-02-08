package iss.nus.edu.sg.appfiles.mobile_ewaste

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.bottomnavigation.BottomNavigationView
import iss.nus.edu.sg.appfiles.mobile_ewaste.testutil.SessionTestUtil
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BottomNavSmokeTest {

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
    fun bottomNav_canNavigateHomeToRewardsAndBack() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.homeTitle)).check(matches(isDisplayed()))

        // Select Rewards tab
        scenario!!.onActivity { activity ->
            activity.findViewById<BottomNavigationView>(R.id.bottomNav).selectedItemId =
                R.id.rewardsFragment
        }
        onView(withId(R.id.buttonRedeem)).check(matches(isDisplayed()))

        // Select Home tab
        scenario!!.onActivity { activity ->
            activity.findViewById<BottomNavigationView>(R.id.bottomNav).selectedItemId =
                R.id.homeFragment
        }
        onView(withId(R.id.homeTitle)).check(matches(isDisplayed()))
    }
}
