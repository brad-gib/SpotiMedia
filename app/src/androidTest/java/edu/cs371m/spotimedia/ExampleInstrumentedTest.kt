package edu.cs371m.spotimedia

import androidx.test.rule.ActivityTestRule


import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import edu.cs371m.spotimedia.RecyclerViewChildActions.Companion.childOfViewAtPositionWithMatcher
import org.hamcrest.CoreMatchers.equalTo


/**
 * [Testing Fundamentals](http://d.android.com/tools/testing/testing_android.html)
 */
// the setup for this test is based on TestFetch that's available on the original project
// I need something like fetch complete to populate the list
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class InstrumentedApplicationTest {

    @Rule @JvmField
    var mActivityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    // Warning, these don't work.
    @Test
    fun testFirstElement() {

    }
}