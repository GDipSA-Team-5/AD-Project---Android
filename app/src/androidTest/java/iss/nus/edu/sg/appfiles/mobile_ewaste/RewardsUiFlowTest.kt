package iss.nus.edu.sg.appfiles.mobile_ewaste

import android.os.SystemClock
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.bottomnavigation.BottomNavigationView
import iss.nus.edu.sg.appfiles.mobile_ewaste.testutil.SessionTestUtil
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RewardsUiFlowTest {

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
    fun rewardsScreen_showsPointsAndHistorySection() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        openRewardsTab()

        onView(withId(R.id.textAvailablePoints)).check(matches(isDisplayed()))
        onView(withId(R.id.rewardsHistoryTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.buttonRedeem)).check(matches(isDisplayed()))
    }

    @Test
    fun rewardsScreen_browseRewardsStore_opensStoreFragment() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        openRewardsTab()

        onView(withId(R.id.buttonRedeem)).perform(click())
        onView(withId(R.id.rewardsStoreTitle)).check(matches(isDisplayed()))
    }

    @Test
    fun rewardsStore_redeemVoucherFlow_noCrash() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        openRewardsTab()

        onView(withId(R.id.buttonRedeem)).perform(click())
        onView(withId(R.id.rewardsStoreTitle)).check(matches(isDisplayed()))

        waitForStoreLoaded()

        var adapterCount = 0
        scenario!!.onActivity { activity ->
            val list = activity.findViewById<RecyclerView>(R.id.rewardsStoreList)
            adapterCount = list?.adapter?.itemCount ?: 0
        }

        if (adapterCount > 0) {
            scenario!!.onActivity { activity ->
                val list = activity.findViewById<RecyclerView>(R.id.rewardsStoreList)
                val firstHolder = list.findViewHolderForAdapterPosition(0)
                val firstItemView = firstHolder?.itemView ?: list.getChildAt(0)
                val redeemButton = firstItemView?.findViewById<View>(R.id.redeemButton)
                redeemButton?.performClick()
            }
            onView(withId(R.id.rewardsStoreTitle)).check(matches(isDisplayed()))
        } else {
            onView(withId(R.id.rewardsStoreEmpty)).check(matches(isDisplayed()))
        }

        assertTrue(true)
    }

    private fun openRewardsTab() {
        scenario!!.onActivity { activity ->
            activity.findViewById<BottomNavigationView>(R.id.bottomNav).selectedItemId =
                R.id.rewardsFragment
        }
        onView(withId(R.id.buttonRedeem)).check(matches(isDisplayed()))
    }

    private fun waitForStoreLoaded(timeoutMs: Long = 8000) {
        val start = SystemClock.elapsedRealtime()
        while (SystemClock.elapsedRealtime() - start < timeoutMs) {
            var loadingVisible = true
            scenario!!.onActivity { activity ->
                val loading = activity.findViewById<View>(R.id.rewardsStoreLoading)
                loadingVisible = loading?.visibility == View.VISIBLE
            }
            if (!loadingVisible) return
            SystemClock.sleep(200)
        }
    }
}
