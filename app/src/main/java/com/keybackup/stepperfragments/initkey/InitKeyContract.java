package com.keybackup.stepperfragments.initkey;

import android.os.Bundle;

import com.android.secret.sharing.AndroidSecretSharing;
import com.android.secret.sharing.KeyPart;

interface InitKeyContract {

    interface View {

        void stepCompleted();

        /**
         * @param errorMessage Can be null
         */
        void stepNotCompleted(String errorMessage);

        void showMessage();

    }

    interface UserActionsListener {

        void createContainer(AndroidSecretSharing secretSharing);

        void setMinimumParts(int parts);

        void setTotalParts(int parts);

        void showContactList();

        KeyPart[] saveKeyParts(boolean chooseContactsNow);

        void onStepOpening(int stepNumber);

        void saveInstanceState(Bundle outState);

        void restore(Bundle savedInstanceState, Bundle arguments);

    }

}
