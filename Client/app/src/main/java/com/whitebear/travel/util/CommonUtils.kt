package com.whitebear.travel.util

import com.google.gson.Gson
import java.lang.reflect.Type

object CommonUtils {
    inline fun <reified T> parseDto(data: Any, typeToken: Type): T {
        val jsonResult: String = Gson().toJson(data)
        return Gson().fromJson<T>(jsonResult, typeToken)
    }
}