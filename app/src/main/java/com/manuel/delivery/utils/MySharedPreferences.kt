package com.manuel.delivery.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson

class MySharedPreferences(context: Context) {
    private var sharedPreferences: SharedPreferences? = null

    init {
        sharedPreferences =
            context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    fun getData(key: String): String? = sharedPreferences?.getString(key, "")
    fun saveData(key: String, value: Any) {
        try {
            val json = Gson().toJson(value)
            sharedPreferences?.edit {
                putString(key, json)
                apply()
            }
        } catch (e: Exception) {
        }
    }

    fun removeData(key: String) {
        sharedPreferences?.edit()?.remove(key)?.apply()
    }
}