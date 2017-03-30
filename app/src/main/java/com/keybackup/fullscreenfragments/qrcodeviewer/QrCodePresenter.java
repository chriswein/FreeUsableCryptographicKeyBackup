package com.keybackup.fullscreenfragments.qrcodeviewer;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.secret.sharing.Contact;
import com.android.secret.sharing.AndroidSecretSharing;
import com.android.secret.sharing.KeyPart;
import com.android.secret.sharing.QrCodeSizeException;
import com.keybackup.BaseActivity;
import com.keybackup.R;
import com.keybackup.fullscreenfragments.contactchooser.ContactChooserFragment;

public class QrCodePresenter implements QrCodeContract.UserActionListener {
    private QrCodeContract.View mView;
    private BaseActivity mActivity;
    private AndroidSecretSharing mSecretSharing;
    private KeyPart mKeyPart;
    private String mQrText;
    private boolean mShowText;
    private Contact mContact;

    public QrCodePresenter(QrCodeContract.View view, BaseActivity activity, KeyPart keyPart, Contact contact) {
        mKeyPart = keyPart;
        mContact = contact;

        init(view, activity);
    }

    public QrCodePresenter(QrCodeContract.View view, BaseActivity activity, String qrText, boolean showText) {
        mQrText = qrText;
        mShowText = showText;
        init(view, activity);
    }

    private void init(QrCodeContract.View view, BaseActivity activity) {
        mView = view;
        mActivity = activity;
        mSecretSharing = activity.getSecretSharing();
    }

    @Override
    public void init(ImageView view, TextView qrText) {
        try {
            if (mKeyPart != null) {
                mSecretSharing.showQrCode(mKeyPart, view);
            } else if (!mShowText) {
                mSecretSharing.showQrCode(mQrText, view);
            } else {
                view.setVisibility(View.GONE);
                qrText.setVisibility(View.VISIBLE);
                qrText.setText(mQrText);
            }
        } catch (QrCodeSizeException e) {
            mView.setDescription(mActivity.getString(R.string.qr_fragment_description_restored_error));
            mView.hideTransferLaterButton();
            mView.hideTransferButton();
            return;
        }

        if (mQrText != null) {
            if (mShowText) {
                mView.setDescription(mActivity.getString(R.string.qr_fragment_description_restored_text));
            } else {
                mView.setDescription(mActivity.getString(R.string.qr_fragment_description_restored));
            }
        } else {
            String name = mContact != null ? mContact.getName() : mKeyPart.getOwner();
            String description = String.format(mActivity.getString(R.string.qr_fragment_description), name);

            mView.setDescription(description);
        }

        if (mContact == null || mContact.getSendStatus() == Contact.SendStatus.SELECTED) {
            mView.hideTransferLaterButton();
        }

        if (mContact == null) {
            mView.hideTransferButton();
        }
    }

    @Override
    public void secretTransferred() {
        boolean prevStatusSelected = mContact.getSendStatus() == Contact.SendStatus.SELECTED;

        mContact.setSendMethod(Contact.SendMethod.QR);
        mContact.setSendStatus(Contact.SendStatus.CONFIRMED);
        mContact.removeKeyPart(mKeyPart);
        mContact.save(mActivity);

        mKeyPart.delete(mActivity);

        finish(prevStatusSelected);
    }

    @Override
    public void transferLater() {
        boolean prevStatusSelected = mContact.getSendStatus() == Contact.SendStatus.SELECTED;

        mContact.setSendMethod(Contact.SendMethod.QR);
        mContact.setSendStatus(Contact.SendStatus.SELECTED);
        mContact.setKeyPart(mKeyPart);
        mContact.save(mActivity);

        finish(prevStatusSelected);
    }

    private void finish(boolean previousSendStatusSelected) {
        Intent intent = new Intent();
        intent.putExtra(ContactChooserFragment.ARG_CONTACT, mContact);
        intent.putExtra(ContactChooserFragment.ARG_PREV_SEND_STATUS_SELECTED, previousSendStatusSelected);

        mActivity.setResult(Activity.RESULT_OK, intent);
        mActivity.finish();
    }
}
