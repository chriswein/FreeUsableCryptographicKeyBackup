package com.keybackup.main.todo.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.keybackup.R;
import com.keybackup.main.todo.adapter.item.TodoContactItem;

import butterknife.BindView;

public class ContactViewHolder extends BaseViewHolder<TodoContactItem> {
    @BindView(R.id.todo_item_contact_headline)
    TextView mHeadline;
    @BindView(R.id.todo_item_contact_subtitle)
    TextView mSubtitle;
    @BindView(R.id.todo_item_contact_image)
    ImageView mContactImage;
    @BindView(R.id.todo_item_contact_positive)
    Button mPositive;
    @BindView(R.id.todo_item_contact_show)
    Button mShow;
    @BindView(R.id.todo_item_contact_more)
    Button mMore;

    public ContactViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setDataOnView(Context context, int position, TodoContactItem item) {
        mHeadline.setText(item.getHeadline());
        mSubtitle.setText(item.getSubtitle());

        item.setContactImage(context, mContactImage);

        if (item.getPositiveButtonListener() != null) {
            mPositive.setVisibility(View.VISIBLE);
            mPositive.setOnClickListener(item.getPositiveButtonListener());
        } else {
            mPositive.setVisibility(View.GONE);
        }

        mShow.setText(item.getShowButtonTitle());
        mShow.setOnClickListener(item.getShowButtonListener());

        mMore.setText(item.getMoreButtonTitle());
        mMore.setOnClickListener(item.getMoreButtonListener());
    }
}
