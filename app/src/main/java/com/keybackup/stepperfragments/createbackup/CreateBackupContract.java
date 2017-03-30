package com.keybackup.stepperfragments.createbackup;

import android.os.Bundle;

class CreateBackupContract {

    interface View {

        void stepCompleted();

        void stepNotCompleted();

        void nextStep();

        void setNameSubtitle(String name);

        void showMessage(String message);

    }

    interface UserActionListener {

        void setBackupName(String name);

        void setSecret(String secret);

        void saveState(Bundle bundle);

        void close();

        boolean sendEmail(CreateBackupDialog.BackupCreatorListener listener);

        boolean saveToCloud(CreateBackupDialog.BackupCreatorListener listener);

        void onStepOpening(int stepNumber, boolean addedUserNameStep);

        boolean printBackup(CreateBackupDialog.BackupCreatorListener listener);

        void showCamera();

    }

}
