package com.keybackup.fullscreenfragments.contactchooser.contactadapter;

import android.content.Context;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keybackup.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemHeader implements Item {
    private String mTitle;

    public ItemHeader(Context context, ItemContact itemContact, boolean sendKeyParts) {
        switch (itemContact.getSend()) {
            case NONE:
                mTitle = context.getString(R.string.contact_list_header_none);
                break;
            case QR:
                mTitle = context.getString(sendKeyParts ? R.string.dialog_change_send_method_show_qr : R.string.contact_list_header_receive_qr);
                break;
            case PRINT:
                mTitle = context.getString(sendKeyParts ? R.string.dialog_change_send_method_show_print : R.string.contact_list_header_receive_print);
                break;
            case EMAIL:
                mTitle = context.getString(sendKeyParts ? R.string.dialog_change_send_method_show_email : R.string.contact_list_header_receive_email);
                break;
        }
    }

    public ItemHeader(String title) {
        mTitle = title;
    }

    @Override
    public int getViewType() {
        return ContactsAdapter.RowType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup parent, Context context, ContactsAdapter adapter) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_contact_header, parent, false);

            holder = new ViewHolder();
            ButterKnife.bind(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mHeaderText.setText(mTitle);

        return convertView;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public static class ViewHolder {
        @BindView(R.id.contact_item_header)
        TextView mHeaderText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
    }

    protected ItemHeader(Parcel in) {
        this.mTitle = in.readString();
    }

    public static final Creator<ItemHeader> CREATOR = new Creator<ItemHeader>() {
        @Override
        public ItemHeader createFromParcel(Parcel source) {
            return new ItemHeader(source);
        }

        @Override
        public ItemHeader[] newArray(int size) {
            return new ItemHeader[size];
        }
    };
}
