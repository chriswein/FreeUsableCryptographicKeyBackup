package com.keybackup.main.todo.adapter.viewholder;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.keybackup.R;
import com.keybackup.main.todo.adapter.item.TodoProgressItem;

import butterknife.BindView;

public class ProgressViewHolder extends BaseViewHolder<TodoProgressItem> {
    @BindView(R.id.todo_item_progress_headline)
    TextView mHeadline;
    @BindView(R.id.todo_item_progress_subhead)
    TextView mDescription;
    @BindView(R.id.todo_item_progress_progress)
    TextView mProgress;
    @BindView(R.id.todo_item_progress_bar)
    SeekBar mBar;
    @BindView(R.id.todo_item_progress_button)
    Button mButton;

    public ProgressViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataOnView(Context context, int position, TodoProgressItem item) {
        mHeadline.setText(item.getHeadline());
        mDescription.setText(item.getDescription());
        mProgress.setText(item.getProgressStatus());

        mBar.setMax(item.getMaxProgress());
        mBar.setProgress(item.getCurrentProgress());

        // remove point from seekbar because user should not try to change value from card
        mBar.setThumb(null);
        mBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mBar.setScaleY(3f);

        mButton.setText(item.getButtonTitle());
        mButton.setOnClickListener(item.getOnClickListener());
    }
}
