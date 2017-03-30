package com.keybackup.main.todo.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.keybackup.R;
import com.keybackup.main.todo.adapter.item.TodoTextItem;

import butterknife.BindView;

public class TextViewHolder extends BaseViewHolder<TodoTextItem> {
    @BindView(R.id.todo_item_text_remove)
    ImageButton mRemoveCardButton;
    @BindView(R.id.todo_item_text_headline)
    TextView mHeadline;
    @BindView(R.id.todo_item_text_subhead)
    TextView mDescription;
    @BindView(R.id.todo_item_text_button)
    Button mButton;
    @BindView(R.id.todo_item_second_text_button)
    Button mSecondButton;

    public TextViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataOnView(Context context, int position, TodoTextItem item) {
        mHeadline.setText(item.getHeadline());
        mDescription.setText(item.getDescription());

        if (item.isRemovable()) {
            mRemoveCardButton.setVisibility(View.VISIBLE);
            mRemoveCardButton.setOnClickListener(item.getRemoveListener());
        } else {
            mRemoveCardButton.setVisibility(View.GONE);
        }

        if (item.hasButton()) {
            mButton.setVisibility(View.VISIBLE);
            mButton.setText(item.getButtonTitle());
            mButton.setOnClickListener(item.getOnClickListener());
        } else {
            mButton.setVisibility(View.GONE);
        }

        if (item.hasSecondButton()) {
            mSecondButton.setVisibility(View.VISIBLE);
            mSecondButton.setText(item.getSecondButtonTitle());
            mSecondButton.setOnClickListener(item.getSecondOnClickListener());
        } else {
            mSecondButton.setVisibility(View.GONE);
        }

        mHeadline.setTextColor(item.getHeadlineColor());
    }
}
