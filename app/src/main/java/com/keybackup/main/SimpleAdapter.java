package com.keybackup.main;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.keybackup.BaseActivity;
import com.keybackup.R;
import com.keybackup.Utils;
import com.android.secret.sharing.SecretPresentation;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleAdapter extends BaseAdapter {
    private BaseActivity mActivity;
    private LayoutInflater mInflater;
    private SecretPresentation[] mItems;

    public SimpleAdapter(BaseActivity activity) {
        this.mActivity = activity;
        mInflater = LayoutInflater.from(activity);
    }

    public void setItems(@NonNull SecretPresentation[] items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public void addItem(SecretPresentation item) {
        SecretPresentation[] newItems = new SecretPresentation[getCount() + 1];

        if (mItems != null) {
            System.arraycopy(mItems, 0, newItems, 0, mItems.length);
        }

        newItems[newItems.length - 1] = item;
        setItems(newItems);
    }

    @Override
    public int getCount() {
        return mItems != null ? mItems.length : 0;
    }

    @Override
    public SecretPresentation getItem(int position) {
        return mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return mItems[position].hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        SecretPresentation secret = mItems[position];

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_simple_item, parent, false);

            holder = new ViewHolder();
            ButterKnife.bind(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(secret.getName());
        holder.date.setText(Utils.getCreationDate(mActivity, secret.getTimestamp()));

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.overview_item_name)
        public TextView name;
        @BindView(R.id.overview_item_date)
        public TextView date;
    }
}
