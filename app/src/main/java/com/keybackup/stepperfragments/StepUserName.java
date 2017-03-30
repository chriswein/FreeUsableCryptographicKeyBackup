package com.keybackup.stepperfragments;

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

public class StepUserName extends LinearLayout implements TextWatcher {
    @BindView(R.id.stepper_user_name_edit)
    EditText mNameEdit;

    UserNameListener mListener;

    public StepUserName(Context context) {
        super(context);
        init(context);
    }

    public StepUserName(Context context, UserNameListener listener) {
        super(context);

        mListener = listener;
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);

        View view = LayoutInflater.from(context).inflate(R.layout.stepper_user_name, this, true);
        ButterKnife.bind(this, view);

        mNameEdit.addTextChangedListener(this);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mListener != null) {
            mListener.setUserName(s.toString());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}

    public interface UserNameListener {
        void setUserName(String name);
    }
}
