package com.keybackup.fullscreenfragments.qrcodereader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;

import com.android.secret.sharing.AndroidSecretSharing;
import com.android.secret.sharing.KeyPart;
import com.android.secret.sharing.QrReader;
import com.android.secret.sharing.QrKeyPartListener;
import com.keybackup.BaseActivity;
import com.keybackup.R;
import com.android.secret.sharing.SecretPresentation;

public class QrReaderPresenter implements QrReaderContract.UserActionListener, QrKeyPartListener {
    private QrReaderContract.View mView;
    private BaseActivity mActivity;
    private AndroidSecretSharing mSecretSharing;
    private QrReader mQrReader;
    private boolean mReadForeignKey;
    private boolean mIgnoreOrigin;

    public QrReaderPresenter(QrReaderContract.View view, BaseActivity activity, boolean foreignKey, boolean ignoreOrigin) {
        mView = view;
        mActivity = activity;
        mSecretSharing = activity.getSecretSharing();
        mReadForeignKey = foreignKey;
        mIgnoreOrigin = ignoreOrigin;
    }

    @Override
    public void showCamera(SurfaceView surfaceView) {
        mQrReader = mSecretSharing.readSecretPart(mActivity, surfaceView, this, mReadForeignKey);
        mQrReader.setIgnoreKeyPartOrigin(mIgnoreOrigin);
        mQrReader.showQRReader();
    }

    @Override
    public void qrCodeDetected(SecretPresentation secret) {
        mQrReader.stop(true);

        Intent data = new Intent();
        data.putExtra(QrReaderFragment.RESULT_KEY_PART, (KeyPart) secret);

        mActivity.setResult(Activity.RESULT_OK, data);
        mActivity.finish();
    }

    @Override
    public void wrongSecretPart() {
        mView.showMessage(mActivity.getString(R.string.secret_part_transfer_error));
    }

    @Override
    public void saveState(Bundle bundle) {
        bundle.putBoolean(QrReaderFragment.ARG_READ_FOREIGN_KEY, mReadForeignKey);
    }
}
