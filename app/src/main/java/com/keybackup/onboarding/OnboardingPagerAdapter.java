package com.keybackup.onboarding;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class OnboardingPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGES = 4;

    public OnboardingPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return OnboardingFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return PAGES;
    }

    public static boolean isLastPage(int page) {
        return page == PAGES - 1;
    }
}