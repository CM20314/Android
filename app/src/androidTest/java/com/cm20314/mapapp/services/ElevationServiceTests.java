package com.cm20314.mapapp.services;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.google.android.material.internal.ContextUtils.getActivity;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.view.KeyEvent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import com.cm20314.mapapp.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ElevationServiceTests {
    private UiDevice uiDevice;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);
    @Before
    public void Initialisation(){
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            }

    @Test
    public void shouldShowToast() {
        uiDevice.pressKeyCode(KeyEvent.KEYCODE_VOLUME_UP);
        onView(withText("Floor 3")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }
}
