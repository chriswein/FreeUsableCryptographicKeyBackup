package com.keybackup.onboarding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.keybackup.BaseFragment;
import com.keybackup.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnboardingFragment extends BaseFragment {
    private static final String ARG_POSITION = "position";

    @BindView(R.id.onboarding_image)
    ImageView mImage;
    @BindView(R.id.onboarding_headline)
    TextView mHeadline;
    @BindView(R.id.onboarding_subhead)
    TextView mSubHead;

    private int mPosition;

    public static Fragment newInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);

        OnboardingFragment fragment = new OnboardingFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mPosition = arguments.getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);
        ButterKnife.bind(this, view);

        switch (mPosition) {
            case 0:
                updateViews(R.drawable.onboarding_01, R.string.onboarding_headline_backup, R.string.onboarding_subhead_backup);
                break;
            case 1:
                updateViews(R.drawable.onboarding_02, R.string.onboarding_headline_key_created, R.string.onboarding_subhead_key_created);
                break;
            case 2:
                updateViews(R.drawable.onboarding_03, R.string.onboarding_headline_share, R.string.onboarding_subhead_share);
                break;
            case 3:
                updateViews(R.drawable.onboarding_04, R.string.onboarding_headline_restore, R.string.onboarding_subhead_restore);
                break;
        }

        return view;
    }

    private void updateViews(int imageId, int headlineId, int explanationId) {
        mImage.setImageResource(imageId);
        mHeadline.setText(headlineId);
        mSubHead.setText(explanationId);
    }
}
