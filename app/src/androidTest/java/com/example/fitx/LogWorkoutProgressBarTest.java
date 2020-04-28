package com.example.fitx;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
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
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LogWorkoutProgressBarTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void logWorkoutProgressBarTest() {
        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.emailField),
                        childAtPosition(
                                childAtPosition(
                                        childAtPosition(
                                                childAtPosition(
                                                        withId(android.R.id.content),
                                                        0),
                                                1),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText.perform(click());

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.emailField),
                        childAtPosition(
                                childAtPosition(
                                        childAtPosition(
                                                childAtPosition(
                                                        withId(android.R.id.content),
                                                        0),
                                                1),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText2.perform(replaceText("brettn@email.sc.edu"), closeSoftKeyboard());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.passwordField),
                        childAtPosition(
                                childAtPosition(
                                        childAtPosition(
                                                childAtPosition(
                                                        withId(android.R.id.content),
                                                        0),
                                                2),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText3.perform(replaceText("abc123"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.loginButton), withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        childAtPosition(
                                                allOf(withId(android.R.id.content),
                                                        childAtPosition(
                                                                withId(R.id.decor_content_parent),
                                                                0)),
                                                0),
                                        3),
                                0),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction listView = onView(
                allOf(withId(R.id.programList),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout),
                                        childAtPosition(
                                                childAtPosition(
                                                        childAtPosition(
                                                                withId(R.id.viewpager),
                                                                0),
                                                        0),
                                                0)),
                                1),
                        isDisplayed()));
        listView.perform(click());

        ViewInteraction listView2 = onView(
                allOf(withId(R.id.program_list),
                        childAtPosition(
                                allOf(withId(R.id.relativeLayout),
                                        childAtPosition(
                                                childAtPosition(
                                                        childAtPosition(
                                                                withId(R.id.viewpager),
                                                                0),
                                                        1),
                                                0)),
                                1),
                        isDisplayed()));
        listView2.perform(click());

        ViewInteraction listView3 = onView(
                allOf(withId(R.id.currentList),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout),
                                        childAtPosition(
                                                childAtPosition(
                                                        childAtPosition(
                                                                withId(R.id.viewpager),
                                                                0),
                                                        0),
                                                0)),
                                2),
                        isDisplayed()));
        listView3.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.log), withText("Log"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout),
                                        childAtPosition(
                                                childAtPosition(
                                                        childAtPosition(
                                                                withId(R.id.viewpager),
                                                                0),
                                                        0),
                                                0)),
                                8),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.num_reps),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(android.R.id.custom),
                                                childAtPosition(
                                                        allOf(withClassName(is("android.widget.FrameLayout")),
                                                                childAtPosition(
                                                                        withClassName(is("com.android.internal.widget.AlertDialogLayout")),
                                                                        2)),
                                                        0)),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("12"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.num_weight),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(android.R.id.custom),
                                                childAtPosition(
                                                        allOf(withClassName(is("android.widget.FrameLayout")),
                                                                childAtPosition(
                                                                        withClassName(is("com.android.internal.widget.AlertDialogLayout")),
                                                                        2)),
                                                        0)),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("20"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(android.R.id.button1), withText("Log"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withClassName(is("android.widget.ScrollView")),
                                                childAtPosition(
                                                        allOf(withClassName(is("com.android.internal.widget.AlertDialogLayout")),
                                                                childAtPosition(
                                                                        withId(android.R.id.content),
                                                                        0)),
                                                        3)),
                                        0),
                                3)));
        appCompatButton3.perform(scrollTo(), click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.log), withText("Log"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout),
                                        childAtPosition(
                                                childAtPosition(
                                                        childAtPosition(
                                                                withId(R.id.viewpager),
                                                                0),
                                                        0),
                                                0)),
                                8),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.num_reps),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(android.R.id.custom),
                                                childAtPosition(
                                                        allOf(withClassName(is("android.widget.FrameLayout")),
                                                                childAtPosition(
                                                                        withClassName(is("com.android.internal.widget.AlertDialogLayout")),
                                                                        2)),
                                                        0)),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("12"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.num_weight),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(android.R.id.custom),
                                                childAtPosition(
                                                        allOf(withClassName(is("android.widget.FrameLayout")),
                                                                childAtPosition(
                                                                        withClassName(is("com.android.internal.widget.AlertDialogLayout")),
                                                                        2)),
                                                        0)),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("20"), closeSoftKeyboard());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(android.R.id.button1), withText("Log"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withClassName(is("android.widget.ScrollView")),
                                                childAtPosition(
                                                        allOf(withClassName(is("com.android.internal.widget.AlertDialogLayout")),
                                                                childAtPosition(
                                                                        withId(android.R.id.content),
                                                                        0)),
                                                        3)),
                                        0),
                                3)));
        appCompatButton5.perform(scrollTo(), click());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.log), withText("Log"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout),
                                        childAtPosition(
                                                childAtPosition(
                                                        childAtPosition(
                                                                withId(R.id.viewpager),
                                                                0),
                                                        0),
                                                0)),
                                8),
                        isDisplayed()));
        appCompatButton6.perform(click());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.num_reps),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(android.R.id.custom),
                                                childAtPosition(
                                                        allOf(withClassName(is("android.widget.FrameLayout")),
                                                                childAtPosition(
                                                                        withClassName(is("com.android.internal.widget.AlertDialogLayout")),
                                                                        2)),
                                                        0)),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("12"), closeSoftKeyboard());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.num_weight),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(android.R.id.custom),
                                                childAtPosition(
                                                        allOf(withClassName(is("android.widget.FrameLayout")),
                                                                childAtPosition(
                                                                        withClassName(is("com.android.internal.widget.AlertDialogLayout")),
                                                                        2)),
                                                        0)),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText("10"), closeSoftKeyboard());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(android.R.id.button1), withText("Log"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withClassName(is("android.widget.ScrollView")),
                                                childAtPosition(
                                                        allOf(withClassName(is("com.android.internal.widget.AlertDialogLayout")),
                                                                childAtPosition(
                                                                        withId(android.R.id.content),
                                                                        0)),
                                                        3)),
                                        0),
                                3)));
        appCompatButton7.perform(scrollTo(), click());

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.logoutButton), withText("Logout"),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout),
                                        childAtPosition(
                                                childAtPosition(
                                                        childAtPosition(
                                                                withId(R.id.viewpager),
                                                                0),
                                                        0),
                                                0)),
                                9),
                        isDisplayed()));
        appCompatButton8.perform(click());
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
