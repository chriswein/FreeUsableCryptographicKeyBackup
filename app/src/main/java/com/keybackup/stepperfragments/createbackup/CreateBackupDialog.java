package com.keybackup.stepperfragments.createbackup;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.android.secret.sharing.SecretPresentation;
import com.keybackup.R;
import com.keybackup.stepperfragments.BaseCreateDialog;
import com.keybackup.stepperfragments.StepScanSecret;

public class CreateBackupDialog extends BaseCreateDialog implements CreateBackupContract.View {
    public static final String TAG = "CreateBackupDialog";

    static final String ARG_SECRET = "arg_secret";
    static final String ARG_NAME = "arg_name";


    private CreateBackupPresenter mPresenter;
    private BackupCreatorListener mListener;

    public static CreateBackupDialog newInstance(String secret) {
        return newInstance(secret, null);
    }

    public static CreateBackupDialog newInstance(String secret, String name) {
        Bundle bundle = new Bundle();
        if (secret != null) {
            bundle.putString(ARG_SECRET, secret);
        }
        bundle.putString(ARG_NAME,name);


        CreateBackupDialog dialog = new CreateBackupDialog();
        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new CreateBackupPresenter(this, mActivity, savedInstanceState, getArguments());
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        mPresenter.showCamera();
    }

    @Override
    protected boolean showUserNameStep() {
        return false;
    }

    public void setBackupCreatorListener(BackupCreatorListener listener) {
        mListener = listener;
    }

    @Override
    protected String getLastStepTitle() {
        return getString(R.string.stepper_last_step_backup_title);
    }

    @Override
    protected String getLastStepButtonTitle() {
        return getString(R.string.stepper_last_step_button_cloud);
    }

    @Override
    protected String getLastStepSecondButtonTitle() {
        if (Build.VERSION.SDK_INT < 19) {
            return null;
        }
        return getString(R.string.stepper_last_step_backup_second_button);
    }

    @Override
    protected String[] getTitles() {
        return getResources().getStringArray(R.array.create_dialog_stepper_titles);
    }

    @Override
    protected String getToolbarTitle() {
        return mActivity.getString(R.string.dialog_create_backup_title);
    }

    @Override
    public View createStepContentView(int stepNumber) {
        String name = getArguments().getString(ARG_NAME);
        switch (stepNumber) {
            case 0:
                return new StepBackupName(mActivity, mPresenter, name);
            case 1:
                return new StepScanSecret(mActivity, mPresenter, null);
            case 2:
                return new StepShowSecret(mActivity);
            default:
                throw new IllegalArgumentException("No view defined for step " + stepNumber);
        }
    }

    @Override
    public void onStepOpening(int stepNumber) {
        mPresenter.onStepOpening(stepNumber, addedUserNameStep());
    }

    @Override
    public void sendData() {
        boolean finish = mPresenter.saveToCloud(mListener);

        if (finish) {
            dismiss();
        }
    }

    @Override
    public void onClickSecondButton() {
        boolean finish = mPresenter.printBackup(mListener);

        if (finish) {
            dismiss();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mPresenter.saveState(outState);
    }

    @Override
    public void stepCompleted() {
        // TODO: workaround while opening secret via intent
        mStepper.post(new Runnable() {
            @Override
            public void run() {
                mStepper.setActiveStepAsCompleted();
            }
        });
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
    public void setNameSubtitle(String name) {
        mStepper.setStepSubtitle(0, name);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mStepper, message, Snackbar.LENGTH_LONG).show();
    }

    public interface BackupCreatorListener {
        void backupCreated(SecretPresentation backup);
    }
}