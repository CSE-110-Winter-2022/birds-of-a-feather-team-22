package com.example.birdsofafeather;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestWaveAndFavoriteProperDisplayEspresso {

    @Rule
    public ActivityTestRule<NameActivity> mActivityTestRule = new ActivityTestRule<>(NameActivity.class);

    @Test
    public void testWaveAndFavoriteProperDisplayEspresso() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.confirm_button), withText("Confirm"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.submit_button), withText("Submit"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.subject_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                10),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("CSE"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.number_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("110"), closeSoftKeyboard());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.quarter_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.year_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                9),
                        isDisplayed()));
        appCompatSpinner2.perform(click());

        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(3);
        appCompatCheckedTextView2.perform(click());

        ViewInteraction appCompatSpinner3 = onView(
                allOf(withId(R.id.class_size_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatSpinner3.perform(click());

        DataInteraction appCompatCheckedTextView3 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView3.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.enter_button), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.number_view), withText("110"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText3.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.number_view), withText("110"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("11"));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.number_view), withText("11"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        appCompatEditText5.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.enter_button), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.done_button), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.nearby_button), withText("Nearby"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton6.perform(click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.mocking_input_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText("a,,,,\nBill,,,,\n,,,,\n2020,FA,CSE,110,Tiny\n"), closeSoftKeyboard());

        ViewInteraction materialTextView = onView(
                allOf(withId(R.id.self_profile_id_view), withText("388ae8cd-8839-4edb-8063-bd2d8dc2e927"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        materialTextView.perform(click());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nBill,,,,\n,,,,\n2020,FA,CSE,110,Tiny\n"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText7.perform(replaceText("a,,,,\nBill,,,,\n,,,,\n2020,FA,CSE,110,Tiny\n388ae8cd-8839-4edb-8063-bd2d8dc2e927,wave,,,"));

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nBill,,,,\n,,,,\n2020,FA,CSE,110,Tiny\n388ae8cd-8839-4edb-8063-bd2d8dc2e927,wave,,,"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText8.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.mock_enter_button), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton7.perform(click());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.mocking_input_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText9.perform(click());

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.mocking_input_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText10.perform(replaceText("a,,,,\nBill,,,,\n,,,,\n2020,FA,CSE,110,Tiny\n388ae8cd-8839-4edb-8063-bd2d8dc2e927,wave,,,"), closeSoftKeyboard());

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nBill,,,,\n,,,,\n2020,FA,CSE,110,Tiny\n388ae8cd-8839-4edb-8063-bd2d8dc2e927,wave,,,"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText11.perform(click());

        ViewInteraction appCompatEditText12 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nBill,,,,\n,,,,\n2020,FA,CSE,110,Tiny\n388ae8cd-8839-4edb-8063-bd2d8dc2e927,wave,,,"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText12.perform(click());

        ViewInteraction appCompatEditText13 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nBill,,,,\n,,,,\n2020,FA,CSE,110,Tiny\n388ae8cd-8839-4edb-8063-bd2d8dc2e927,wave,,,"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText13.perform(replaceText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,110,Tiny\n388ae8cd-8839-4edb-8063-bd2d8dc2e927,wave,,,"));

        ViewInteraction appCompatEditText14 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,110,Tiny\n388ae8cd-8839-4edb-8063-bd2d8dc2e927,wave,,,"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText14.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText15 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,110,Tiny\n388ae8cd-8839-4edb-8063-bd2d8dc2e927,wave,,,"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText15.perform(click());

        ViewInteraction appCompatEditText16 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,110,Tiny\n388ae8cd-8839-4edb-8063-bd2d8dc2e927,wave,,,"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText16.perform(click());

        ViewInteraction appCompatEditText17 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,110,Tiny\n388ae8cd-8839-4edb-8063-bd2d8dc2e927,wave,,,"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText17.perform(replaceText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,11,Tiny\n388ae8cd-8839-4edb-8063-bd2d8dc2e927,wave,,,"));

        ViewInteraction appCompatEditText18 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,11,Tiny\n388ae8cd-8839-4edb-8063-bd2d8dc2e927,wave,,,"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText18.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText19 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,11,Tiny\n388ae8cd-8839-4edb-8063-bd2d8dc2e927,wave,,,"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText19.perform(click());

        ViewInteraction appCompatEditText20 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,11,Tiny\n388ae8cd-8839-4edb-8063-bd2d8dc2e927,wave,,,"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText20.perform(replaceText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,11,Tiny\n388ae8cd-8839-4edb-"));

        ViewInteraction appCompatEditText21 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,11,Tiny\n388ae8cd-8839-4edb-"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText21.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText22 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,11,Tiny\n388ae8cd-8839-4edb-"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText22.perform(click());

        ViewInteraction appCompatEditText23 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,11,Tiny\n388ae8cd-8839-4edb-"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText23.perform(replaceText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,11,Tiny\n"));

        ViewInteraction appCompatEditText24 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,11,Tiny\n"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText24.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.mock_enter_button), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton8.perform(click());

        ViewInteraction appCompatButton9 = onView(
                allOf(withId(R.id.start_button), withText("Start"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton9.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.session_recycler_view),
                        childAtPosition(
                                withId(R.id.classes_layout),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction appCompatButton10 = onView(
                allOf(withId(R.id.stop_button), withText("Stop"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton10.perform(click());

        ViewInteraction appCompatEditText25 = onView(
                allOf(withId(R.id.session_name_input_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText25.perform(replaceText("CSE 110"), closeSoftKeyboard());

        ViewInteraction appCompatButton11 = onView(
                allOf(withId(R.id.submit_session_name_button), withText("Submit"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton11.perform(click());

        ViewInteraction appCompatButton12 = onView(
                allOf(withId(R.id.nearby_button), withText("Nearby"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton12.perform(click());

        ViewInteraction appCompatEditText26 = onView(
                allOf(withId(R.id.mocking_input_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText26.perform(replaceText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,11,Tiny\n"), closeSoftKeyboard());

        ViewInteraction appCompatEditText27 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,11,Tiny\n"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText27.perform(click());

        ViewInteraction appCompatEditText28 = onView(
                allOf(withId(R.id.mocking_input_view), withText("a,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,11,Tiny\n"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText28.perform(replaceText("ab,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,11,Tiny\n"));

        ViewInteraction appCompatEditText29 = onView(
                allOf(withId(R.id.mocking_input_view), withText("ab,,,,\nJim,,,,\n,,,,\n2020,FA,CSE,11,Tiny\n"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText29.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton13 = onView(
                allOf(withId(R.id.mock_enter_button), withText("Enter"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton13.perform(click());

        ViewInteraction appCompatButton14 = onView(
                allOf(withId(R.id.start_button), withText("Start"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton14.perform(click());

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.session_recycler_view),
                        childAtPosition(
                                withId(R.id.classes_layout),
                                0)));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction appCompatButton15 = onView(
                allOf(withId(R.id.stop_button), withText("Stop"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton15.perform(click());

        ViewInteraction appCompatEditText30 = onView(
                allOf(withId(R.id.session_name_input_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText30.perform(replaceText("CSE 110"), closeSoftKeyboard());

        ViewInteraction appCompatButton16 = onView(
                allOf(withId(R.id.submit_session_name_button), withText("Submit"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton16.perform(click());

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.star),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        0),
                                2),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.match_name_view), withText("Bill"),
                        withParent(allOf(withId(R.id.match_layout),
                                withParent(withId(R.id.belowLayout)))),
                        isDisplayed()));
        textView.check(matches(withText("Bill")));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.wave),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.match_name_view), withText("Jim"),
                        withParent(allOf(withId(R.id.match_layout),
                                withParent(withId(R.id.belowLayout)))),
                        isDisplayed()));
        textView2.check(matches(withText("Jim")));

        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.star),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        imageView2.check(matches(isDisplayed()));
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
