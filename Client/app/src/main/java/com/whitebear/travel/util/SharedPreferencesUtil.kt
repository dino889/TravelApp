package com.whitebear.travel.util

import android.content.Context
import android.content.SharedPreferences
import com.whitebear.travel.src.dto.User

class SharedPreferencesUtil (context: Context) {
    val SHARED_PREFERENCES_NAME = "whitebear_preference"
    val COOKIES_KEY_NAME = "cookies"

    var preferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    //사용자 정보 저장
    fun addUser(user: User) {
        val editor = preferences.edit()
        editor.putInt("id", user.id)
        editor.putString("token", user.token)
        editor.apply()
    }

    fun getUser(): User {
        val id = preferences.getInt("id", 0)
        if (id != 0) {
            return User(id)
        } else {
            return User()
        }
    }


    fun deleteUser(){
        //preference 지우기
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    fun addUserCookie(cookies: HashSet<String>) {
        val editor = preferences.edit()
        editor.putStringSet(COOKIES_KEY_NAME, cookies)
        editor.apply()
    }

    fun getUserCookie(): MutableSet<String>? {
        return preferences.getStringSet(COOKIES_KEY_NAME, HashSet())
    }

    fun getString(key:String): String? {
        return preferences.getString(key, null)
    }

    fun deleteUserCookie() {
        preferences.edit().remove(COOKIES_KEY_NAME).apply()
    }

    fun setAutoLogin(userId: Int) {
        val editor = preferences.edit()
        editor.putInt("auto", userId)
        editor.apply()
    }

    fun getAutoLogin() : Int {
        return preferences.getInt("auto", 0)
    }

    fun deleteAutoLogin() {
        preferences.edit().remove("auto").apply()
    }
}