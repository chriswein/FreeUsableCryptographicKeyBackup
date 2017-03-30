package com.keybackup.fullscreenfragments.contactchooser.contactadapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.secret.sharing.Contact;
import com.keybackup.R;
import com.keybackup.fullscreenfragments.contactchooser.ContactChooserPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ContactsAdapter extends BaseAdapter {
    private static final String ARG_CONTACTS = "contacts";

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Item> mItems = new ArrayList<>();
    private int mSize;
    private boolean mShowSendMethodHeader;
    private ContactChooserPresenter mPresenter;
    private boolean mSendKeyParts;

    public enum RowType {
        CONTACT_ITEM, HEADER_ITEM, ADD_ITEM
    }

    public ContactsAdapter(Context context, ContactChooserPresenter presenter) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mPresenter = presenter;
    }

    public int addContact(@NonNull ItemContact contact) {
        int position = -1;

        // leave out first item (because it is a header)
        for (int i = 1; i < mSize; i++) {
            Item item = mItems.get(i);
            if (item instanceof Contact && ((Contact) item).getName().compareTo(contact.getName()) > 0) {
                position = i;
                break;
            }
        }

        // contact not inserted yet => place it before last header ("add new contact")
        if (position < 0) {
            position = mSize -1;
        }

        mItems.add(position, contact);
        mSize++;
        notifyDataSetChanged();

        return position;
    }

    public void click(int position, int requestCode) {
        mPresenter.closeKeyboard();
        mPresenter.itemClicked(null, position, requestCode);
    }

    public void showSendMethodHeader() {
        if (mShowSendMethodHeader) {
            return;
        }

        mShowSendMethodHeader = true;

        Collections.sort(mItems, new Comparator<Item>() {
            @Override
            public int compare(Item lhs, Item rhs) {
                if (lhs instanceof ItemHeader || lhs instanceof ItemAddContact) {
                    return -1;
                } else if (rhs instanceof ItemHeader || rhs instanceof ItemAddContact) {
                    return 1;
                }

                ItemContact left = (ItemContact) lhs;
                ItemContact right = (ItemContact) rhs;

                int group = left.getSendMethodId() - right.getSendMethodId();

                if (group != 0) {
                    return group;
                }

                return left.getName().compareTo(right.getName());
            }
        });

        // remove contacts without key part
        while (mSize > 0) {
            Item item = mItems.get(0);
            if (item instanceof ItemHeader || item instanceof ItemAddContact || ((ItemContact) item).notChosen()) {
                mItems.remove(0);
                mSize--;
            } else {
                break;
            }
        }

        ItemHeader firstItemHeader = new ItemHeader(mContext, (ItemContact) mItems.get(0), mSendKeyParts);
        mItems.add(0, firstItemHeader);
        mSize++;

        // add header items
        for (int i = 2; i < mSize; i++) {
            ItemContact contactBefore = ((ItemContact) mItems.get(i - 1));
            ItemContact contact = ((ItemContact) mItems.get(i));

            if (contactBefore.otherSendMethod(contact)) {
                ItemHeader itemHeader = new ItemHeader(mContext, contact, mSendKeyParts);
                mItems.add(i, itemHeader);
                i++;
                mSize++;
            }
        }

        notifyDataSetChanged();
    }

    public void setContact(Contact contact) {
        ((ItemContact) mItems.get(contact.getListPosition())).setContact(contact);
        notifyDataSetChanged();
    }

    public void setContacts(@NonNull ArrayList<Contact> contacts, boolean sendKeyParts) {
        mSendKeyParts = sendKeyParts;

        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

        mItems.clear();

        for (Contact contact : contacts) {
            mItems.add(new ItemContact(contact, sendKeyParts));
        }

        if (mItems.size() > 0) {
            mItems.add(0, new ItemHeader(mContext.getString(R.string.contact_list_header_contacts)));
        }

        mItems.add(new ItemAddContact());

        mSize = mItems.size();

        notifyDataSetChanged();
    }

    public void setContact(int position, Contact contact) {
        Item contactItem = new ItemContact(contact, mSendKeyParts);
        mItems.set(position, contactItem);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public Contact getItem(int position) {
        return ((ItemContact) mItems.get(position)).getContact();
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mItems.get(position).getView(mInflater, convertView, parent, mContext, this);
    }

    @Override
    public int getItemViewType (int position) {
        return mItems.get(position).getViewType();
    }

    @Override
    public int getViewTypeCount () {
        return RowType.values().length;
    }

    @Override
    public boolean isEnabled(int position) {
        return mItems.get(position).isEnabled();
    }

    public void saveState(Bundle outState) {
        outState.putParcelableArrayList(ARG_CONTACTS, mItems);
    }

    public void restoreState(Bundle savedState) {
        mItems = savedState.getParcelableArrayList(ARG_CONTACTS);

        if (mItems == null) {
            mItems = new ArrayList<>();
        }
        mSize = mItems.size();

        notifyDataSetChanged();
    }
}
