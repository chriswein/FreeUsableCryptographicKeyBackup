package com.keybackup.fullscreenfragments.contactchooser.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import com.keybackup.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendDialogAdapter extends BaseAdapter {
    private SendDialog.ITEMS[] mItems;
    private Context mContext;
    private LayoutInflater mInflater;

    private int mSelected;
    private String mEmail;

    public SendDialogAdapter(Context context, SendDialog.ITEMS[] items) {
        mItems = items;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public Object getItem(int position) {
        return mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return mItems[position].hashCode();
    }

    public int getSelectedItem() {
        return mSelected;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getEmail() {
        return mEmail;
    }

    public void focusEmailEdit(ListView listView, int position) {
        View view = listView.getChildAt(position);
        ViewHolder holder = (ViewHolder) view.getTag();

        showKeyboard(holder);
    }

    private void showKeyboard(ViewHolder holder) {
        if (holder.emailEdit.getVisibility() == View.VISIBLE) {
            holder.emailEdit.requestFocus();
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(holder.emailEdit, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SendDialog.ITEMS item = mItems[position];
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dialog_send_method_list_item, parent, false);

            holder = new ViewHolder();
            ButterKnife.bind(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.radioButton.setChecked(mSelected == position);
        holder.radioButton.setText(item.toString(mContext));
        holder.radioButton.setTag(position);
        holder.radioButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSelected = (Integer) v.getTag();
                notifyDataSetChanged();
            }
        });

        boolean emailEditVisible = mSelected == position && (item == SendDialog.ITEMS.SHOW_EMAIL || item == SendDialog.ITEMS.CHANGE_TO_EMAIL);
        holder.emailEdit.setVisibility(emailEditVisible ? View.VISIBLE : View.GONE);
        holder.emailEdit.setText(mEmail);

        holder.emailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmail = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        showKeyboard(holder);

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.dialog_send_method_radio_button)
        RadioButton radioButton;
        @BindView(R.id.dialog_send_method_email)
        EditText emailEdit;
    }
}