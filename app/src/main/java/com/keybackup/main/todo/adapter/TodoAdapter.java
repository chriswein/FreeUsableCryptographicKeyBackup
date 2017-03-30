package com.keybackup.main.todo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.keybackup.main.todo.adapter.item.TodoItem;
import com.keybackup.main.todo.adapter.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Structure followed by http://stackoverflow.com/a/34464367/6644308
 */
public class TodoAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context mContext;
    private List<TodoItem> mItems = new ArrayList<>();
    private int mSize;

    public TodoAdapter(Context context) {
        mContext = context;
    }

    public void addItem(TodoItem item) {
        mItems.add(item);
        mSize++;

        notifyDataSetChanged();
    }

    public void removeItem(TodoItem item) {
        boolean removed = mItems.remove(item);

        if (removed) {
            mSize--;
            notifyDataSetChanged();
        }
    }

    public void clear() {
        mItems.clear();
        mSize = 0;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getItemViewType().getId();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewType type = ViewType.getViewType(viewType);

        View view = LayoutInflater.from(parent.getContext()).inflate(type.getLayoutId(), parent, false);
        BaseViewHolder holder = ViewType.getViewHolder(type, view);

        ButterKnife.bind(holder, view);

        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        TodoItem item = mItems.get(position);
        holder.setDataOnView(mContext, position, item);
    }

    @Override
    public int getItemCount() {
        return mSize;
    }
}
