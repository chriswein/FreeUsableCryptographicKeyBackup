package com.keybackup.stepperfragments.createbackup;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keybackup.R;

import butterknife.ButterKnife;


public class StepShowSecret extends LinearLayout {

    String secret = "SECRET SECRET";

    public StepShowSecret(Context context) {
        super(context);
        init(context);
    }


    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);

        TextView view = (TextView) LayoutInflater.from(context).inflate(R.layout.stepper_show_secret, this, true);
        view.setText("Your secret: \\n"+secret);
        ButterKnife.bind(this, view);

    }
}
