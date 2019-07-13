package com.hcrpurdue.jason.hcrhousepoints.Activities;

import androidx.test.espresso.ViewInteraction;

import com.hcrpurdue.jason.hcrhousepoints.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

public class LogInTestActivity extends BaseTestActivity {

    public static final String RESIDENT_EMAIL = "UITestHonorsResident@purdue.edu";
    public static final String RHP_EMAIL = "UITestRHP@purdue.edu";
    public static final String REC_EMAIL = "UITestREC@purdue.edu";
    public static final String SHREVE_RESIDENT_EMAIL = "UITestShreveResident@purdue.edu";
    public static final String ACCOUNT_PASSWORD = "Honors1";


    ViewInteraction emailEditText;
    ViewInteraction passwordEditText;
    ViewInteraction logInButton;
    ViewInteraction createAccountButton;

    public LogInTestActivity(){
        emailEditText = onView(allOf(withId(R.id.email_input), childAtPosition(
                childAtPosition( withId(R.id.email_input_layout),0),0),isDisplayed()));
        passwordEditText = onView(allOf(withId(R.id.password_input), childAtPosition(
                childAtPosition( withId(R.id.password_input_layout),0),0),isDisplayed()));
        logInButton = onView(allOf(withId(R.id.log_in_button),childAtPosition(
                childAtPosition(withId(android.R.id.content),0),2),isDisplayed()));
        createAccountButton = onView(allOf(withId(R.id.create_account_button),childAtPosition(
                childAtPosition(withId(android.R.id.content),0),2),isDisplayed()));
    }

    public void typeEmailText(String email){
        typeTextToField(emailEditText,email);
    }

    public void typePasswordText(String password){
        typeTextToField(passwordEditText,password);
    }

    public void clickLogInButton(){
        logInButton.perform(click());
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clickCreateAccountButton(){
        createAccountButton.perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public PointSubmissionTestActivity logIn(String email, String password){
        typeEmailText(email);
        typePasswordText(password);
        clickLogInButton();
        return new PointSubmissionTestActivity();
    }

    public PointSubmissionTestActivity logInResident(){
        return logIn(RESIDENT_EMAIL,ACCOUNT_PASSWORD);
    }

    public PointSubmissionTestActivity logInRHP(){
        return logIn(RHP_EMAIL,ACCOUNT_PASSWORD);
    }

}
