package com.keybackup.main.todo.adapter.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.android.secret.sharing.Contact;
import com.keybackup.R;
import com.keybackup.main.todo.adapter.ViewType;

public class TodoContactItem implements TodoItem {
    private Contact mContact;
    private String mHeadline;
    private String subtitle;
    private String showButtonTitle;
    private String moreButtonTitle;
    private View.OnClickListener mPositiveButtonListener;
    private View.OnClickListener mShowButtonListener;
    private View.OnClickListener mMoreButtonListener;

    public TodoContactItem(Context context, Contact contact, boolean isKeyPart) {
        mContact = contact;

        if (isKeyPart) {
            mHeadline = mContact.getName();
        } else {
            mHeadline = String.format(context.getString(R.string.card_contact_headline_backup), mContact.getName());
        }

        moreButtonTitle = context.getString(R.string.card_contact_more);

        switch (contact.getSendMethod()) {
            case QR:
                subtitle = String.format(context.getString(R.string.card_contact_subtitle_qr), mContact.getName());
                showButtonTitle = context.getString(R.string.card_contact_show_qr_code);
                break;
            case PRINT:
                subtitle = String.format(context.getString(R.string.card_contact_subtitle_print), mContact.getName());
                showButtonTitle = context.getString(R.string.print_qr_code);
                break;
            case EMAIL:
                subtitle = String.format(context.getString(R.string.card_contact_subtitle_email), mContact.getName());
                showButtonTitle = context.getString(R.string.dialog_change_send_method_show_email);
                break;
            default:
                throw new IllegalStateException("No send method assigned to contact " + contact.getName());
        }
    }

    @Override
    public ViewType getItemViewType() {
        return ViewType.CONTACT;
    }

    public void setPositiveButtonListener(View.OnClickListener listener) {
        mPositiveButtonListener = listener;
    }

    public void setShowButtonListener(View.OnClickListener listener) {
        mShowButtonListener = listener;
    }

    public void setMoreButtonListener(View.OnClickListener listener) {
        mMoreButtonListener = listener;
    }

    public View.OnClickListener getShowButtonListener() {
        return mShowButtonListener;
    }

    public View.OnClickListener getMoreButtonListener() {
        return mMoreButtonListener;
    }

    public View.OnClickListener getPositiveButtonListener() {
        return mPositiveButtonListener;
    }

    public void setContactImage(Context context, ImageView imageView) {
        Bitmap image = mContact.getPhoto(context);

        if (image != null) {
            imageView.setImageBitmap(image);
        } else {
            imageView.setImageResource(R.drawable.ic_contact_circle_black_24dp_vector);
        }
    }

    public String getHeadline() {
        return mHeadline;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getShowButtonTitle() {
        return showButtonTitle;
    }

    public String getMoreButtonTitle() {
        return moreButtonTitle;
    }
}
