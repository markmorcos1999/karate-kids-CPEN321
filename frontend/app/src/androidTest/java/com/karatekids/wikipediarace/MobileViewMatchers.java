package com.karatekids.wikipediarace;

import androidx.test.espresso.Root;

import org.hamcrest.Matcher;
//ChatGPT usage: No
public class MobileViewMatchers {
    //ChatGPT usage: No
    public static Matcher<Root> isToast() {
        return new ToastMatcher();
    }
}
