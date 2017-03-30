package com.keybackup.stepperfragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.secret.sharing.AndroidSecretSharing;
import com.keybackup.BaseActivity;
import com.keybackup.DialogFinishListener;
import com.keybackup.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperSecondButton;

public abstract class BaseCreateDialog extends DialogFragment implements VerticalStepperForm, VerticalStepperSecondButton {
    @BindView(R.id.dialog_create_backup_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.dialog_create_backup_message)
    protected TextView mMessage;
    @BindView(R.id.dialog_create_backup_stepper)
    protected VerticalStepperFormLayout mStepper;


    protected BaseActivity mActivity;
    protected AndroidSecretSharing mSecretSharing;

    private boolean mAddedUserNameStep;
    private DialogFinishListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (BaseActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        mSecretSharing = mActivity.getSecretSharing();
        initStepper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_create_backup, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, view);

        mToolbar.setTitle(getToolbarTitle());
        mActivity.setSupportActionBar(mToolbar);

        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }

        return view;
    }

    private void initStepper() {
        String[] titles = getTitles();
        if (!mSecretSharing.hasUserName() && showUserNameStep()) {
            mAddedUserNameStep = true;

            String[] newTitles = new String[titles.length + 1];
            System.arraycopy(titles, 0, newTitles, 0, titles.length);

            newTitles[newTitles.length - 1] = getResources().getString(R.string.dialog_step_enter_name);

            titles = newTitles;
        }

        int colorPrimary = ContextCompat.getColor(mActivity, R.color.colorPrimary);
        int colorPrimaryDark = ContextCompat.getColor(mActivity, R.color.colorPrimaryDark);

        VerticalStepperFormLayout.Builder stepperBuilder = VerticalStepperFormLayout.Builder.newInstance(mStepper, titles, this, mActivity)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(false)
                .setupLastStep(getLastStepTitle(), getLastStepButtonTitle());

        String lastStepSecondButtonTitle = getLastStepSecondButtonTitle();
        if (lastStepSecondButtonTitle != null) {
            stepperBuilder.addSecondButtonLastStep(lastStepSecondButtonTitle, this);
        }

        stepperBuilder.init();

        if (mAddedUserNameStep) {
            mStepper.setStepSubtitle(titles.length - 1, getString(R.string.stepper_subtitle_optional));
        }
    }

    protected abstract boolean showUserNameStep();

    protected abstract String getLastStepTitle();

    protected abstract String getLastStepButtonTitle();

    protected abstract String getLastStepSecondButtonTitle();

    protected abstract String[] getTitles();

    protected abstract String getToolbarTitle();

    public boolean addedUserNameStep() {
        return mAddedUserNameStep;
    }

    public void setListener(DialogFinishListener listener) {
        mListener = listener;
    }

    @Override
    public void dismiss() {
        super.dismiss();

        mActivity.getSupportFragmentManager().popBackStack();

        if (mListener != null) {
            mListener.onFinished();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            dismiss();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
