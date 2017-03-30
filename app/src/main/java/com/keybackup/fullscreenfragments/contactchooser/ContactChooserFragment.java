package com.keybackup.fullscreenfragments.contactchooser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.secret.sharing.Contact;
import com.android.secret.sharing.KeyPart;
import com.keybackup.BaseFragment;
import com.keybackup.R;
import com.keybackup.fullscreenfragments.qrcodereader.QrReaderFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class ContactChooserFragment extends BaseFragment implements ContactChooserContract.View {
    private static final String ARG_KEY_PARTS = "key_parts";
    public static final String ARG_CONTACT = "contact";
    public static final String ARG_PREV_SEND_STATUS_SELECTED = "prev_send_status_selected";
    public static final String ARG_MISSING_KEY_PARTS = "missing_key_parts";

    public static final int REQUEST_KEY_SENT = 4258;
    public static final int REQUEST_KEY_RECEIVE = 4259;

    @BindView(R.id.send_description)
    TextView mDescription;
    @BindView(R.id.send_contact_list)
    ListView mListView;

    private ContactChooserPresenter mPresenter;

    public static ContactChooserFragment newInstance(KeyPart[] keyParts) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(ARG_KEY_PARTS, keyParts);

        ContactChooserFragment fragment = new ContactChooserFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public static ContactChooserFragment newReceiveInstance(int missingKeyParts) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_MISSING_KEY_PARTS, missingKeyParts);

        ContactChooserFragment fragment = new ContactChooserFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_chooser, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        if (bundle == null) {
            if (getArguments().containsKey(ARG_KEY_PARTS)) {
                Parcelable[] parts = getArguments().getParcelableArray(ARG_KEY_PARTS);
                KeyPart[] keyParts = new KeyPart[parts.length];
                System.arraycopy(parts, 0, keyParts, 0, parts.length);

                mPresenter = new ContactChooserPresenter(this, mActivity, keyParts);
            } else {
                int missingKeyParts = getArguments().getInt(ARG_MISSING_KEY_PARTS);
                mPresenter = new ContactChooserPresenter(this, mActivity, missingKeyParts);
            }

            mPresenter.setAdapter(mListView);
            mPresenter.requestContactsPermission();
        } else {
            mPresenter = new ContactChooserPresenter(this, mActivity);
            mPresenter.setAdapter(mListView);
            mPresenter.restoreState(bundle);
        }
    }

    @OnItemClick(R.id.send_contact_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPresenter.itemClicked(this, position, REQUEST_KEY_SENT);
    }

    @Override
    public void setDescription(String description) {
        if (description != null) {
            mDescription.setText(description);
        }
        mDescription.setVisibility(description == null ? View.GONE : View.VISIBLE);
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_KEY_SENT) {
            if (resultCode == Activity.RESULT_OK) {
                Contact contact = data.getParcelableExtra(ARG_CONTACT);
                boolean prevSendStatusSelected = data.getBooleanExtra(ARG_PREV_SEND_STATUS_SELECTED, false);
                mPresenter.secretSent(contact, prevSendStatusSelected);
            }
        } else if (requestCode == REQUEST_KEY_RECEIVE) {
            if (resultCode == Activity.RESULT_OK) {
                KeyPart keyPart = data.getParcelableExtra(QrReaderFragment.RESULT_KEY_PART);
                mPresenter.receivedKeyPart(keyPart);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mPresenter.saveState(outState);
    }
}
