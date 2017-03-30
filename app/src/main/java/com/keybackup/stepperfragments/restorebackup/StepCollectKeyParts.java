package com.keybackup.stepperfragments.restorebackup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.keybackup.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepCollectKeyParts extends LinearLayout {
    @BindView(R.id.stepper_collect_shares_open_contact)
    Button mOpenContacts;

    public StepCollectKeyParts(Context context) {
        super(context);
        init(context, null);
    }

    public StepCollectKeyParts(Context context, OnClickListener onClickListener) {
        super(context);

        init(context, onClickListener);
    }

    private void init(Context context, OnClickListener onClickListener) {
        setOrientation(LinearLayout.VERTICAL);

        View view = LayoutInflater.from(context).inflate(R.layout.stepper_collect_key_parts, this, true);
        ButterKnife.bind(this, view);

        if (onClickListener != null) {
            mOpenContacts.setOnClickListener(onClickListener);
        }
    }
}
