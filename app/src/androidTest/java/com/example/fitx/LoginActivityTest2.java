package com.example.fitx;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest2 {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void loginActivityTest2() {
        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.emailField),
                        childAtPosition(
                                childAtPosition(
                                        childAtPosition(
                                                childAtPosition(
                                                        allOf(withId(android.R.id.content),
                                                                childAtPosition(
                                                                        allOf(withId(R.id.decor_content_parent),
                                                                                childAtPosition(
                                                                                        childAtPosition(
                                                                                                withClassName(is("android.widget.LinearLayout")),
                                                                                                1),
                                                                                        0)),
                                                                        0)),
                                                        0),
                                                0),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("ebassman77@gmail.com"), closeSoftKeyboard());

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.passwordField),
                        childAtPosition(
                                childAtPosition(
                                        childAtPosition(
                                                childAtPosition(
                                                        allOf(withId(android.R.id.content),
                                                                childAtPosition(
                                                                        allOf(withId(R.id.decor_content_parent),
                                                                                childAtPosition(
                                                                                        childAtPosition(
                                                                                                withClassName(is("android.widget.LinearLayout")),
                                                                                                1),
                                                                                        0)),
                                                                        0)),
                                                        0),
                                                1),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText2.perform(replaceText("eadg3663"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.loginButton), withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        childAtPosition(
                                                allOf(withId(android.R.id.content),
                                                        childAtPosition(
                                                                allOf(withId(R.id.decor_content_parent),
                                                                        childAtPosition(
                                                                                childAtPosition(
                                                                                        withClassName(is("android.widget.LinearLayout")),
                                                                                        1),
                                                                                0)),
                                                                0)),
                                                0),
                                        2),
                                0),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_exercises), withContentDescription("Exercises"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.navigation),
                                                childAtPosition(
                                                        allOf(withId(R.id.container),
                                                                childAtPosition(
                                                                        allOf(withId(android.R.id.content),
                                                                                childAtPosition(
                                                                                        allOf(withId(R.id.decor_content_parent),
                                                                                                childAtPosition(
                                                                                                        childAtPosition(
                                                                                                                withClassName(is("android.widget.LinearLayout")),
                                                                                                                1),
                                                                                                        0)),
                                                                                        0)),
                                                                        0)),
                                                        1)),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.navigation_home), withContentDescription("Home"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.navigation),
                                                childAtPosition(
                                                        allOf(withId(R.id.container),
                                                                childAtPosition(
                                                                        allOf(withId(android.R.id.content),
                                                                                childAtPosition(
                                                                                        allOf(withId(R.id.decor_content_parent),
                                                                                                childAtPosition(
                                                                                                        childAtPosition(
                                                                                                                withClassName(is("android.widget.LinearLayout")),
                                                                                                                1),
                                                                                                        0)),
                                                                                        0)),
                                                                        0)),
                                                        1)),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.logoutButton), withText("Logout"),
                        childAtPosition(
                                childAtPosition(
                                        childAtPosition(
                                                childAtPosition(
                                                        allOf(withId(R.id.viewpager),
                                                                childAtPosition(
                                                                        allOf(withId(R.id.container),
                                                                                childAtPosition(
                                                                                        allOf(withId(android.R.id.content),
                                                                                                childAtPosition(
                                                                                                        allOf(withId(R.id.decor_content_parent),
                                                                                                                childAtPosition(
                                                                                                                        childAtPosition(
                                                                                                                                withClassName(is("android.widget.LinearLayout")),
                                                                                                                                1),
                                                                                                                        0)),
                                                                                                        0)),
                                                                                        0)),
                                                                        2)),
                                                        0),
                                                0),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton2.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
