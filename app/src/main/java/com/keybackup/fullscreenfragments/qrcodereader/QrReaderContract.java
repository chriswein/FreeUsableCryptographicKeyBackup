package com.keybackup.fullscreenfragments.qrcodereader;

import android.os.Bundle;
import android.view.SurfaceView;

interface QrReaderContract {

    interface View {

        void showMessage(String message);

    }

    interface UserActionListener {

        void showCamera(SurfaceView surfaceView);

        void saveState(Bundle bundle);

    }

}
