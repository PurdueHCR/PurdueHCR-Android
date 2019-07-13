package com.hcrpurdue.jason.hcrhousepoints.Tests;


import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;

import com.hcrpurdue.jason.hcrhousepoints.Activities.PointSubmissionTestActivity;

import org.junit.Test;
import org.junit.runner.RunWith;



@LargeTest
@RunWith(AndroidJUnit4.class)
public class LogInTest extends BaseTest{


    @Test
    public void logInTest() {
        PointSubmissionTestActivity activity = baseActivity.logInResident();
        activity.assertIsDisplayed();
    }

    @Test
    public void emptyFieldLogInTest(){

    }

    @Test
    public void invalidEmailTest(){

    }

    @Test
    public void invalidPasswordTest(){

    }

    @Test
    public void transitionToCreateAccountTest(){

    }

}
