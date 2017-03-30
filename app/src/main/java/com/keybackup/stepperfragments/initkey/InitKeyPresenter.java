package com.keybackup.stepperfragments.initkey;

import android.app.Activity;
import android.os.Bundle;

import com.android.secret.sharing.AndroidSecretSharing;
import com.android.secret.sharing.Container;
import com.android.secret.sharing.KeyPart;
import com.keybackup.Logger;
import com.keybackup.Preferences;
import com.keybackup.R;
import com.keybackup.Utils;
import com.keybackup.stepperfragments.StepUserName;
import com.keybackup.fullscreenfragments.FullscreenFragmentActivity;

public class InitKeyPresenter implements InitKeyContract.UserActionsListener, StepUserName.UserNameListener {
    private static final int MIN_ALLOWED_KEY_PARTS = 3;

    private InitKeyContract.View mView;
    private AndroidSecretSharing mSecretSharing;
    private Container mContainer;
    private Activity mActivity;

    private int mMinParts = 5;
    private int mTotalParts = 10;
    private String mUserName;
    private boolean mShowMessage;
    private boolean mStepsCreated;

    public InitKeyPresenter(Activity activity, InitKeyContract.View view) {
        mActivity = activity;
        mView = view;
    }

    @Override
    public void createContainer(AndroidSecretSharing secretSharing) {
        mSecretSharing = secretSharing;

        mContainer = secretSharing.newContainer();
        mContainer.setMinimumRecoverParts(mMinParts);
        mContainer.setTotalParts(mTotalParts);
    }

    @Override
    public void setMinimumParts(int parts) {
        mMinParts = parts;

        if (mContainer != null) {
            mContainer.setMinimumRecoverParts(parts);
        }

        checkContinueButton();
    }

    @Override
    public void setTotalParts(int parts) {
        mTotalParts = parts;

        if (mContainer != null) {
            mContainer.setTotalParts(parts);
        }

        checkContinueButton();
    }

    @Override
    public void showContactList() {
        KeyPart[] parts = saveKeyParts(true);

        Logger.opensChooseContactsDialog("Key settings dialog");
        Bundle bundle = FullscreenFragmentActivity.getContactListArguments(parts);
        Utils.startActivity(mActivity, FullscreenFragmentActivity.class, false, bundle, -1);
    }

    @Override
    public KeyPart[] saveKeyParts(boolean chooseContactsNow) {
        Logger.finishSplitKey(mActivity, mMinParts, mTotalParts, mUserName, chooseContactsNow);

        Preferences.getPreferences(mActivity).containerCreated();
        return mContainer.createPrivateKeyParts(mActivity);
    }

    @Override
    public void onStepOpening(int stepNumber) {
        mStepsCreated = true;

        switch (stepNumber) {
            case 0:
                checkContinueButton();
                break;
            case 1:
                mView.stepCompleted();
                break;
        }
    }

    @Override
    public void saveInstanceState(Bundle outState) {
        outState.putBoolean(InitKeyDialog.ARG_SHOW_MESSAGE, mShowMessage);
    }

    @Override
    public void restore(Bundle savedInstanceState, Bundle arguments) {
        if (savedInstanceState != null) {
            mShowMessage = savedInstanceState.getBoolean(InitKeyDialog.ARG_SHOW_MESSAGE);
        } else {
            mShowMessage = arguments.getBoolean(InitKeyDialog.ARG_SHOW_MESSAGE);
        }

        if (mShowMessage) {
            mView.showMessage();
        }
    }

    @Override
    public void setUserName(String name) {
        mUserName = name;
        mSecretSharing.setUserName(name);
    }

    private void checkContinueButton() {
        if (!mStepsCreated) {
            return;
        }

        if (mMinParts < MIN_ALLOWED_KEY_PARTS) {
            mView.stepNotCompleted(String.format(mActivity.getString(R.string.stepper_minimum_parts_error), MIN_ALLOWED_KEY_PARTS));
        } else if (mTotalParts < mMinParts) {
            mView.stepNotCompleted(mActivity.getString(R.string.stepper_total_parts_error));
        } else {
            mView.stepCompleted();
        }
    }
}
