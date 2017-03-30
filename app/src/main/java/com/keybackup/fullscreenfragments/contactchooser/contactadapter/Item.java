package com.keybackup.fullscreenfragments.contactchooser.contactadapter;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface Item extends Parcelable {

    int getViewType();

    View getView(LayoutInflater inflater, View convertView, ViewGroup parent, Context context, ContactsAdapter adapter);

    boolean isEnabled();
}
