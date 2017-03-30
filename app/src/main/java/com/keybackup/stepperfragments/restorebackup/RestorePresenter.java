package com.keybackup.stepperfragments.restorebackup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.SurfaceView;
import android.view.View;

import com.android.secret.sharing.AndroidSecretSharing;
import com.android.secret.sharing.Backup;
import com.android.secret.sharing.Container;
import com.android.secret.sharing.KeyPart;
import com.android.secret.sharing.QrReader;
import com.keybackup.BaseActivity;
import com.keybackup.R;
import com.keybackup.Utils;
import com.keybackup.stepperfragments.StepScanSecret;
import com.keybackup.fullscreenfragments.FullscreenFragmentActivity;

import github.nisrulz.qreader.QRDataListener;

public class RestorePresenter implements RestoreContract.UserActionListener, View.OnClickListener, StepScanSecret.QrStepListener, QRDataListener {
    static final int REQUEST_MISSING_PARTS = 4587;

    private RestoreContract.View mView;
    private BaseActivity mActivity;
    private AndroidSecretSharing mSecretSharing;
    private QrReader mQrReader;
    private KeyPart[] mKeyParts;
    private int mMissingKeyParts;
    private String mEncryptedBackup;
    private Container mContainer;
    private boolean mForceKeyPartCollection;
    private SurfaceView mSurfaceView;
    private Backup mBackup;

    private Fragment mFragment;

    public RestorePresenter(Fragment fragment, RestoreContract.View view, BaseActivity activity, Backup backup) {
        mFragment = fragment;
        mBackup = backup;

        mView = view;
        mActivity = activity;
        mForceKeyPartCollection = backup == null;
    }

    @Override
    public void showCamera(AndroidSecretSharing secretSharing) {
        mSecretSharing = secretSharing;

        if (mForceKeyPartCollection) {
            mContainer = mSecretSharing.newContainer();
        } else {
            mContainer = mSecretSharing.getContainer();
        }

        mQrReader = mSecretSharing.readQrCode(mActivity, mSurfaceView, this);
        mQrReader.showQRReader();

        if (!mForceKeyPartCollection) {
            setKeyParts(null);
        }
    }

    @Override
    public void onClick(View v) {
        // click on show contact list button

        Bundle bundle = FullscreenFragmentActivity.getContactListReceiveArguments(mForceKeyPartCollection ? 0 : mMissingKeyParts);
        Utils.startActivity(mFragment, FullscreenFragmentActivity.class, bundle, REQUEST_MISSING_PARTS);
    }

    @Override
    public void initQrReader(SurfaceView view) {
        mSurfaceView = view;
    }

    @Override
    public boolean enableTextInput() {
        return false;
    }

    @Override
    public View createStepContentView(int stepNumber) {
        switch (stepNumber) {
            case 0:
                return new StepCollectKeyParts(mActivity, this);
            case 1:
                if (mBackup == null || !mBackup.isAvailableInCloudStorage()) {
                    return new StepScanSecret(mActivity, this, mBackup != null ? mBackup.getStoreMethod() : null);
                }
            default:
                throw new IllegalArgumentException("No view defined for step " + stepNumber);
        }
    }

    @Override
    public void onStepOpening(int stepNumber) {
        switch (stepNumber) {
            case 0:
                if (mForceKeyPartCollection || mSecretSharing == null) {
                    mView.stepNotCompleted();
                } else {
                    setKeyParts(null);
                }
                break;
            case 1:
                if (mBackup.isAvailableInCloudStorage()) {
                    mView.stepCompleted();
                    return;
                }

                if (mQrReader != null) {
                    mQrReader.restart();
                }

                if (mEncryptedBackup == null) {
                    mView.stepNotCompleted();
                }


                break;
        }
    }

    @Override
    public void setKeyParts(KeyPart[] keyParts) {
        mView.stepNotCompleted();

        // reload all saved key parts if not a backup from another device is restored
        mKeyParts = mForceKeyPartCollection ? keyParts : mSecretSharing.getUserKeyParts();

        if (mForceKeyPartCollection) {
            int minimumParts = mKeyParts[0].getMinimumKeyParts(mActivity);
            mContainer.setMinimumRecoverParts(minimumParts);
        }

        if (mContainer.getMinimumRecoverParts() <= mKeyParts.length) {
            mView.stepCompleted();
            mView.nextStep();
        } else {
            mMissingKeyParts = mContainer.getMinimumRecoverParts() - mKeyParts.length;
        }
    }

    @Override
    public void showQrCode() {
        if (mEncryptedBackup == null && mBackup.isAvailableInCloudStorage()) {
            mEncryptedBackup = mSecretSharing.getFromCloud(mBackup);
        }

        String secret = mContainer.restoreBackup(mKeyParts, mEncryptedBackup);

        if (secret != null) {
            Utils.startActivity(mActivity, FullscreenFragmentActivity.class, false, FullscreenFragmentActivity.getQrCodeArguments(secret), -1);
            mView.dismiss();
        } else {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mView.showSnackbar(mActivity.getString(R.string.dialog_restore_wrong_secret));
                }
            });
        }
    }

    @Override
    public void showText() {
        if (mEncryptedBackup == null && mBackup.isAvailableInCloudStorage()) {
            mEncryptedBackup = mSecretSharing.getFromCloud(mBackup);
        }

        String secret = mContainer.restoreBackup(mKeyParts, mEncryptedBackup);

        if (secret != null) {
            Utils.startActivity(mActivity, FullscreenFragmentActivity.class, false, FullscreenFragmentActivity.getShowTextArguments(secret), -1);
            mView.dismiss();
        } else {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mView.showSnackbar(mActivity.getString(R.string.dialog_restore_wrong_secret));
                }
            });
        }
    }

    @Override
    public void onDetected(String data) {
        mEncryptedBackup = data;

        mQrReader.stop(false);

        mView.stepCompleted();
        mView.nextStep();
    }

    @Override
    public void setSecret(String secret) {}
}
