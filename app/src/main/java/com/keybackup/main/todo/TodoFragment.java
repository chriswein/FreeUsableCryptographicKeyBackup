package com.keybackup.main.todo;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.keybackup.BaseActivity;
import com.keybackup.BaseFragment;
import com.keybackup.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoFragment extends BaseFragment implements TodoContract.TodoView {
    @BindView(R.id.todo_recycler_view)
    RecyclerView mRecyclerView;

    private TodoPresenter mPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        ButterKnife.bind(this, view);

        // changes in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.card_padding)));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        mPresenter = new TodoPresenter(mActivity, this, ((BaseActivity) getActivity()).getSecretSharing());
        mPresenter.setAdapter(mRecyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();

        mPresenter.loadCards(mActivity);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG).show();
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int mVerticalSpaceHeight;

        public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = parent.getChildAdapterPosition(view) == 0 ? mVerticalSpaceHeight : 0;
            outRect.bottom = mVerticalSpaceHeight;
            outRect.left = mVerticalSpaceHeight;
            outRect.right = mVerticalSpaceHeight;
        }
    }
}
