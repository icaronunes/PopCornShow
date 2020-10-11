package main

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import configuracao.SettingsActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class HelloWorldEspressoTest {

	@get:Rule
	val activityRule = ActivityTestRule(SettingsActivity::class.java, false , true)

	@Test
	fun listGoesOverTheFold() {
		onView(withText("Configurações")).check(matches(isDisplayed()))
	}
}