package com.keybackup.main.backup;

import com.android.secret.sharing.SecretPresentation;
import com.keybackup.BaseActivity;

public interface BackupContract {

    interface View {

        void showData(SecretPresentation[] backups);

        void showSnackbar(String message);

        void addBackup(SecretPresentation backup);

        void showEmptyScreen();

    }

    interface UserActionsListener {

        void onItemClick(BaseActivity activity, int position, SecretPresentation backup);

        void loadData();

    }
}
