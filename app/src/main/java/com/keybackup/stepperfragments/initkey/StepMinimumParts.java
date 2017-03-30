package com.keybackup.stepperfragments.initkey;

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

public class StepMinimumParts extends LinearLayout {
    @BindView(R.id.stepper_minimum_edit)
    EditText mMinimumEdit;
    @BindView(R.id.stepper_total_edit)
    EditText mTotalEdit;

    private InitKeyPresenter mPresenter;

    public StepMinimumParts(Context context) {
        super(context);
        init(context);
    }

    public StepMinimumParts(Context context, InitKeyPresenter presenter) {
        super(context);

        mPresenter = presenter;
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);

        View view = LayoutInflater.from(context).inflate(R.layout.stepper_minimum_parts, this, true);
        ButterKnife.bind(this, view);

        mMinimumEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                valuesUpdated(s, true);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mTotalEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                valuesUpdated(s, false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // update values in presenter according to default values in layout file
        valuesUpdated(mMinimumEdit.getText(), true);
        valuesUpdated(mTotalEdit.getText(), false);
    }

    private void valuesUpdated(CharSequence value, boolean minimum) {
        if (mPresenter != null) {
            int parts = value.length() > 0 ? Integer.valueOf(value.toString()) : 0;

            if (minimum) {
                mPresenter.setMinimumParts(parts);
            } else {
                mPresenter.setTotalParts(parts);
            }
        }
    }
}
