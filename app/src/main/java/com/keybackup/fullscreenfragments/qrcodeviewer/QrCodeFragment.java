package com.keybackup.fullscreenfragments.qrcodeviewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.secret.sharing.Contact;
import com.android.secret.sharing.KeyPart;
import com.keybackup.BaseFragment;
import com.keybackup.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QrCodeFragment extends BaseFragment implements QrCodeContract.View {
    private static final String ARG_KEY_PART = "key_part";
    private static final String ARG_CONTACT = "contact";
    private static final String ARG_QR_TEXT = "qr_text";
    private static final String ARG_SHOW_TEXT = "show_text";

    @BindView(R.id.qr_code_description)
    TextView mDescription;
    @BindView(R.id.qr_code_image)
    ImageView mQrImage;
    @BindView(R.id.qr_code_text)
    TextView mQrText;
    @BindView(R.id.qr_code_transferred)
    Button mQrCodeTransferred;
    @BindView(R.id.qr_code_transfer_later)
    Button mQrCodeTransferLater;

    private QrCodePresenter mPresenter;

    public static QrCodeFragment newInstance(KeyPart keyPart, Contact contact) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_KEY_PART, keyPart);
        bundle.putParcelable(ARG_CONTACT, contact);

        QrCodeFragment fragment = new QrCodeFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public static QrCodeFragment newInstance(String qrText, boolean showText) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_QR_TEXT, qrText);
        bundle.putBoolean(ARG_SHOW_TEXT, showText);

        QrCodeFragment fragment = new QrCodeFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_code_viewer, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        if (getArguments().containsKey(ARG_KEY_PART)) {
            KeyPart keyPart = getArguments().getParcelable(ARG_KEY_PART);
            Contact contact = null;

            if (getArguments().containsKey(ARG_CONTACT)) {
                contact = getArguments().getParcelable(ARG_CONTACT);
            }

            mPresenter = new QrCodePresenter(this, mActivity, keyPart, contact);
        } else {
            String qrText = getArguments().getString(ARG_QR_TEXT);
            boolean showText = getArguments().getBoolean(ARG_SHOW_TEXT);
            mPresenter = new QrCodePresenter(this, mActivity, qrText, showText);
        }

        // wait till the size of the image view is calculated to generate the qr code with this size
        mQrImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){

                    @Override
                    public void onGlobalLayout() {
                        mQrImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        mPresenter.init(mQrImage, mQrText);
                    }
        });
    }

    @OnClick(R.id.qr_code_transferred)
    public void onTransferredClicked() {
        mPresenter.secretTransferred();
    }

    @OnClick(R.id.qr_code_transfer_later)
    public void onTransferLaterClicked() {
        mPresenter.transferLater();
    }

    @Override
    public void setDescription(String description) {
        mDescription.setText(description);
    }

    @Override
    public void hideTransferLaterButton() {
        mQrCodeTransferLater.setVisibility(View.GONE);
    }

    @Override
    public void hideTransferButton() {
        mQrCodeTransferred.setVisibility(View.GONE);
    }
}
