package com.keybackup.fullscreenfragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.android.secret.sharing.Contact;
import com.android.secret.sharing.KeyPart;
import com.keybackup.BaseActivity;
import com.keybackup.Logger;
import com.keybackup.R;
import com.keybackup.fullscreenfragments.qrcodeviewer.QrCodeFragment;
import com.keybackup.fullscreenfragments.qrcodereader.QrReaderFragment;
import com.keybackup.fullscreenfragments.contactchooser.ContactChooserFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullscreenFragmentActivity extends BaseActivity {
    public static final int REQUEST_CODE_PERMISSION_CONTACTS = 5678;
    public static final String RESULT_RECEIVED_KEY_PARTS = "result_key_parts";

    private static final String ARG_KEY_PARTS = "key_parts";
    private static final String ARG_MISSING_KEY_PARTS = "missing_key_parts";
    private static final String ARG_KEY_PART = "secret_part";
    private static final String ARG_QR_TEXT = "qr_text";
    private static final String ARG_SHOW_TEXT = "show_text";
    private static final String ARG_CONTACT = "contact";
    private static final String ARG_QR_READER = "qr_reader";
    private static final String ARG_FOREIGN_KEY = "foreign_key";
    private static final String ARG_IGNORE_ORIGIN_KEY = "origin_key";

    @BindView(R.id.activity_fullscreen_fragment_container)
    FrameLayout mFragmentContainer;

    private KeyPart[] mKeyParts;
    private int mMissingKeyParts;

    private KeyPart mKeyPart;
    private String mQrText;
    private String mShowText;
    private Contact mContact;
    private boolean mShowQrReader;
    private boolean mReadForeignKey;
    private boolean mIgnoreOriginKey;
    private PermissionListener mListener;

    public static Bundle getContactListArguments(KeyPart[] keyParts) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(ARG_KEY_PARTS, keyParts);

        return bundle;
    }

    public static Bundle getContactListReceiveArguments(int missingKeyParts) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_MISSING_KEY_PARTS, missingKeyParts);

        return bundle;
    }

    public static Bundle getQrCodeArguments(KeyPart keyPart, Contact contact) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_KEY_PART, keyPart);
        bundle.putParcelable(ARG_CONTACT, contact);

        return bundle;
    }

    public static Bundle getQrCodeArguments(KeyPart secretPart) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_KEY_PART, secretPart);

        return bundle;
    }

    public static Bundle getQrCodeArguments(String qrText) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_QR_TEXT, qrText);

        return bundle;
    }

    public static Bundle getShowTextArguments(String text) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SHOW_TEXT, text);

        return bundle;
    }

    public static Bundle getQrReaderArguments(boolean readForeignKey, boolean ignoreOrigin) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_QR_READER, true);
        bundle.putBoolean(ARG_FOREIGN_KEY, readForeignKey);
        bundle.putBoolean(ARG_IGNORE_ORIGIN_KEY, ignoreOrigin);

        return bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen_fragment);
        ButterKnife.bind(this);

        Fragment fragment = null;

        if (savedInstanceState == null) {
            mShowQrReader = getIntent().getBooleanExtra(ARG_QR_READER, false);
            mReadForeignKey = getIntent().getBooleanExtra(ARG_FOREIGN_KEY, false);
            mIgnoreOriginKey = getIntent().getBooleanExtra(ARG_IGNORE_ORIGIN_KEY, false);
            mQrText = getIntent().getStringExtra(ARG_QR_TEXT);
            mShowText = getIntent().getStringExtra(ARG_SHOW_TEXT);
            mMissingKeyParts = getIntent().getIntExtra(ARG_MISSING_KEY_PARTS, -1);

            if (getIntent().hasExtra(ARG_KEY_PARTS)) {
                Parcelable[] parts = getIntent().getParcelableArrayExtra(ARG_KEY_PARTS);
                mKeyParts = new KeyPart[parts.length];
                System.arraycopy(parts, 0, mKeyParts, 0, parts.length);

            } else if (getIntent().hasExtra(ARG_KEY_PART)) {
                mKeyPart = getIntent().getParcelableExtra(ARG_KEY_PART);

                if (getIntent().hasExtra(ARG_CONTACT)) {
                    mContact = getIntent().getParcelableExtra(ARG_CONTACT);
                }
            }

            fragment = initFragment();
        } else {
            mShowQrReader = savedInstanceState.getBoolean(ARG_QR_READER, false);
            mReadForeignKey = savedInstanceState.getBoolean(ARG_FOREIGN_KEY, false);
            mIgnoreOriginKey = savedInstanceState.getBoolean(ARG_IGNORE_ORIGIN_KEY, false);
            mQrText = savedInstanceState.getString(ARG_QR_TEXT);
            mShowText = savedInstanceState.getString(ARG_SHOW_TEXT);
            mMissingKeyParts = savedInstanceState.getInt(ARG_MISSING_KEY_PARTS, -1);

            if (savedInstanceState.containsKey(ARG_KEY_PARTS)) {
                mKeyParts = (KeyPart[]) savedInstanceState.getParcelableArray(ARG_KEY_PARTS);
            } else if (savedInstanceState.containsKey(ARG_KEY_PART)) {
                mKeyPart = savedInstanceState.getParcelable(ARG_KEY_PART);

                if (savedInstanceState.containsKey(ARG_CONTACT)) {
                    mContact = savedInstanceState.getParcelable(ARG_CONTACT);
                }
            }
        }

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_fullscreen_fragment_container, fragment, "fullscreen_fragment")
                    .commit();
        }

        initToolbar();
        setDisplayHomeAsUpEnabled();
    }

    private Fragment initFragment() {
        Fragment fragment;

        if (mShowQrReader) {
            fragment = QrReaderFragment.newInstance(mReadForeignKey, mIgnoreOriginKey);
        } else if (mKeyPart != null) {
            fragment = QrCodeFragment.newInstance(mKeyPart, mContact);
        } else if (mQrText != null) {
            fragment = QrCodeFragment.newInstance(mQrText, false);
        } else if (mShowText != null) {
            fragment = QrCodeFragment.newInstance(mShowText, true);
        } else if (mKeyParts != null) {
            fragment = ContactChooserFragment.newInstance(mKeyParts);
        } else {
            fragment = ContactChooserFragment.newReceiveInstance(mMissingKeyParts);
        }

        return fragment;
    }

    private void initToolbar() {
        if (mShowQrReader) {
            initToolbar(R.string.qr_reader_title);
        } else if (mKeyPart != null) {
            initToolbar(R.string.qr_fragment_title_key_part);
        } else if (mQrText != null) {
            initToolbar(R.string.qr_fragment_title);
        } else if (mShowText != null) {
            initToolbar(R.string.qr_fragment_title_restored_text);
        } else if (mKeyParts != null) {
            initToolbar(R.string.contact_chooser_share_parts_title);
        } else {
            initToolbar(R.string.contact_chooser_collect_parts_title);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArray(ARG_KEY_PARTS, mKeyParts);
        outState.putParcelable(ARG_KEY_PART, mKeyPart);
        outState.putParcelable(ARG_CONTACT, mContact);
        outState.putBoolean(ARG_QR_READER, mShowQrReader);
        outState.putBoolean(ARG_FOREIGN_KEY, mReadForeignKey);
        outState.putInt(ARG_MISSING_KEY_PARTS, mMissingKeyParts);
        outState.putString(ARG_QR_TEXT, mQrText);
        outState.putString(ARG_SHOW_TEXT, mShowText);
        outState.putBoolean(ARG_IGNORE_ORIGIN_KEY, mIgnoreOriginKey);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (mKeyParts != null) {
                Logger.exitsChooseContactsDialog();
            }

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mKeyParts != null) {
            Logger.exitsChooseContactsDialog();
        }

        super.onBackPressed();
    }

    public void setListener(PermissionListener listener) {
        mListener = listener;
    }

    public void setKeyParts(KeyPart[] parts) {
        mKeyParts = parts;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION_CONTACTS && mListener != null) {
            mListener.contactPermissionGranted();
        }
    }

    public interface PermissionListener {
        void contactPermissionGranted();
    }
}
