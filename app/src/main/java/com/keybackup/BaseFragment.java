package com.keybackup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.android.secret.sharing.AndroidSecretSharing;

public abstract class BaseFragment extends Fragment {
    protected AndroidSecretSharing mSecretSharing;
    protected BaseActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (BaseActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        mSecretSharing = mActivity.getSecretSharing();
    }
}
