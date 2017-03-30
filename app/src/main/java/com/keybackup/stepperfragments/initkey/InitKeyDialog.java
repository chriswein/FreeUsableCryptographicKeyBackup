package com.keybackup.stepperfragments.initkey;

import android.os.Bundle;
import android.view.View;

import com.keybackup.R;
import com.keybackup.stepperfragments.BaseCreateDialog;
import com.keybackup.stepperfragments.StepUserName;

public class InitKeyDialog extends BaseCreateDialog implements InitKeyContract.View {
    public static final String TAG = "share_key_dialog";
    protected static final String ARG_SHOW_MESSAGE = "show_message";

    private InitKeyPresenter mPresenter;

    public static InitKeyDialog newInstance(boolean showMessage) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_SHOW_MESSAGE, showMessage);

        InitKeyDialog dialog = new InitKeyDialog();
        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new InitKeyPresenter(mActivity, this);
    }

    @Override
    protected boolean showUserNameStep() {
        return true;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        mPresenter.restore(bundle, getArguments());
        mPresenter.createContainer(mActivity.getSecretSharing());
    }

    @Override
    protected String getLastStepTitle() {
        return getString(R.string.stepper_last_step_key_title);
    }

    @Override
    protected String getLastStepButtonTitle() {
        return getString(R.string.stepper_last_step_key_second_button);
    }

    @Override
    protected String getLastStepSecondButtonTitle() {
        return getString(R.string.stepper_last_step_button);
    }

    @Override
    protected String[] getTitles() {
        return getResources().getStringArray(R.array.create_dialog_stepper_titles_private_key);
    }

    @Override
    protected String getToolbarTitle() {
        return mActivity.getString(R.string.dialog_share_key_title);
    }

    @Override
    public View createStepContentView(int stepNumber) {
        switch (stepNumber) {
            case 0:
                return new StepMinimumParts(mActivity, mPresenter);
            case 1:
                if (addedUserNameStep()) {
                    return new StepUserName(mActivity, mPresenter);
                }
            default:
                throw new IllegalArgumentException("No view defined for step " + stepNumber);
        }
    }

    @Override
    public void onStepOpening(int stepNumber) {
        mPresenter.onStepOpening(stepNumber);
    }

    @Override
    public void sendData() {
        mPresenter.showContactList();
        dismiss();
    }

    @Override
    public void onClickSecondButton() {
        mPresenter.saveKeyParts(false);
        dismiss();
    }

    @Override
    public void stepCompleted() {
        mStepper.setActiveStepAsCompleted();
    }

    @Override
    public void stepNotCompleted(String errorMessage) {
        mStepper.setActiveStepAsUncompleted(errorMessage);
    }

    @Override
    public void showMessage() {
        mMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mPresenter.saveInstanceState(outState);
    }
}
