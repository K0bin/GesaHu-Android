package rhedox.gesahuvertretungsplan.test.uiTest;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;

/**
 * Created by Robin on 21.05.2016.
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent result = new Intent(targetContext, MainActivity.class);
            result.putExtra(MainActivity.EXTRA_DATE, new DateTime(2016,5,25,0,0).getMillis());
            return result;
        }
    };

    @Test
    public void changeFragment() throws InterruptedException {
        onView(withId(R.id.viewPager)).perform(swipeLeft(), swipeLeft(), swipeRight(), swipeRight());
        Thread.sleep(1000);
        onView(withId(R.id.fab)).check(matches(isDisplayed())).check(matches(isEnabled())).perform(click());
        onView(withText("Ankündigungen")).perform(pressBack());
    }

    @Test
    public void selectSubstitute() throws InterruptedException {
        onView(withId(R.id.viewPager)).perform(swipeDown());
        Thread.sleep(1000);
        onView(withText("Ethik 09abcd")).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.mcab_toolbar)).check(matches(isDisplayed())).check(matches(isEnabled()));
    }
}
