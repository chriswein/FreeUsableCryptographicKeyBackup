package com.keybackup.onboarding;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Button;

import com.keybackup.BaseActivity;
import com.keybackup.Logger;
import com.keybackup.Preferences;
import com.keybackup.R;
import com.keybackup.Utils;
import com.keybackup.main.MainActivity;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnboardingActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    @BindView(R.id.pager_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.pager_button_get_started)
    Button mGetStartedButton;
    @BindView(R.id.pager_indicator)
    CirclePageIndicator mIndicator;

    int mCurrentPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);
        ButterKnife.bind(this);

        initToolbar(R.string.app_name);
        setDisplayHomeAsUpEnabled();

        hideToolbar();

        mViewPager.setAdapter(new OnboardingPagerAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(this);

        mIndicator.setViewPager(mViewPager);
    }

    @OnClick(R.id.pager_button_get_started)
    public void onGetStartedClick() {
        if (OnboardingPagerAdapter.isLastPage(mCurrentPage)) {
            Logger.finishOnboarding();
            Preferences.getPreferences(this).onboardingFinished();
            Utils.startActivity(this, MainActivity.class, true);
        } else {
            mViewPager.setCurrentItem(mCurrentPage + 1, true);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;

        if (OnboardingPagerAdapter.isLastPage(mCurrentPage)) {
            mGetStartedButton.setText(R.string.onboarding_get_started);
        } else {
            mGetStartedButton.setText(R.string.onboarding_next);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // not used here
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // not used here
    }
}
