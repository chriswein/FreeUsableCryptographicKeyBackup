package com.keybackup.stepperfragments.createbackup;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.keybackup.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepBackupName extends LinearLayout implements TextWatcher {
    @BindView(R.id.stepper_backup_name_edit)
    EditText mNameEdit;

    private CreateBackupPresenter mPresenter;

    public StepBackupName(Context context) {
        super(context);
        init(context, null);
    }

    public StepBackupName(Context context, CreateBackupPresenter presenter, String name) {
        super(context);

        mPresenter = presenter;
        init(context, name);
    }

    private void init(Context context, String name) {
        setOrientation(LinearLayout.VERTICAL);

        View view = LayoutInflater.from(context).inflate(R.layout.stepper_backup_name, this, true);
        ButterKnife.bind(this, view);

        if(name != null){
            mNameEdit.setText(name);
            mPresenter.setBackupName(name);
        }
        mNameEdit.addTextChangedListener(this);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mPresenter.setBackupName(s.toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}
}
