package com.cm20314.mapapp.services;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cm20314.mapapp.MainActivity;
import com.cm20314.mapapp.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MapFragmentTests {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void searchContainersTest1(){
        onView(withId(R.id.end_search_view)).perform(typeText("C"));
        onView(withId(R.id.progress_indicator)).check(matches(isDisplayed()));
    }
    @Test
    public void getDirectionsTest(){
        onView(withId(R.id.get_directions_button)).perform(click());
        onView(withId(R.id.progress_indicator)).check(matches(isDisplayed()));
    }
}
