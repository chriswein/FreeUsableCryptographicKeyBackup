package com.keybackup;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String PREFERENCES = "key_backup_prefs";
    private static final String PREF_ONBOARDING_FINISHED = "onboarding_finished";
    private static final String PREF_CARD_HELP_BACKUP = "card_help_backup";
    private static final String PREF_CONTAINER_CREATED = "container_created";

    private static Preferences mPreferences;
    private SharedPreferences mSharedPreferences;

    public static Preferences getPreferences(Context context) {
        if (mPreferences == null) {
            mPreferences = new Preferences(context);
        }
        return mPreferences;
    }

    private Preferences (Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public void onboardingFinished() {
        setBoolean(PREF_ONBOARDING_FINISHED, true);
    }

    public boolean isOnboardingFinished() {
        return mSharedPreferences.getBoolean(PREF_ONBOARDING_FINISHED, false);
    }

    public void cardHelpBackupRemoved() {
        setBoolean(PREF_CARD_HELP_BACKUP, true);
    }

    public boolean isCardHelpBackupRemoved() {
        return mSharedPreferences.getBoolean(PREF_CARD_HELP_BACKUP, false);
    }

    public void containerCreated() {
        setBoolean(PREF_CONTAINER_CREATED, true);
    }

    public boolean isContainerCreated() {
        return mSharedPreferences.getBoolean(PREF_CONTAINER_CREATED, false);
    }

    private void setBoolean(String pref, boolean value) {
        mSharedPreferences.edit()
                .putBoolean(pref, value)
                .apply();
    }
}
