package com.keybackup.main.backup;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.secret.sharing.SecretPresentation;
import com.keybackup.BaseFragment;
import com.keybackup.Logger;
import com.keybackup.R;
import com.keybackup.main.MainActivity;
import com.keybackup.main.SimpleAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class BackupFragment extends BaseFragment implements BackupContract.View {
    @BindView(R.id.overview_layout)
    LinearLayout mLayout;
    @BindView(R.id.overview_empty_message)
    TextView mEmptyText;
    @BindView(R.id.overview_list)
    ListView mListView;
    @BindView(R.id.overview_empty_image)
    ImageView mEmptyImage;

    private BackupPresenter mPresenter;
    private SimpleAdapter mAdapter;

    public static BackupFragment newInstance() {
        return new BackupFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new SimpleAdapter(mActivity);
        mListView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        mPresenter = new BackupPresenter(this, mSecretSharing);
        mPresenter.loadData();

        ((MainActivity) mActivity).setBackupCreatorListener(mPresenter);
    }

    @OnItemClick(R.id.overview_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Logger.startedRestoringBackup();
        mPresenter.onItemClick(mActivity, position, mAdapter.getItem(position));
    }

    @Override
    public void showData(SecretPresentation[] backups) {
        setEmptyMessageVisibility(false);
        mAdapter.setItems(backups);

    }

    @Override
    public void showSnackbar(String message) {
        Snackbar.make(mLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void addBackup(SecretPresentation backup) {
        setEmptyMessageVisibility(false);
        mAdapter.addItem(backup);
    }

    @Override
    public void showEmptyScreen() {
        setEmptyMessageVisibility(true);
        mEmptyText.setText(R.string.overview_backups_empty);
        mEmptyImage.setImageResource(R.drawable.onboarding_02);
    }

    private void setEmptyMessageVisibility(boolean visible) {
        mListView.setVisibility(visible ? View.GONE : View.VISIBLE);
        mEmptyText.setVisibility(visible ? View.VISIBLE : View.GONE);
        mEmptyImage.setVisibility(visible ? View.VISIBLE : View.GONE);
        mLayout.setGravity(visible ? Gravity.CENTER : Gravity.NO_GRAVITY);
    }
}
