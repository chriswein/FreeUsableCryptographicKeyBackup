package com.keybackup.main.todo.adapter.item;

import android.content.res.Resources;
import android.view.View;

import com.keybackup.main.todo.adapter.ViewType;

public class TodoProgressItem implements TodoItem {
    private String mHeadline;
    private String mDescription;
    private String mProgressStatus;
    private int mMaxProgress;
    private int mCurrentProgress;
    private String mButtonTitle;
    private View.OnClickListener mButtonListener;


    public String getHeadline() {
        return mHeadline;
    }

    public void setHeadline(String headline) {
        this.mHeadline = headline;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setProgressStatus(String status) {
        mProgressStatus = status;
    }

    public String getProgressStatus() {
        return mProgressStatus;
    }

    public void setMaxProgress(int max) {
        mMaxProgress = max;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setCurrentProgress(int progress) {
        mCurrentProgress = progress;
    }

    public int getCurrentProgress() {
        return mCurrentProgress;
    }

    public void setButton(Resources resources, int titleId, View.OnClickListener listener) {
        mButtonTitle = resources.getString(titleId);
        this.mButtonListener = listener;
    }

    public View.OnClickListener getOnClickListener() {
        return mButtonListener;
    }

    public String getButtonTitle() {
        return mButtonTitle;
    }

    @Override
    public ViewType getItemViewType() {
        return ViewType.PROGRESS;
    }
}
