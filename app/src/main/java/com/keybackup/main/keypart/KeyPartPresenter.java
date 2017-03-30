package com.keybackup.main.keypart;

import android.content.Context;

import com.android.secret.sharing.AndroidSecretSharing;
import com.android.secret.sharing.KeyPart;
import com.android.secret.sharing.QrKeyPartListener;
import com.android.secret.sharing.SecretPresentation;
import com.keybackup.BaseActivity;
import com.keybackup.R;
import com.keybackup.Utils;
import com.keybackup.fullscreenfragments.FullscreenFragmentActivity;

public class KeyPartPresenter implements KeyPartContract.UserActionsListener, QrKeyPartListener {
    private KeyPartContract.View mView;
    private AndroidSecretSharing mSecretSharing;
    private Context mContext;

    public KeyPartPresenter(KeyPartContract.View view, Context context, AndroidSecretSharing secretSharing) {
        mView = view;
        mContext = context;
        mSecretSharing = secretSharing;
    }

    @Override
    public void onItemClick(BaseActivity activity, int position, SecretPresentation keyPart) {
        Utils.startActivity(activity, FullscreenFragmentActivity.class, false, FullscreenFragmentActivity.getQrCodeArguments((KeyPart) keyPart), -1);
    }

    @Override
    public void loadData() {
        Object[] keyParts = mSecretSharing.getForeignKeyParts();

        if (keyParts == null || keyParts.length == 0) {
            mView.showEmptyScreen();
        } else {
            mView.showKeyParts((SecretPresentation[]) keyParts);
        }
    }

    @Override
    public void qrCodeDetected(SecretPresentation secret) {
        // QR Reader dialog scanned new foreign key part
        mView.addKeyPart(secret);

        String message = String.format(mContext.getString(R.string.secret_part_transferred_success), secret.getName());
        mView.showSnackbar(message);
    }

    @Override
    public void wrongSecretPart() {
        // Handled by QR Reader dialog
    }
}
