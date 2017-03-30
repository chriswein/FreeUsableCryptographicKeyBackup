package com.keybackup.fullscreenfragments.contactchooser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.android.secret.sharing.Contact;
import com.android.secret.sharing.KeyPart;

interface ContactChooserContract {

    interface View {

        void setDescription (String description);

        Fragment getFragment();
    }

    interface UserActionListener {

        void setAdapter(ListView listView);

        void requestContactsPermission();

        void itemClicked(Fragment fragment, int position, int requestCode);

        void secretSent(Contact contact, boolean previousSendStatusSelected);

        void closeKeyboard();

        void saveState(Bundle bundle);

        void restoreState(Bundle bundle);

        void receivedKeyPart(KeyPart keyPart);
    }
}
