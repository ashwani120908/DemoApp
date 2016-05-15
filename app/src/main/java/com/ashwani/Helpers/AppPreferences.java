package com.ashwani.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ashwani on 19/2/16.
 */
public class AppPreferences {

    public static final String APP_PREFERENCES = "app_pref";
    public static final String USER_UNIQUE_NAME = "user_name";
    public static final String USER_PASSWORD = "user_password";
    public static final String USER_CHECK_PASSWORD = "user_check_password";
    public static final String USER_NAME = "user_first_name";

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    Context context;

    public AppPreferences(Context context) {
        this.context = context;
        sharedpreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
    }

    public void clearAllPreferences() {
        editor.clear();
        editor.commit();
    }

    public void setUserUniqueNamePref(String userName) {
        editor.putString(USER_UNIQUE_NAME, userName);
        editor.commit();
    }

    public String getUserUniqueNamePref() {
        return sharedpreferences.getString(USER_UNIQUE_NAME, "");
    }

    public void setUserPasswordPref(String userPassword) {
        editor.putString(USER_PASSWORD, userPassword);
        editor.commit();
    }

    public String getUserPasswordPref() {
        return sharedpreferences.getString(USER_PASSWORD, "");
    }

    public String getUserCheckPasswordPref() {
        return sharedpreferences.getString(USER_CHECK_PASSWORD, "");
    }

    public void setUserCheckPasswordPref(String userCheckPassword) {
        editor.putString(USER_CHECK_PASSWORD, userCheckPassword);
        editor.commit();
    }

    public void setUserNamePref(String userFirstName) {
        editor.putString(USER_NAME, userFirstName);
        editor.commit();
    }

    public String getUserNamePref() {
        return sharedpreferences.getString(USER_NAME, "");
    }
}
