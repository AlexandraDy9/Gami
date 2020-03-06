package com.university.gami_android.preferences

import android.content.Context
import android.content.SharedPreferences
import com.university.gami_android.GamiApplication
import com.university.gami_android.preferences.PrefKeys

class PreferenceHandler private constructor() {
    companion object {

        val preferences: SharedPreferences
            get() = GamiApplication.instance
                .getSharedPreferences(PrefKeys.PREFS_NAME_TAG, Context.MODE_PRIVATE)

        fun setUserEmail(userEmail: String) {
            preferences.edit().putString(PrefKeys.PREF_EMAIL, userEmail).apply()
        }

        fun getUserName() : String{
            return preferences.getString(PrefKeys.PREF_USERNAME, "") ?: ""
        }

        fun setUserName(userName: String) {
            preferences.edit().putString(PrefKeys.PREF_USERNAME, userName).apply()
        }

        fun setUserFirstName(userFirstName: String) {
            preferences.edit().putString(PrefKeys.PREF_FIRSTNAME, userFirstName).apply()
        }

        fun setUserLastName(userLastName: String) {
            preferences.edit().putString(PrefKeys.PREF_LASTNAME, userLastName).apply()
        }

        fun setAuthorization(authorization: String) {
            preferences.edit().putString(PrefKeys.PREF_AUTHORIZATION, authorization).apply()
        }

        fun getAuthorization(): String {
            return preferences.getString(PrefKeys.PREF_AUTHORIZATION, "") ?: ""
        }
    }
}