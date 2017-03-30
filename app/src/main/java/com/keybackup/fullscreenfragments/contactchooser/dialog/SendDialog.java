package com.keybackup.fullscreenfragments.contactchooser.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.android.secret.sharing.Contact;
import com.keybackup.R;

public class SendDialog extends DialogFragment implements View.OnClickListener {
    public static final String TAG = "dialog fragment";

    private static final String ARG_CONTACT = "contact";

    private SendDialogListener mSendListener;
    private SendChangeDialogListener mChangeListener;
    private Contact mContact;
    private ITEMS[] mItems;

    private ListView mListView;
    private SendDialogAdapter mAdapter;

    enum ITEMS {
        SHOW_QR,
        SHOW_PRINT,
        SHOW_EMAIL,
        CHANGE_TO_QR,
        CHANGE_TO_PRINT,
        CHANGE_TO_EMAIL,
        DESELECT;

        public String toString(Context context) {
            switch (this) {
                case SHOW_QR:
                    return context.getString(R.string.dialog_change_send_method_show_qr);
                case SHOW_PRINT:
                    return context.getString(R.string.dialog_change_send_method_show_print);
                case SHOW_EMAIL:
                    return context.getString(R.string.dialog_change_send_method_show_email);
                case CHANGE_TO_QR:
                    return context.getString(R.string.dialog_change_send_method_change_qr);
                case CHANGE_TO_PRINT:
                    return context.getString(R.string.dialog_change_send_method_change_print);
                case CHANGE_TO_EMAIL:
                    return context.getString(R.string.dialog_change_send_method_change_email);
                case DESELECT:
                   return context.getString(R.string.dialog_change_send_method_deselect);
                default:
                    throw new IllegalStateException("No string defined for " + this);
            }
        }
    }

    public static SendDialog newInstance(Contact contact) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_CONTACT, contact);

        SendDialog dialog = new SendDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getContext();
        Bundle args = savedInstanceState != null ? savedInstanceState : getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        mContact = args.getParcelable(ARG_CONTACT);

        if (mContact == null) {
            dismiss();
            return builder.create();
        }

        String title;

        boolean deviceCanPrint = Build.VERSION.SDK_INT >= 19;

        if (mContact.getSendMethod() == Contact.SendMethod.NONE) {
            title = context.getString(R.string.dialog_send_method_title);
            if (deviceCanPrint) {
                mItems = new ITEMS[]{ITEMS.SHOW_QR, ITEMS.SHOW_PRINT, ITEMS.SHOW_EMAIL};
            } else {
                mItems = new ITEMS[]{ITEMS.SHOW_QR, ITEMS.SHOW_EMAIL};
            }
        } else {
            title = String.format(getString(R.string.dialog_change_send_method_title), mContact.getSendMethod().toString(context));

            mItems = new ITEMS[deviceCanPrint ? 4 : 3];

            switch (mContact.getSendMethod()) {
                case QR:
                    mItems[0] = ITEMS.SHOW_QR;
                    if (deviceCanPrint) {
                        mItems[1] = ITEMS.CHANGE_TO_PRINT;
                    }
                    mItems[deviceCanPrint ? 2 : 1] = ITEMS.CHANGE_TO_EMAIL;
                    break;
                case PRINT:
                    mItems[0] = ITEMS.SHOW_PRINT;
                    mItems[1] = ITEMS.CHANGE_TO_QR;
                    mItems[2] = ITEMS.CHANGE_TO_EMAIL;
                    break;
                case EMAIL:
                    mItems[0] = ITEMS.SHOW_EMAIL;
                    mItems[1] = ITEMS.CHANGE_TO_QR;
                    if (deviceCanPrint) {
                        mItems[2] = ITEMS.CHANGE_TO_PRINT;
                    }
                    break;
            }
            mItems[deviceCanPrint ? 3 : 2] = ITEMS.DESELECT;
        }

        int listPaddingTop = context.getResources().getDimensionPixelSize(R.dimen.dialog_title_padding_vertical);

        mListView = new ListView(context);
        mListView.setDivider(null);
        mListView.setPadding(0, listPaddingTop, 0, 0);

        mAdapter = new SendDialogAdapter(context, mItems);

        String email = mContact.getEmail();
        if (TextUtils.isEmpty(email)) {
            email = mContact.loadEmail(context);
        }
        mAdapter.setEmail(email);

        mListView.setAdapter(mAdapter);

        builder.setTitle(title)
                .setView(mListView)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing to do here. See dialog.setOnShowListener()
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null);

        final AlertDialog dialog = builder.create();

        // overrides OK Button behaviour to be able to prevent auto closing dialog
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(SendDialog.this);
            }
        });

        // clear flags of alert dialog class so that keyboard will be shown when entering email address
        mListView.post(new Runnable() {
            @Override
            public void run() {
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            }
        });

        return dialog;
    }

    public void setListener(SendDialogListener listener) {
        mSendListener = listener;
    }

    public void setListener(SendChangeDialogListener listener) {
        mChangeListener = listener;
    }

    @Override
    public void onClick(View view) {
        int selectedItem = mAdapter.getSelectedItem();
        ITEMS item = mItems[selectedItem];
        String email = mAdapter.getEmail();

        if (mContact.getSendMethod() == Contact.SendMethod.NONE && mSendListener != null) {
            switch (item) {
                case SHOW_QR:
                    mSendListener.chosenSendMethod(Contact.SendMethod.QR);
                    break;
                case SHOW_PRINT:
                    mSendListener.chosenSendMethod(Contact.SendMethod.PRINT);
                    break;
                case SHOW_EMAIL:
                    if (TextUtils.isEmpty(email)) {
                        mAdapter.focusEmailEdit(mListView, selectedItem);
                        return;
                    }
                    mSendListener.setEmailAddress(email);
                    mSendListener.chosenSendMethod(Contact.SendMethod.EMAIL);
                    break;
            }
        } else if (mContact.getSendMethod() != Contact.SendMethod.NONE && mChangeListener != null) {
            switch (item) {
                case SHOW_QR:
                case SHOW_PRINT:
                    mChangeListener.showSelectedMethod();
                    break;
                case SHOW_EMAIL:
                    if (TextUtils.isEmpty(email)) {
                        mAdapter.focusEmailEdit(mListView, selectedItem);
                        return;
                    }

                    mChangeListener.setEmailAddress(email);
                    mChangeListener.showSelectedMethod();
                    break;
                case CHANGE_TO_QR:
                    mChangeListener.changeTo(Contact.SendMethod.QR);
                    break;
                case CHANGE_TO_PRINT:
                    mChangeListener.changeTo(Contact.SendMethod.PRINT);
                    break;
                case CHANGE_TO_EMAIL:
                    if (TextUtils.isEmpty(email)) {
                        mAdapter.focusEmailEdit(mListView, selectedItem);
                        return;
                    }

                    mChangeListener.setEmailAddress(email);
                    mChangeListener.changeTo(Contact.SendMethod.EMAIL);
                    break;
                case DESELECT:
                    mChangeListener.deselect();
                    break;
            }
        }

        dismiss();
    }

    public interface SendDialogListener {
        void setEmailAddress(String emailAddress);
        void chosenSendMethod(Contact.SendMethod sendMethod);
    }

    public interface SendChangeDialogListener {
        void setEmailAddress(String emailAddress);
        void showSelectedMethod();
        void changeTo(Contact.SendMethod sendMethod);
        void deselect();
    }
}
