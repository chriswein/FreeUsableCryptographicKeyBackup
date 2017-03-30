package com.keybackup.main.todo;

import android.support.v7.widget.RecyclerView;

import com.keybackup.BaseActivity;

interface TodoContract {

    interface TodoView {

        void showMessage(String message);

    }

    interface UserActionsListener {

        void setAdapter(RecyclerView view);

        void loadCards(BaseActivity activity);

    }
}
