package com.keybackup.stepperfragments.createbackup;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.android.secret.sharing.AndroidSecretSharing;
import com.android.secret.sharing.Backup;
import com.android.secret.sharing.QrCodeSizeException;
import com.android.secret.sharing.QrReader;
import com.keybackup.BaseActivity;
import com.keybackup.Logger;
import com.keybackup.R;
import com.keybackup.stepperfragments.StepScanSecret;

import github.nisrulz.qreader.QRDataListener;

public class CreateBackupPresenter implements CreateBackupContract.UserActionListener, QRDataListener, StepScanSecret.QrStepListener {
    private static final String ARG_BACKUP = "backup";

    private CreateBackupContract.View mView;
    private AndroidSecretSharing mSecretSharing;
    private BaseActivity mActivity;
    private Backup mBackup;
    private QrReader mQrReader;
    private String mSecretInput;    // secret that will be backed up
    private boolean mIntentSecret;  // true if a secret was sent by another app via intent
    private SurfaceView mSurfaceView;

    public CreateBackupPresenter(CreateBackupContract.View view, BaseActivity activity, Bundle savedInstanceState, Bundle arguments) {
        mView = view;
        mActivity = activity;
        mSecretSharing = mActivity.getSecretSharing();

        if (savedInstanceState != null) {
            mBackup = savedInstanceState.getParcelable(ARG_BACKUP);
        } else {
            // TODO: If user did not initialized the container, the app will crash here when trying to backup a secret via intent
            mBackup = mSecretSharing.getContainer().createBackup();
        }

        if (arguments.containsKey(CreateBackupDialog.ARG_SECRET)) {
            mIntentSecret = true;
            mSecretInput = arguments.getString(CreateBackupDialog.ARG_SECRET);
            //TODO: Snackbar: Backup imported
        }
    }

    @Override
    public void showCamera() {
        mQrReader = mSecretSharing.readQrCode(mActivity, mSurfaceView, this);
        mQrReader.showQRReader();
    }

    @Override
    public void setBackupName(String name) {
        if (TextUtils.isEmpty(name)) {
            mView.stepNotCompleted();
        } else {
            mBackup.setName(name);
            mView.stepCompleted();
        }
    }

    @Override
    public void setSecret(String secret) {
        if (TextUtils.isEmpty(secret)) {
            mView.stepNotCompleted();
        } else {
            mSecretInput = secret;
            mView.stepCompleted();
        }
    }

    @Override
    public void initQrReader(SurfaceView view) {
        mSurfaceView = view;
    }

    @Override
    public boolean enableTextInput() {
        return true;
    }

    @Override
    public void saveState(Bundle bundle) {
        bundle.putParcelable(ARG_BACKUP, mBackup);
    }

    @Override
    public void close() {
        if (mQrReader != null) {
            mQrReader.stop(true);
        }
    }

    @Override
    public void onStepOpening(int stepNumber, boolean addedUserNameStep) {
        if (stepNumber != 0 && !TextUtils.isEmpty(mBackup.getName())) {
            mView.setNameSubtitle(mBackup.getName());
        }

        switch (stepNumber) {
            case 0:
                if (mBackup == null || TextUtils.isEmpty(mBackup.getName())) {
                    mView.stepNotCompleted();
                }
                mView.setNameSubtitle(null);
                break;
            case 1:
                if (mIntentSecret) {
                    mView.stepCompleted();
                    mView.nextStep();
                }

                if (mQrReader != null) {
                    mQrReader.restart();
                }

                if (TextUtils.isEmpty(mSecretInput)) {
                    mView.stepNotCompleted();
                }
                break;
        }
    }

    @Override
    public boolean printBackup(CreateBackupDialog.BackupCreatorListener listener) {
        save(listener, Backup.BackupStoreMethod.PRINT);

        try {
            mSecretSharing.printQrCode(mActivity, mBackup);
        } catch (QrCodeSizeException e) {
            e.printStackTrace();
            mView.showMessage(mActivity.getString(R.string.create_backup_qr_code_error));

            return false;
        }

        Logger.backupCreated(mBackup.getName(), false);

        return true;
    }

    @Override
    public boolean saveToCloud(CreateBackupDialog.BackupCreatorListener listener) {
        save(listener, Backup.BackupStoreMethod.CLOUD);
        mActivity.showSnackbar(mActivity.getString(R.string.snackbar_message_cloud));
        mSecretSharing.saveToCloud(mBackup);

        Logger.backupCreated(mBackup.getName(), true);

        return true;
    }

    @Override
    public boolean sendEmail(CreateBackupDialog.BackupCreatorListener listener) {
        save(listener, Backup.BackupStoreMethod.EMAIL);

        boolean sendEmail = false;

        try {
            sendEmail = mSecretSharing.sendEmail(mActivity, mBackup);
        } catch (QrCodeSizeException e) {
            e.printStackTrace();
        }

        if (sendEmail) {
            Logger.backupCreated(mBackup.getName(), true);
        }

        return sendEmail;
    }

    private void save(CreateBackupDialog.BackupCreatorListener listener, Backup.BackupStoreMethod storeMethod) {
        mBackup.setStoreMethod(storeMethod);
        mBackup.encrypt(mSecretInput);

        // backup will be saved in mSecretSharing.saveToCloud() method
        if (storeMethod != Backup.BackupStoreMethod.CLOUD) {
            mBackup.save(mActivity);
        }

        if (listener != null) {
            listener.backupCreated(mBackup);
        }

        close();
    }

    @Override
    public void onDetected(String data) {
        mQrReader.stop(false);

        mSecretInput = data;

        mActivity.showSnackbar(mActivity.getString(R.string.snackbar_message_qrscan));

        mView.stepCompleted();
        mView.nextStep();
    }
}
