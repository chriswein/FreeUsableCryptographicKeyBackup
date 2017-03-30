package com.keybackup.fullscreenfragments.contactchooser;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.android.secret.sharing.Contact;
import com.android.secret.sharing.ContactLoader;
import com.android.secret.sharing.AndroidSecretSharing;
import com.android.secret.sharing.KeyPart;
import com.android.secret.sharing.QrCodeSizeException;
import com.keybackup.BaseActivity;
import com.keybackup.R;
import com.keybackup.Utils;
import com.keybackup.fullscreenfragments.FullscreenFragmentActivity;
import com.keybackup.fullscreenfragments.contactchooser.contactadapter.ContactsAdapter;
import com.keybackup.fullscreenfragments.contactchooser.dialog.SendDialog;

import java.util.ArrayList;
import java.util.Arrays;

public class ContactChooserPresenter implements ContactChooserContract.UserActionListener, FullscreenFragmentActivity.PermissionListener, ContactLoader.ContactLoaderListener {
    private static final String ARG_KEYS = "keys";
    private static final String ARG_SEND_KEYS = "send_keys";
    private static final String ARG_MISSING_KEY_PARTS = "missing_key_parts";

    private ContactChooserContract.View mView;
    private FullscreenFragmentActivity mActivity;
    private AndroidSecretSharing mSecretSharing;
    private ContactLoader mContactsLoader;

    private ContactsAdapter mAdapter;
    private ArrayList<KeyPart> mKeyParts;
    private boolean mSendKeyParts;
    private int mMissingKeyParts;
    private boolean mReceiveKeyPartsFromOtherDevice;

    private Contact mContact;
    private ArrayList<KeyPart> mReceivedKeyParts = new ArrayList<>();

    public ContactChooserPresenter(ContactChooserContract.View view, BaseActivity activity, KeyPart[] keyParts) {
        this(view, activity);

        mKeyParts = new ArrayList<>();
        mKeyParts.addAll(Arrays.asList(keyParts));

        mSendKeyParts = true;

        updateDescription();
    }

    public ContactChooserPresenter(ContactChooserContract.View view, BaseActivity activity, int missingKeyParts) {
        this(view, activity);
        mMissingKeyParts = missingKeyParts;

        if (mMissingKeyParts == 0) {
            mReceiveKeyPartsFromOtherDevice = true;
        }

        mSendKeyParts = false;

        updateDescription();
    }

    public ContactChooserPresenter(ContactChooserContract.View view, BaseActivity activity) {
        mView = view;
        mActivity = (FullscreenFragmentActivity) activity;
        mSecretSharing = mActivity.getSecretSharing();

        ((FullscreenFragmentActivity) activity).setListener(this);
    }

    @Override
    public void setAdapter(ListView listView) {
        mAdapter = new ContactsAdapter(mActivity, this);
        listView.setAdapter(mAdapter);
    }

    @Override
    public void requestContactsPermission() {
        int contactsPermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS);
        if (contactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[] {Manifest.permission.READ_CONTACTS}, FullscreenFragmentActivity.REQUEST_CODE_PERMISSION_CONTACTS);
            return;
        }

        contactPermissionGranted();
    }

    @Override
    public void itemClicked (Fragment fragment, int position, final int requestCode) {
        Contact contact = mAdapter.getItem(position);
        contact.setListPosition(position);

        if (mSendKeyParts) {
            clickSend(contact, requestCode);
        } else {
            clickReceive(fragment, contact, requestCode);
        }
    }

    private void clickSend(final Contact contact, final int requestCode) {
        switch (contact.getSendStatus()) {
            case NONE:
                SendDialog dialog = SendDialog.newInstance(contact);

                dialog.setListener(new SendDialog.SendDialogListener() {
                    @Override
                    public void setEmailAddress(String emailAddress) {
                        contact.setEmail(emailAddress);
                    }

                    @Override
                    public void chosenSendMethod(Contact.SendMethod sendMethod) {
                        sendSecret(sendMethod, contact, requestCode);
                    }
                });

                dialog.show(mActivity.getSupportFragmentManager(), SendDialog.TAG);
                break;
            case SELECTED:
                SendDialog changeDialog = SendDialog.newInstance(contact);

                changeDialog.setListener(new SendDialog.SendChangeDialogListener() {
                    @Override
                    public void setEmailAddress(String emailAddress) {
                        contact.setEmail(emailAddress);
                    }

                    @Override
                    public void showSelectedMethod() {
                        sendSecret(contact.getSendMethod(), contact, requestCode);
                    }

                    @Override
                    public void changeTo(Contact.SendMethod sendMethod) {
                        contact.setSendMethod(sendMethod);
                        contact.save(mActivity);

                        sendSecret(sendMethod, contact, requestCode);
                    }

                    @Override
                    public void deselect() {
                        ArrayList<KeyPart> partsLeft = new ArrayList<>();

                        if (mKeyParts.size() > 0) {
                            partsLeft.addAll(mKeyParts);
                        }

                        partsLeft.add(contact.getKeyPart(mActivity));
                        contact.clearSendSelection(mActivity);

                        mAdapter.setContact(contact);

                        mKeyParts = partsLeft;
                        mActivity.setKeyParts(mKeyParts.toArray(new KeyPart[mKeyParts.size()]));

                        if (mKeyParts.size() == 1) {
                            loadContacts();
                        }

                        updateDescription();
                    }
                });

                changeDialog.show(mActivity.getSupportFragmentManager(), SendDialog.TAG);

                break;
            case SENT:
                break;
            case CONFIRMED:
                break;
        }
    }

    private void clickReceive(Fragment fragment, Contact contact, int requestCode) {
        if (contact.getSendStatus() == Contact.SendStatus.RECEIVED) {
            return;
        }

        mContact = contact;

        Utils.startActivity(fragment, FullscreenFragmentActivity.class, FullscreenFragmentActivity.getQrReaderArguments(false, mReceiveKeyPartsFromOtherDevice), ContactChooserFragment.REQUEST_KEY_RECEIVE);
    }

    private void sendSecret(Contact.SendMethod sendMethod, Contact contact, int requestCode) {
        boolean previousStatusSelected = contact.getSendStatus() != Contact.SendStatus.NONE;
        KeyPart keyPart;

        if (!previousStatusSelected) {
            contact.setKeyPart(mKeyParts.get(0));
            keyPart = mKeyParts.get(0);
        } else {
            keyPart = contact.getKeyPart(mActivity);
        }

        switch (sendMethod) {
            case QR:
                Utils.startActivity(mView.getFragment(), FullscreenFragmentActivity.class, FullscreenFragmentActivity.getQrCodeArguments(keyPart, contact), requestCode);
                break;
            case PRINT:
                contact.setSendMethod(Contact.SendMethod.PRINT);
                contact.setSendStatus(Contact.SendStatus.SELECTED);

                if (!previousStatusSelected) {
                    contact.setKeyPart(keyPart);
                }

                contact.save(mActivity);

                try {
                    mSecretSharing.printQrCode(mActivity, contact);
                } catch (QrCodeSizeException e) {
                    // Secret parts should never be too large
                    e.printStackTrace();
                }

                secretSent(contact, previousStatusSelected);
                break;
            case EMAIL:
                contact.setSendMethod(Contact.SendMethod.EMAIL);
                contact.setSendStatus(Contact.SendStatus.SELECTED);

                if (!previousStatusSelected) {
                    contact.setKeyPart(keyPart);
                }

                contact.save(mActivity);

                try {
                    mSecretSharing.sendEmail(mActivity, contact);
                } catch (QrCodeSizeException e) {
                    e.printStackTrace();
                }

                secretSent(contact, previousStatusSelected);
                break;
        }
    }

    @Override
    public void secretSent(Contact contact, boolean previousSendStatusSelected) {
        if (previousSendStatusSelected) {
            mAdapter.setContact(contact);
            return;
        }

        ArrayList<KeyPart> partsLeft = new ArrayList<>();

        if (mKeyParts.size() > 0) {
            partsLeft.addAll(mKeyParts);
            partsLeft.remove(0);
        }

        mKeyParts = partsLeft;
        mActivity.setKeyParts(mKeyParts.toArray(new KeyPart[mKeyParts.size()]));

        updateDescription();

        mAdapter.setContact(contact);

        if (mKeyParts.size() == 0) {
            mAdapter.showSendMethodHeader();
        }
    }

    @Override
    public void closeKeyboard() {
        View view = mActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void saveState(Bundle bundle) {
        mAdapter.saveState(bundle);

        bundle.putParcelableArrayList(ARG_KEYS, mKeyParts);
        bundle.putBoolean(ARG_SEND_KEYS, mSendKeyParts);
        bundle.putInt(ARG_MISSING_KEY_PARTS, mMissingKeyParts);
    }

    @Override
    public void restoreState(Bundle bundle) {
        mAdapter.restoreState(bundle);

        mKeyParts = bundle.getParcelableArrayList(ARG_KEYS);
        mSendKeyParts = bundle.getBoolean(ARG_SEND_KEYS);
        mMissingKeyParts = bundle.getInt(ARG_MISSING_KEY_PARTS);

        updateDescription();
    }

    @Override
    public void receivedKeyPart(KeyPart keyPart) {
        if (mMissingKeyParts == 0) {
            // if backup is restored on another device the minimum key parts must be loaded from a key part
            mMissingKeyParts = keyPart.getMinimumKeyParts(mActivity);
        }

        mReceivedKeyParts.add(keyPart);
        mMissingKeyParts--;

        if (mMissingKeyParts <= 0) {
            Intent intent = new Intent();
            intent.putExtra(FullscreenFragmentActivity.RESULT_RECEIVED_KEY_PARTS, mReceivedKeyParts);
            mActivity.setResult(Activity.RESULT_OK, intent);
            mActivity.finish();
        }

        if (mContact != null) {
            mContact.setSendStatus(Contact.SendStatus.RECEIVED);
            mAdapter.setContact(mContact.getListPosition(), mContact);
        }

        updateDescription();
    }

    @Override
    public void contactPermissionGranted() {
        loadContacts();
    }

    @Override
    public void contactsLoaded(ArrayList<Contact> contacts) {
        mAdapter.setContacts(contacts, mSendKeyParts);
    }

    private void updateDescription() {
        String description = null;

        if (mSendKeyParts) {
            if (mKeyParts.size() > 0) {
                description = mActivity.getResources().getQuantityString(R.plurals.contact_chooser_share_parts_description, mKeyParts.size(), mKeyParts.size());
            } else {
                description = mActivity.getString(R.string.contact_chooser_share_parts_description_full);
            }
        } else if (mMissingKeyParts > 0) {
            description = mActivity.getResources().getQuantityString(R.plurals.contact_chooser_receive_parts_description, mMissingKeyParts, mMissingKeyParts);
        }

        mView.setDescription(description);
    }

    private void loadContacts() {
        if (mContactsLoader == null) {
            mContactsLoader = new ContactLoader(mActivity, this);
        }

        // load contacts if key parts should be sent or if backup is restored from another device
        if ((mSendKeyParts && mKeyParts.size() > 0) || mReceiveKeyPartsFromOtherDevice) {
            mActivity.getSupportLoaderManager().initLoader(0, null, mContactsLoader);
        } else {
            mContactsLoader.loadContacts();
            mAdapter.showSendMethodHeader();
        }
    }
}
