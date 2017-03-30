package com.keybackup.fullscreenfragments.contactchooser.contactadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcel;
import android.support.v4.widget.Space;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.secret.sharing.Contact;
import com.keybackup.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemContact implements Item {
    private Contact mContact;
    private boolean mSendKey;

    public ItemContact(Contact contact, boolean sendKey) {
        mContact = contact;
        mSendKey = sendKey;
    }

    @Override
    public int getViewType() {
        return ContactsAdapter.RowType.CONTACT_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup parent, Context context, ContactsAdapter adapter) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_contact_item, parent, false);

            holder = new ViewHolder();
            ButterKnife.bind(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(mContact.getName());

        Bitmap photo = mContact.getPhoto(context);
        if (photo != null) {
            holder.image.setImageBitmap(photo);
        } else {
            holder.image.setImageResource(R.drawable.ic_contact_circle_black_24dp_vector);
        }

        Contact.SendStatus status = mContact.getSendStatus();

        holder.sendMethod.setColorFilter(Color.GRAY);

        if (status == Contact.SendStatus.CONFIRMED) {
            holder.sendMethod.setImageResource(R.drawable.ic_done_all_black_24dp);
        } else {
            holder.sendMethod.setImageResource(R.drawable.ic_done_black_24dp_vector);

            if (mSendKey) {
                holder.sendMethod.setColorFilter(null);
            }
        }

        if (mSendKey) {
            holder.sendMethod.setVisibility(status == Contact.SendStatus.NONE ? View.INVISIBLE : View.VISIBLE);
        } else {
            holder.sendMethod.setVisibility(status == Contact.SendStatus.RECEIVED ? View.VISIBLE : View.INVISIBLE);
        }

        boolean isEnabled = isEnabled();

        if (isEnabled) {
            holder.image.setColorFilter(null);
        } else {
            holder.image.setColorFilter(Color.GRAY);
        }

        holder.name.setEnabled(isEnabled);

        return convertView;
    }

    @Override
    public boolean isEnabled() {
        if (mSendKey) {
            return mContact.getSendStatus() != Contact.SendStatus.CONFIRMED;
        } else {
            return mContact.getSendStatus() != Contact.SendStatus.RECEIVED;
        }
    }

    public String getName() {
        return mContact.getName();
    }

    public int getSendMethodId() {
        return mContact.getSendMethod().getId();
    }

    public boolean notChosen() {
        return mContact.getSendMethod() == com.android.secret.sharing.Contact.SendMethod.NONE;
    }

    public boolean otherSendMethod(ItemContact itemContact) {
        return mContact.getSendMethod().getId() != itemContact.getSendMethodId();
    }

    public Contact.SendMethod getSend() {
        return mContact.getSendMethod();
    }

    public Contact getContact() {
        return mContact;
    }

    public void setContact(Contact contact) {
        mContact = contact;
    }

    public static class ViewHolder {
        @BindView(R.id.contact_item_image)
        ImageView image;
        @BindView(R.id.contact_item_name)
        TextView name;
        @BindView(R.id.contact_item_email_address)
        EditText emailAddress;
        @BindView(R.id.contact_item_space)
        Space space;
        @BindView(R.id.contact_item_send_method)
        ImageView sendMethod;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mContact, flags);
    }

    protected ItemContact(Parcel in) {
        this.mContact = in.readParcelable(Contact.class.getClassLoader());
    }

    public static final Creator<ItemContact> CREATOR = new Creator<ItemContact>() {
        @Override
        public ItemContact createFromParcel(Parcel source) {
            return new ItemContact(source);
        }

        @Override
        public ItemContact[] newArray(int size) {
            return new ItemContact[size];
        }
    };
}
