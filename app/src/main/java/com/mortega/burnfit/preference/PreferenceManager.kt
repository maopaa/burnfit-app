package com.mortega.burnfit.preference

import android.content.Context
import android.content.SharedPreferences
import com.mortega.burnfit.R

class PreferenceManager (private val context: Context) {

    private var sharedPreferences: SharedPreferences? = null

    init { getSharedPreferences() }

    private fun getSharedPreferences() {

        sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.my_preference),
            Context.MODE_PRIVATE
        )
    }

    fun writePreference() {
        val editor = sharedPreferences!!.edit()
        editor.putString(context.getString(R.string.my_preference_key), "INIT_OK")
        editor.apply()
    }

    fun checkPreference(): Boolean {
        var status = false

        status = sharedPreferences!!.getString(
            context.getString(R.string.my_preference_key), "null"
        ) != "null"

        return status
    }

    fun clearPreference() {
        sharedPreferences!!.edit().clear().apply()
    }
}