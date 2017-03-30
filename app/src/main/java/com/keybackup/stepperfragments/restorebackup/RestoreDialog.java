package com.keybackup.stepperfragments.restorebackup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.android.secret.sharing.Backup;
import com.android.secret.sharing.KeyPart;
import com.keybackup.Logger;
import com.keybackup.R;
import com.keybackup.stepperfragments.BaseCreateDialog;
import com.keybackup.fullscreenfragments.FullscreenFragmentActivity;

import java.util.ArrayList;

public class RestoreDialog extends BaseCreateDialog implements RestoreContract.View {
    public static final String TAG = "Restore Dialog";

    private static final String ARG_BACKUP = "backup";

    private RestorePresenter mPresenter;
    private Backup mBackup;
    private String mToolbarTitle;

    public static RestoreDialog newInstance(Backup backup) {
        Bundle bundle = new Bundle();

        if (backup != null) {
            bundle.putParcelable(ARG_BACKUP, backup);
        }

        RestoreDialog dialog = new RestoreDialog();
        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            if (getArguments().containsKey(ARG_BACKUP)) {
                mBackup = getArguments().getParcelable(ARG_BACKUP);
            }
        } else if (savedInstanceState.containsKey(ARG_BACKUP)) {
            mBackup = savedInstanceState.getParcelable(ARG_BACKUP);
        }

        mPresenter = new RestorePresenter(this, this, mActivity, mBackup);

        mToolbarTitle = String.format(mActivity.getString(R.string.dialog_restore_backup_title), mBackup != null ? mBackup.getName() : "");
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        mPresenter.showCamera(mActivity.getSecretSharing());
    }

    @Override
    protected boolean showUserNameStep() {
        return false;
    }

    @Override
    protected String getLastStepTitle() {
        return getString(R.string.stepper_last_step_collect_shares_title);
    }

    @Override
    protected String getLastStepButtonTitle() {
        return getString(R.string.stepper_last_step_collect_shares_button);
    }

    @Override
    protected String getLastStepSecondButtonTitle() {
        return getString(R.string.stepper_last_step_collect_shares_second_button);
    }

    @Override
    protected String[] getTitles() {
        String[] titles = getResources().getStringArray(R.array.restore_backup_stepper_titles);

        if (mBackup.isAvailableInCloudStorage()) {
            titles = new String[] { titles[0] };
        }

        return titles;
    }

    @Override
    protected String getToolbarTitle() {
        return mToolbarTitle;
    }

    @Override
    public View createStepContentView(int stepNumber) {
        return mPresenter.createStepContentView(stepNumber);
    }

    @Override
    public void onStepOpening(int stepNumber) {
        mPresenter.onStepOpening(stepNumber);
    }

    @Override
    public void sendData() {
        Logger.finishedRestoringBackup(true);
        mPresenter.showQrCode();
    }

    @Override
    public void onClickSecondButton() {
        Logger.finishedRestoringBackup(false);
        mPresenter.showText();
    }

    @Override
    public void stepCompleted() {
        mStepper.setActiveStepAsCompleted();
    }

    @Override
    public void stepNotCompleted() {
        mStepper.setActiveStepAsUncompleted(null);
    }

    @Override
    public void nextStep() {
        mStepper.goToNextStep();
    }

    @Override
    public void showSnackbar(String message) {
        Snackbar.make(mStepper, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RestorePresenter.REQUEST_MISSING_PARTS && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra(FullscreenFragmentActivity.RESULT_RECEIVED_KEY_PARTS)) {
                ArrayList<KeyPart> receivedKeyParts = data.getParcelableArrayListExtra(FullscreenFragmentActivity.RESULT_RECEIVED_KEY_PARTS);

                mPresenter.setKeyParts(mSecretSharing.keyListToArray(receivedKeyParts));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mBackup != null) {
            outState.putParcelable(ARG_BACKUP, mBackup);
        }
    }
}
