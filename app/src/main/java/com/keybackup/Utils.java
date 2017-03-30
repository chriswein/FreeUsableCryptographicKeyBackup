package com.keybackup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Button;

import java.text.DateFormat;
import java.util.Date;

public class Utils {

    public static void startActivity(Activity currentActivity, Class startActivity, boolean finishCurrent) {
        startActivity(currentActivity, startActivity, finishCurrent, null, 0);
    }

    public static void startActivity(Activity currentActivity, Class startActivity, boolean finishCurrent, Bundle extras, int requestCode) {
        Intent intent = new Intent(currentActivity, startActivity);

        if (extras != null) {
            intent.putExtras(extras);
        }

        if (requestCode > 0) {
            currentActivity.startActivityForResult(intent, requestCode);
        } else {
            currentActivity.startActivity(intent);
        }

        if (finishCurrent) {
            currentActivity.finish();
        }
    }

    public static void startActivity(Fragment currentFragment, Class startActivity, Bundle extras, int requestCode) {
        Intent intent = new Intent(currentFragment.getContext(), startActivity);

        if (extras != null) {
            intent.putExtras(extras);
        }

        if (requestCode > 0) {
            currentFragment.startActivityForResult(intent, requestCode);
        } else {
            currentFragment.startActivity(intent);
        }
    }

    public static String getCreationDate(Context context, long timestamp) {
        Date date = new Date(timestamp);
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);

        return dateFormat.format(date);
    }

    public static void openFullscreenDialog(BaseActivity activity, DialogFragment dialog, String tag) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(android.R.id.content, dialog, tag)
                .addToBackStack(null)
                .commit();
    }
}
