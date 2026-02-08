package iss.nus.edu.sg.appfiles.mobile_ewaste

import android.os.SystemClock
import android.widget.Spinner
import android.widget.TextView
import android.view.View
import androidx.core.os.bundleOf
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import iss.nus.edu.sg.appfiles.mobile_ewaste.testutil.SessionTestUtil
import org.hamcrest.CoreMatchers.anything
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisposeSubmitE2ETest {

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
    fun submitDisposal_success_clearsForm() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // Navigate to Dispose with a selected bin so backend validation can pass.
        scenario!!.onActivity { activity ->
            activity.findViewById<BottomNavigationView>(R.id.bottomNav).selectedItemId = R.id.homeFragment
            val navHost =
                activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHost.navController.navigate(
                R.id.disposeFragment,
                bundleOf(
                    "selectedBinId" to 1,
                    "selectedBinLabel" to "Test Bin"
                )
            )
        }

        onView(withId(R.id.buttonLogDisposal)).check(matches(isDisplayed()))

        // Wait until categories are loaded from API.
        val categoriesLoaded = waitForSpinnerItems(R.id.spCategory, minCount = 2)
        assertTrue("Category data not loaded from API", categoriesLoaded)

        // Select category (position 1 because 0 is placeholder)
        onView(withId(R.id.spCategory)).perform(click())
        onData(anything()).atPosition(1).perform(click())

        // Wait until item types are loaded for selected category.
        val itemTypesLoaded = waitForSpinnerItems(R.id.spItemType, minCount = 2)
        assertTrue("Item type data not loaded from API", itemTypesLoaded)

        // Select item type
        onView(withId(R.id.spItemType)).perform(click())
        onData(anything()).atPosition(1).perform(click())

        // Fill required fields
        onView(withId(R.id.etSerialNo)).perform(
            replaceText("E2E-SN-${System.currentTimeMillis()}"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.etEstimatedWeight)).perform(replaceText("0.5"), closeSoftKeyboard())

        // Submit
        onView(withId(R.id.buttonLogDisposal)).perform(click())

        // Success path clears form in DisposeFragment (serial text becomes empty)
        val cleared = waitForText(R.id.etSerialNo, "", timeoutMs = 10000)
        assertTrue("Submit did not complete successfully (form not cleared)", cleared)

        onView(withId(R.id.etSerialNo)).check(matches(withText("")))
    }

    private fun waitForSpinnerItems(
        spinnerId: Int,
        minCount: Int,
        timeoutMs: Long = 10000
    ): Boolean {
        val start = SystemClock.elapsedRealtime()
        while (SystemClock.elapsedRealtime() - start < timeoutMs) {
            var count = 0
            scenario!!.onActivity { activity ->
                val spinner = activity.findViewById<View>(spinnerId) as? Spinner
                count = spinner?.adapter?.count ?: 0
            }
            if (count >= minCount) return true
            SystemClock.sleep(200)
        }
        return false
    }

    private fun waitForText(
        viewId: Int,
        expected: String,
        timeoutMs: Long = 5000
    ): Boolean {
        val start = SystemClock.elapsedRealtime()
        while (SystemClock.elapsedRealtime() - start < timeoutMs) {
            var text = ""
            scenario!!.onActivity { activity ->
                val view = activity.findViewById<View>(viewId) as? TextView
                text = view?.text?.toString() ?: ""
            }
            if (text == expected) return true
            SystemClock.sleep(200)
        }
        return false
    }
}
