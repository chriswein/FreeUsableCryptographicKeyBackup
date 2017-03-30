package com.keybackup.main.todo.adapter;

import android.view.View;

import com.keybackup.R;
import com.keybackup.main.todo.adapter.viewholder.BaseViewHolder;
import com.keybackup.main.todo.adapter.viewholder.ContactViewHolder;
import com.keybackup.main.todo.adapter.viewholder.ProgressViewHolder;
import com.keybackup.main.todo.adapter.viewholder.TextViewHolder;

public enum ViewType {
    TEXT(1, R.layout.list_todo_item_text),
    CONTACT(2, R.layout.list_todo_item_contact),
    PROGRESS(3, R.layout.list_todo_item_progress);

    int id;
    int layoutId;

    ViewType(int id, int layoutId) {
        this.id = id;
        this.layoutId = layoutId;
    }

    public int getId() {
        return id;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public static ViewType getViewType(int viewTypeId) {
        switch (viewTypeId) {
            case 1:
                return TEXT;
            case 2:
                return CONTACT;
            case 3:
                return PROGRESS;
            default:
                throw new IllegalArgumentException("No view type defined for id: " + viewTypeId);
        }
    }

    public static BaseViewHolder getViewHolder(ViewType viewType, View view) {
        switch (viewType) {
            case TEXT:
                return new TextViewHolder(view);
            case CONTACT:
                return new ContactViewHolder(view);
            case PROGRESS:
                return new ProgressViewHolder(view);
            default:
                throw new IllegalArgumentException("No view holder defined for view type: " + viewType);
        }
    }
}
