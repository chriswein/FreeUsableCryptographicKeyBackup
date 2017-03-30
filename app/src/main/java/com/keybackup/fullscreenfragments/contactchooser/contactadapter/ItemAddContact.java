package com.keybackup.fullscreenfragments.contactchooser.contactadapter;

import android.content.Context;
import android.os.Parcel;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.secret.sharing.AndroidSecretSharing;
import com.android.secret.sharing.Contact;
import com.keybackup.R;
import com.keybackup.fullscreenfragments.contactchooser.ContactChooserFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemAddContact implements Item {

    @Override
    public int getViewType() {
        return ContactsAdapter.RowType.ADD_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup parent, final Context context, final ContactsAdapter adapter) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_contact_add, parent, false);

            holder = new ViewHolder();
            ButterKnife.bind(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = holder.name.getText().toString();

                if (!TextUtils.isEmpty(name)) {
                    Contact androidContact = AndroidSecretSharing.newContact();
                    androidContact.setName(name);

                    ItemContact itemContact = new ItemContact(androidContact, true);
                    int position = adapter.addContact(itemContact);
                    adapter.click(position, ContactChooserFragment.REQUEST_KEY_SENT);

                    holder.name.setText("");
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public static class ViewHolder {
        @BindView(R.id.contact_chooser_add_name)
        EditText name;
        @BindView(R.id.contact_chooser_add)
        ImageButton add;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public ItemAddContact() {
    }

    protected ItemAddContact(Parcel in) {
    }

    public static final Creator<ItemAddContact> CREATOR = new Creator<ItemAddContact>() {
        @Override
        public ItemAddContact createFromParcel(Parcel source) {
            return new ItemAddContact(source);
        }

        @Override
        public ItemAddContact[] newArray(int size) {
            return new ItemAddContact[size];
        }
    };
}
