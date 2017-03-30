package com.keybackup.intentbackup;


import android.os.Bundle;

class IntentBackupContract {

    interface View {

    }

    interface UserActionListener {

        void saveState(Bundle outState);

        void saveToCloud(String backupName);

    }

}
