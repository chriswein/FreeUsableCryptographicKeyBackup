package com.keybackup.intentbackup;


import android.os.Bundle;
import android.text.TextUtils;

import com.android.secret.sharing.AndroidSecretSharing;
import com.android.secret.sharing.Backup;
import com.keybackup.BaseActivity;
import com.keybackup.Logger;
import com.keybackup.Preferences;
import com.keybackup.Utils;
import com.keybackup.onboarding.OnboardingActivity;

public class IntentBackupPresenter implements IntentBackupContract.UserActionListener {
    private static final String ARG_BACKUP = "backup";

    private BaseActivity mActivity;
    private AndroidSecretSharing mSecretSharing;
    private Backup mBackup;
    private String mSecret;

    public IntentBackupPresenter(BaseActivity activity, AndroidSecretSharing secretSharing, String secret, Bundle savedInstanceState) {
        mActivity = activity;
        mSecret = secret;
        mSecretSharing = secretSharing;

        Logger.init(activity);

        Preferences preferences = Preferences.getPreferences(activity);
        if (!preferences.isOnboardingFinished()) {
            Logger.startOnboarding(-1);
            Utils.startActivity(mActivity, OnboardingActivity.class, true);
            return;
        }

        if (savedInstanceState != null) {
            mBackup = savedInstanceState.getParcelable(ARG_BACKUP);
        } else {
            mBackup = mSecretSharing.getContainer().createBackup();
        }
    }

    @Override
    public void saveState(Bundle outState) {
        outState.putParcelable(ARG_BACKUP, mBackup);
    }

    @Override
    public void saveToCloud(String backupName) {
        if (TextUtils.isEmpty(backupName)) {
            return;
        }

        mBackup.setName(backupName);
        mBackup.setStoreMethod(Backup.BackupStoreMethod.CLOUD);
        mBackup.encrypt(mSecret);
        mSecretSharing.saveToCloud(mBackup);

        mActivity.finish();
    }
}
