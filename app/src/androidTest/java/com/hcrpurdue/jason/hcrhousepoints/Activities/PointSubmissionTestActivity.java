package com.hcrpurdue.jason.hcrhousepoints.Activities;

import androidx.test.espresso.ViewInteraction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class PointSubmissionTestActivity extends BaseTestActivity {
    public void assertIsDisplayed(){
        ViewInteraction textView = onView(
                allOf(withText("Submit Points"),isDisplayed()));
        textView.check(matches(withText("Submit Points")));
    }
}
