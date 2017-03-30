package com.keybackup.main.todo.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.keybackup.main.todo.adapter.item.TodoItem;

public abstract class BaseViewHolder<T extends TodoItem> extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void setDataOnView(Context context, int position, T item);
}
