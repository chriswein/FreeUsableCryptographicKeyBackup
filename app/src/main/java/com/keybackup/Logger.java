package com.keybackup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2793;
    private static final String FILE = Environment.getExternalStorageDirectory() + "/backups_log_";
    private static File mLogFile;
    private static long mStartTime;
    private static long mLastTime;
    private static String mTemp = "";

    public static boolean init(Activity activity) {
        if (mStartTime == 0) {
            mStartTime = mLastTime = System.currentTimeMillis();
        }

        return initLogger(activity);
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initLogger(activity);
        }
    }

    private static boolean initLogger(Activity activity) {
        int storagePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
            return false;
        }

        mLogFile = new File(FILE + mStartTime + ".txt");

        boolean fileCreated = false;
        try {
            fileCreated = mLogFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(mTemp)) {
            printText(mTemp);
            mTemp = "";
        }

        return fileCreated;
    }

    public static void finish() {
        if (mLogFile != null) {
            long minutes = (System.currentTimeMillis() - mStartTime) / 1000 / 60;

            printText("\nSession length: " + minutes + " minutes\n\n");
        }
    }

    /**
     * Onboarding started or user opened help.
     *
     * @param pageView Page of main menu (0 - 2) or -1 if it´s the first start of the app.
     */
    public static void startOnboarding(int pageView) {
        if (pageView < 0) {
            print("Onboarding started", false);
        } else {
            print("Help opened from page view " + pageView, false);
        }
    }

    public static void finishOnboarding() {
        print("Onboarding/Help finished", true);
    }

    /**
     * Key of box will be split.
     *
     * @param entryPoint Which button did the user select to open this view?
     */
    public static void startSplitKey(String entryPoint) {
        print("Opened key setting from " + entryPoint, false);
    }

    /**
     * Split key dialog finished.
     *
     * @param minParts       Selected minimum parts.
     * @param totalParts     Selected total parts.
     * @param userName       Name of user.
     * @param chooseContacts True if users selects "Choose Contacts" Button at the end of the dialog.
     */
    public static void finishSplitKey(Context context, int minParts, int totalParts, String userName, boolean chooseContacts) {
        int defMinimumParts = Integer.valueOf(context.getResources().getString(R.string.step_minimum_parts));
        int defTotalParts = Integer.valueOf(context.getResources().getString(R.string.step_total_parts));

        String message = "";

        if (minParts != defMinimumParts) {
            message += "User set minimum parts to " + minParts + '\n';
        }

        if (totalParts != defTotalParts) {
            message += "User set total parts to " + totalParts + '\n';
        }

        if (!TextUtils.isEmpty(userName)) {
            message += "Changed name to " + userName + '\n';
        }

        if (chooseContacts) {
            message += "Exit key setting dialog. Chooses contacts now";
        } else {
            message += "Exit key setting dialog. Chooses contacts later";
        }

        print(message, true);
    }

    /**
     * User starts "Choose Contacts" dialog.
     *
     * @param entryPoint Can be a card or split key dialog.
     */
    public static void opensChooseContactsDialog(String entryPoint) {
        print("Opens 'Choose Contacts' dialog from " + entryPoint, false);
    }

    public static void exitsChooseContactsDialog() {
        print("Exits 'Choose Contacts' dialog", true);
    }

    public static void newBackup() {
        print("Create new backup", false);
    }

    /**
     * Backup is created.
     *
     * @param name  Name of backup.
     * @param cloud True if user chose to save backup in cloud storage.
     */
    public static void backupCreated(String name, boolean cloud) {
        print("Backup created with name \"" + name + "\", " + (cloud ? "saved in cloud" : "printed"), true);
    }

    /**
     * User exported backup from card.
     *
     * @param toCloud Backup is saved to cloud storage.
     */
    public static void exportBackupFromCard(boolean toCloud) {
        print("Backup exported from card, " + (toCloud ? "saved in cloud" : "printed"),  true);
    }

    public static void startedRestoringBackup() {
        print("Started restoring backup", false);
    }

    /**
     * Backup is restored.
     *
     * @param qrShown True if user wants to show the backup as a QR Code.
     */
    public static void finishedRestoringBackup(boolean qrShown) {
        if (qrShown) {
            print("Finished restoring backup, shown as QR Code", true);
        } else {
            print("Finished restoring backup, shown as text", true);
        }
    }

    private static void print(String title, boolean addEmptyLine) {
        long seconds = (System.currentTimeMillis() - mLastTime) / 1000;
        String message = title + " (" + seconds + " seconds)" + '\n';

        if (addEmptyLine) {
            message += '\n';
        }

        mLastTime = System.currentTimeMillis();

        if (mLogFile == null) {
            mTemp += message;
            return;
        }

        printText(message);
    }

    private static void printText(String text) {
        try {
            new PrintWriter(new FileOutputStream(mLogFile, true))
                    .append(text)
                    .close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
