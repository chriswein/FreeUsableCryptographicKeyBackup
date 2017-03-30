package com.keybackup.intentbackup;


import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.keybackup.BaseActivity;
import com.keybackup.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntentBackupDialogActivity extends BaseActivity implements IntentBackupContract.View {
    @BindView(R.id.activity_backup_dialog_edit)
    EditText mBackupName;

    private IntentBackupPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String intentType = intent.getType();
        String secret = null;

        if ("com.keybackup.dialog.SECRET".equals(intent.getAction()) && intentType != null && intentType.equals("text/plain")) {
            secret = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        if (secret == null) {
            finish();
            return;
        }

        mPresenter = new IntentBackupPresenter(this, mSecretSharing, secret, savedInstanceState);

        setContentView(R.layout.activity_backup_dialog);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.activity_backup_dialog_cancel)
    public void onClickCancel() {
        finish();
    }

    @OnClick(R.id.activity_backup_dialog_ok)
    public void onClickOk() {
        String backupName = mBackupName.getText().toString();
        mPresenter.saveToCloud(backupName);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mPresenter.saveState(outState);
    }
}
