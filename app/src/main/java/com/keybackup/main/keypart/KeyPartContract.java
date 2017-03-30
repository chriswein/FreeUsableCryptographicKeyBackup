package com.keybackup.main.keypart;

import com.android.secret.sharing.SecretPresentation;
import com.keybackup.BaseActivity;

public interface KeyPartContract {

    interface View {

        void showKeyParts(SecretPresentation[] keyParts);

        void showSnackbar(String message);

        void addKeyPart(SecretPresentation keyPart);

        void showEmptyScreen();

    }

    interface UserActionsListener {

        void onItemClick(BaseActivity activity, int position, SecretPresentation keyPart);

        void loadData();

    }
}
