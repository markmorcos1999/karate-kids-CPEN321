package com.karatekids.wikipediarace;

import android.os.IBinder;
import android.view.WindowManager;

import androidx.test.espresso.Root;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

//ChatGPT usage: No
//https://stackoverflow.com/questions/28390574/checking-toast-message-in-android-espresso/33387980#33387980
public class ToastMatcher extends TypeSafeMatcher<Root> {

    //ChatGPT usage: No
    @Override
    public void describeTo(Description description) {
        description.appendText("is toast");
    }

    //ChatGPT usage: No
    @Override
    public boolean matchesSafely(Root root) {
        int type = root.getWindowLayoutParams().get().type;
        if (type == WindowManager.LayoutParams.TYPE_TOAST) {
            IBinder windowToken = root.getDecorView().getWindowToken();
            IBinder appToken = root.getDecorView().getApplicationWindowToken();
            if (windowToken == appToken) {
                // windowToken == appToken means this window isn't contained by any other windows.
                // if it was a window for an activity, it would have TYPE_BASE_APPLICATION.
                return true;
            }
        }
        return false;
    }
}
