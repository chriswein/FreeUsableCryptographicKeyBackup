package com.keybackup.main.todo.adapter.item;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;

import com.keybackup.main.todo.adapter.ViewType;

public class TodoTextItem implements TodoItem {
    private String mHeadline;
    private int mHeadlineColor = Color.BLACK;
    private String mDescription;
    private boolean mIsRemovable;
    private String mButtonTitle;
    private String mSecondButtonTitle;
    private View.OnClickListener mButtonListener;
    private View.OnClickListener mSecondButtonListener;
    private View.OnClickListener mRemoveListener;

    public TodoTextItem() {

    }

    public TodoTextItem (Resources resources, int headlineId, int descriptionId) {
        this.mHeadline = resources.getString(headlineId);
        this.mDescription = resources.getString(descriptionId);
    }

    public void setHeadlineColor(int color) {
        mHeadlineColor = color;
    }

    public int getHeadlineColor() {
        return mHeadlineColor;
    }

    public String getHeadline() {
        return mHeadline;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean isRemovable() {
        return mIsRemovable;
    }

    public boolean hasButton() {
        return mButtonTitle != null;
    }

    public boolean hasSecondButton() {
        return mButtonTitle != null;
    }

    public void setHeadline(String headline) {
        this.mHeadline = headline;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    /**
     * Card will be marked with a cross in the upper right corner so that it can be removed.
     *
     * @param listener  Listener will be invoked when user removes the card.
     */
    public void setRemovable(View.OnClickListener listener) {
        mIsRemovable = true;
        mRemoveListener = listener;
    }

    public void setButton(Resources resources, int titleId, View.OnClickListener listener) {
        mButtonTitle = resources.getString(titleId);
        this.mButtonListener = listener;
    }

    public void setSecondButton(Resources resources, int titleId, View.OnClickListener listener) {
        mSecondButtonTitle = resources.getString(titleId);
        this.mSecondButtonListener = listener;
    }

    public View.OnClickListener getOnClickListener() {
        return mButtonListener;
    }

    public View.OnClickListener getSecondOnClickListener() {
        return mSecondButtonListener;
    }

    public View.OnClickListener getRemoveListener() {
        return mRemoveListener;
    }

    public String getButtonTitle() {
        return mButtonTitle;
    }

    public String getSecondButtonTitle() {
        return mSecondButtonTitle;
    }

    @Override
    public ViewType getItemViewType() {
        return ViewType.TEXT;
    }
}
