package iss.nus.edu.sg.appfiles.mobile_ewaste

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import iss.nus.edu.sg.appfiles.mobile_ewaste.testutil.SessionTestUtil
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginUiValidationTest {

    private var scenario: ActivityScenario<MainActivity>? = null

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        SessionTestUtil.logout(context)
    }

    @After
    fun tearDown() {
        scenario?.close()
        scenario = null
    }

    @Test
    fun login_withEmptyFields_showsRequiredErrors() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.buttonLogin)).perform(click())

        onView(withId(R.id.loginEmail)).check(matches(hasErrorText("Email is required")))
        onView(withId(R.id.loginPassword)).check(matches(hasErrorText("Password is required")))
    }

    @Test
    fun login_withInvalidEmail_showsInvalidEmailError() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.loginEmail)).perform(replaceText("invalid-email"), closeSoftKeyboard())
        onView(withId(R.id.loginPassword)).perform(replaceText("secret123"), closeSoftKeyboard())
        onView(withId(R.id.buttonLogin)).perform(click())

        onView(withId(R.id.loginEmail)).check(matches(hasErrorText("Invalid email")))
    }

    @Test
    fun login_screenCanNavigateToCreateAccount() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.registerLink)).perform(click())

        onView(withId(R.id.createAccountTitle)).check(matches(isDisplayed()))
    }
}
