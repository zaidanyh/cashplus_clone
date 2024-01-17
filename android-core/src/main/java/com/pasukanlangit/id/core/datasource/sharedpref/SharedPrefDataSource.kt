package com.pasukanlangit.id.core.datasource.sharedpref

import android.content.SharedPreferences

class SharedPrefDataSource(private val sharedPreferences: SharedPreferences) {
    companion object {
        const val KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN"
        const val KEY_UUID = "KEY_UUID"
        const val KEY_NAME_USER = "KEY_NAME_USER"
        const val KEY_EMAIL_USER = "KEY_EMAIL_USER"
        const val KEY_PHONE_NUMBER = "KEY_PHONE_NUMBER"
        const val KEY_BOARDING_HAS_BEEN_SHOW = "KEY_BOARDING_HAS_BEEN_SHOW"
    }

    fun setAccessToken(value: String?) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, value).apply()
    }
    fun getAccessToken(): String? = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)

    fun setUUID(value: String) {
        sharedPreferences.edit().putString(KEY_UUID, value).apply()
    }
    fun getUUID(): String? = sharedPreferences.getString(KEY_UUID, null)

    fun deleteAuth() {
        sharedPreferences.edit()
            .remove(KEY_UUID)
            .remove(KEY_ACCESS_TOKEN)
            .apply()
    }

    fun setNameProfile(value: String) {
        sharedPreferences.edit().putString(KEY_NAME_USER, value).apply()
    }
    fun getNameProfile(): String? = sharedPreferences.getString(KEY_NAME_USER, null)

    fun setPhoneNumber(value: String) {
        sharedPreferences.edit().putString(KEY_PHONE_NUMBER, value).apply()
    }
    fun getPhoneNumber(): String? = sharedPreferences.getString(KEY_PHONE_NUMBER, null)

    fun setEmailProfile(value: String) {
        sharedPreferences.edit().putString(KEY_EMAIL_USER, value).apply()
    }
    fun getEmailProfile(): String? = sharedPreferences.getString(KEY_EMAIL_USER, null)

    fun deleteProfile() {
        with(sharedPreferences.edit()) {
            remove(KEY_NAME_USER)
            remove(KEY_PHONE_NUMBER)
            remove(KEY_EMAIL_USER)
            apply()
        }
    }

    fun setBoardingHasBeenShow(value: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_BOARDING_HAS_BEEN_SHOW, value).apply()
    }
    fun getBoardingHasBeenShow(): Boolean = sharedPreferences.getBoolean(KEY_BOARDING_HAS_BEEN_SHOW, false)
}