package com.keybackup.stepperfragments.restorebackup;

import com.android.secret.sharing.AndroidSecretSharing;
import com.android.secret.sharing.KeyPart;

interface RestoreContract {

    interface View {

        void stepCompleted();

        void stepNotCompleted();

        void nextStep();

        void dismiss();

        void showSnackbar(String message);

    }

    interface UserActionListener {

        android.view.View createStepContentView(int stepNumber);

        void onStepOpening(int stepNumber);

        void showQrCode();

        void showText();

        void setKeyParts(KeyPart[] keyParts);

        void showCamera(AndroidSecretSharing secretSharing);

    }

}
