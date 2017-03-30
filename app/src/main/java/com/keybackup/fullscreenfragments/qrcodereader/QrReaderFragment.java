package com.keybackup.fullscreenfragments.qrcodereader;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.keybackup.BaseFragment;
import com.keybackup.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QrReaderFragment extends BaseFragment implements QrReaderContract.View {
    public static final String RESULT_KEY_PART = "result_key_part";

    protected static final String ARG_READ_FOREIGN_KEY = "read_foreign_key";
    protected static final String ARG_IGNORE_ORIGIN = "ignore_origin";

    @BindView(R.id.qr_reader_view)
    SurfaceView mSurfaceView;

    private QrReaderPresenter mPresenter;
    private Snackbar mSnackbar;

    public static QrReaderFragment newInstance(boolean readForeignKey, boolean ignoreOrigin) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_READ_FOREIGN_KEY, readForeignKey);
        bundle.putBoolean(ARG_IGNORE_ORIGIN, ignoreOrigin);

        QrReaderFragment fragment = new QrReaderFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_code_reader, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        bundle = bundle == null ? getArguments() : bundle;
        boolean foreignKey = bundle.getBoolean(ARG_READ_FOREIGN_KEY);
        boolean ignoreOrigin = bundle.getBoolean(ARG_IGNORE_ORIGIN);

        mPresenter = new QrReaderPresenter(this, mActivity, foreignKey, ignoreOrigin);

        mPresenter.showCamera(mSurfaceView);
    }

    @Override
    public void showMessage(String message) {
        if (mSnackbar == null || !mSnackbar.isShownOrQueued()) {
            mSnackbar = Snackbar.make(mSurfaceView, message, Snackbar.LENGTH_LONG);
            mSnackbar.show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mPresenter.saveState(outState);
    }
}
