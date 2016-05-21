package rhedox.gesahuvertretungsplan.test.uiTest;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.SmallTest;

import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import rhedox.gesahuvertretungsplan.R;
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity;
import rhedox.gesahuvertretungsplan.ui.activity.WelcomeActivity;

/**
 * Created by Robin on 21.05.2016.
 */
@SmallTest
public class WelcomeActivityTest {
    @Rule
    public ActivityTestRule<WelcomeActivity> activityTestRule = new ActivityTestRule<WelcomeActivity>(WelcomeActivity.class);

    @Test
    public void testSwiping() throws InterruptedException {
        //onView(withId(R.id.viewPager)).perform(swipeLeft(), swipeRight(), swipeLeft(), swipeRight(), swipeLeft(), swipeRight(), swipeLeft(), swipeRight(), swipeRight(), swipeRight());
    }

    @Test
    public void testClicking() throws InterruptedException {
        onView(withId(R.id.fab)).perform(click(), click(), click());
    }
}
