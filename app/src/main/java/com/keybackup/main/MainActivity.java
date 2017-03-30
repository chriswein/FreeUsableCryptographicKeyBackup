package com.keybackup.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.webkit.WebView;

import com.android.secret.sharing.QrKeyPartListener;
import com.android.secret.sharing.SecretPresentation;
import com.keybackup.BaseActivity;
import com.keybackup.Logger;
import com.keybackup.Preferences;
import com.keybackup.R;
import com.keybackup.Utils;
import com.keybackup.stepperfragments.createbackup.CreateBackupDialog;
import com.keybackup.stepperfragments.initkey.InitKeyDialog;
import com.keybackup.stepperfragments.restorebackup.RestoreDialog;
import com.keybackup.fullscreenfragments.FullscreenFragmentActivity;
import com.keybackup.fullscreenfragments.qrcodereader.QrReaderFragment;
import com.keybackup.main.backup.BackupFragment;
import com.keybackup.main.keypart.KeyPartFragment;
import com.keybackup.main.todo.TodoFragment;
import com.keybackup.onboarding.OnboardingActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    private static final int REQUEST_QR_PART = 7693;

    @BindView(R.id.main_tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.main_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.main_fab)
    FloatingActionButton mFab;

    private int mCurrentPage;

    private Preferences mPreferences;
    private QrKeyPartListener mKeyPartListener;
    private CreateBackupDialog.BackupCreatorListener mBackupCreatorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.init(this);

        mPreferences = Preferences.getPreferences(this);

        if (!mPreferences.isOnboardingFinished()) {
            Logger.startOnboarding(-1);
            Utils.startActivity(this, OnboardingActivity.class, true);
            return;
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar(R.string.app_name);

        mViewPager.setAdapter(new MainAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(this);

        mFab.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mFab.animate().translationY(mFab.getHeight() + mFab.getPaddingBottom()).setDuration(0);
                mFab.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        Intent intent = getIntent();
        String intentType = intent.getType();
        String secret = null;
        String name = null;
        if ("com.keybackup.SECRET".equals(intent.getAction()) && intentType != null) {
            if (intentType.equals("text/plain")) {
                secret = intent.getStringExtra(Intent.EXTRA_TEXT);
                name = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
            }
            if (secret != null && name != null) {
                showBackupDialog(secret,name);
            } else if(secret != null){
                showBackupDialog(secret);
            }
        }
    }

    public void setKeyPartListener(QrKeyPartListener listener) {
        mKeyPartListener = listener;
    }

    public void setBackupCreatorListener(CreateBackupDialog.BackupCreatorListener listener) {
        mBackupCreatorListener = listener;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_help) {
            Logger.startOnboarding(mCurrentPage);
            Utils.startActivity(this, OnboardingActivity.class, false);
            return true;
        } else if (id == R.id.action_licenses) {
            showLicenses();
            return true;
        } else if (id == R.id.action_restore) {
            Utils.openFullscreenDialog(this, RestoreDialog.newInstance(null), RestoreDialog.TAG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLicenses() {
        WebView webView = new WebView(this);
        webView.loadUrl("file:///android_asset/open_source_licenses.html");

        new AlertDialog.Builder(this)
                .setTitle(R.string.action_licenses)
                .setView(webView)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_QR_PART) {
            if (resultCode == Activity.RESULT_OK && mKeyPartListener != null) {
                SecretPresentation key = data.getParcelableExtra(QrReaderFragment.RESULT_KEY_PART);
                mKeyPartListener.qrCodeDetected(key);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            mFab.animate().translationY(mFab.getHeight() + mFab.getPaddingBottom()).setDuration(200);
        } else if (mCurrentPage == 0) {
            mFab.animate().translationY(0).setDuration(200);
        }

        mCurrentPage = position;
    }

    @OnClick(R.id.main_fab)
    public void onFabClick() {
        if (mCurrentPage == 1) {
            if (mPreferences.isContainerCreated()) {
                Logger.newBackup();
                showBackupDialog(null);
            } else {
                Logger.startSplitKey("New Backup (+ Button)");
                Utils.openFullscreenDialog(this, InitKeyDialog.newInstance(true), InitKeyDialog.TAG);
            }
        } else {
            Utils.startActivity(this, FullscreenFragmentActivity.class, false, FullscreenFragmentActivity.getQrReaderArguments(true, false), REQUEST_QR_PART);
        }
    }

    private void showBackupDialog(String secret, String name) {
        CreateBackupDialog dialog = CreateBackupDialog.newInstance(secret, name);
        dialog.setBackupCreatorListener(mBackupCreatorListener);

        Utils.openFullscreenDialog(this, dialog, CreateBackupDialog.TAG);
    }

    private void showBackupDialog(String secret) {
        CreateBackupDialog dialog = CreateBackupDialog.newInstance(secret);
        dialog.setBackupCreatorListener(mBackupCreatorListener);

        Utils.openFullscreenDialog(this, dialog, CreateBackupDialog.TAG);
    }

    private class MainAdapter extends FragmentPagerAdapter {

        public MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TodoFragment();
                case 1:
                    return BackupFragment.newInstance();
                case 2:
                    return KeyPartFragment.newInstance();
                default:
                    throw new IllegalStateException("No fragment specified for page " + position);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.main_tab_todo);
                case 1:
                    return getString(R.string.main_tab_backups);
                case 2:
                    return getString(R.string.main_tab_key_parts);
                default:
                    throw new IllegalStateException("No title specified for page " + position);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageScrollStateChanged(int state) {}
}
