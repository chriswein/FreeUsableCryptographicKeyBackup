package com.keybackup.stepperfragments;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.secret.sharing.Backup;
import com.keybackup.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StepScanSecret extends LinearLayout implements TextWatcher {
    @BindView(R.id.stepper_import_secret_store_method)
    TextView mSendMethodText;
    @BindView(R.id.stepper_import_secret_qr_reader)
    SurfaceView mQrView;
    @BindView(R.id.stepper_import_secret_description)
    TextView mDescription;
    @BindView(R.id.stepper_import_secret_edit)
    EditText mSecretEdit;

    private QrStepListener mListener;

    public StepScanSecret(Context context) {
        super(context);
        init(context);
    }

    public StepScanSecret(Context context, QrStepListener listener, Backup.BackupStoreMethod storeMethod) {
        super(context);

        mListener = listener;
        init(context);

        if (storeMethod != null) {
            if (storeMethod == Backup.BackupStoreMethod.EMAIL) {
                mSendMethodText.setVisibility(VISIBLE);
                mSendMethodText.setText(R.string.stepper_import_secret_store_method_email);
            }
            if (storeMethod == Backup.BackupStoreMethod.PRINT) {
                mSendMethodText.setVisibility(VISIBLE);
                mSendMethodText.setText(R.string.stepper_import_secret_store_method_print);
            }
        }
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);

        View view = LayoutInflater.from(context).inflate(R.layout.stepper_scan_secret, this, true);
        ButterKnife.bind(this, view);

        mSecretEdit.addTextChangedListener(this);

        mListener.initQrReader(mQrView);

        if (!mListener.enableTextInput()) {
            mDescription.setVisibility(GONE);
            mSecretEdit.setVisibility(GONE);
        }
    }

    @OnClick(R.id.stepper_import_secret_qr_reader)
    public void onCameraClick() {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mListener.setSecret(s.toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}

    public interface QrStepListener {
        void initQrReader(SurfaceView view);

        boolean enableTextInput();

        void setSecret(String secret);
    }
}
