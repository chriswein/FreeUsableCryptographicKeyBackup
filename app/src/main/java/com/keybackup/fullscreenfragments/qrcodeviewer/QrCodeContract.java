package com.keybackup.fullscreenfragments.qrcodeviewer;

import android.widget.ImageView;
import android.widget.TextView;

interface QrCodeContract {

    interface View {

        void setDescription(String description);

        void hideTransferLaterButton();

        void hideTransferButton();

    }

    interface UserActionListener {

        void init(ImageView view, TextView qrText);

        void secretTransferred();

        void transferLater();

    }

}
