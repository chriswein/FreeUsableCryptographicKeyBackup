package com.keybackup.main.backup;

import com.android.secret.sharing.AndroidSecretSharing;
import com.android.secret.sharing.Backup;
import com.android.secret.sharing.SecretPresentation;
import com.keybackup.BaseActivity;
import com.keybackup.Utils;
import com.keybackup.stepperfragments.createbackup.CreateBackupDialog;
import com.keybackup.stepperfragments.restorebackup.RestoreDialog;

public class BackupPresenter implements BackupContract.UserActionsListener, CreateBackupDialog.BackupCreatorListener {
    private BackupContract.View mView;
    private AndroidSecretSharing mSecretSharing;

    public BackupPresenter(BackupContract.View view, AndroidSecretSharing secretSharing) {
        mView = view;
        mSecretSharing = secretSharing;
    }

    @Override
    public void onItemClick(BaseActivity activity, int position, SecretPresentation backup) {
        Utils.openFullscreenDialog(activity, RestoreDialog.newInstance((Backup) backup), RestoreDialog.TAG);
    }

    @Override
    public void loadData() {
        Object[] backups = mSecretSharing.getSavedBackups();

        if (backups == null || backups.length == 0) {
            mView.showEmptyScreen();
        } else {
            mView.showData((SecretPresentation[]) backups);
        }
    }

    @Override
    public void backupCreated(SecretPresentation backup) {
        mView.addBackup(backup);
    }
}
