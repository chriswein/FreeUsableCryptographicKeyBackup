package com.keybackup;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.secret.sharing.AndroidSecretSharing;
import com.android.secret.sharing.QrKeyPartListener;
import com.android.secret.sharing.SecretPresentation;

import github.nisrulz.qreader.QRDataListener;

public abstract class BaseActivity extends AppCompatActivity implements QRDataListener, QrKeyPartListener {
    protected AndroidSecretSharing mSecretSharing;

    private Toolbar mToolbar;
    private ActionBar mActionBar;

    private QRDataListener mQrDataListener;
    private QrKeyPartListener mSecretPartListener;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSecretSharing = new AndroidSecretSharing(this);

        // Remove files (key parts and backups) that were attached to emails from disk. Cannot be done earlier
        // because the email might not be sent when returning back from email app.
        mSecretSharing.cleanEmailAttachmentFolder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSecretSharing.close();
        mQrDataListener = null;
        mSecretPartListener = null;

        Logger.finish();
    }

    protected void initToolbar(int titleResId) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(titleResId);
        setSupportActionBar(mToolbar);

        mActionBar = getSupportActionBar();
    }

    protected void setDisplayHomeAsUpEnabled() {
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public AndroidSecretSharing getSecretSharing() {
        return mSecretSharing;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public void hideToolbar() {
        mToolbar.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mSecretSharing.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onDetected(String secret) {
        if (mQrDataListener != null) {
            mQrDataListener.onDetected(secret);
        }
    }

    @Override
    public void qrCodeDetected(SecretPresentation secret) {
        if (mSecretPartListener != null) {
            mSecretPartListener.qrCodeDetected(secret);
        }
    }

    @Override
    public void wrongSecretPart() {
        if (mSecretPartListener != null) {
            mSecretPartListener.wrongSecretPart();
        }
    }

    public void showSnackbar(String message) {
        View v = getWindow().getDecorView().getRootView();
        if (mSnackbar == null || !mSnackbar.isShownOrQueued()) {
            mSnackbar = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
            mSnackbar.show();
        }

        v.bringToFront();
        v.invalidate();
    }
}
