package cn.soloho.oraclebb.extend

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by kjsolo on 2017/6/26.
 */

val Any.LOG_TAG: String
    get() {
        val jClazz = this::class.java
        if (jClazz.enclosingClass != null) {
            return jClazz.enclosingClass.simpleName
        } else {
            return jClazz.simpleName
        }
    }

inline fun <reified T> genericType() = object: TypeToken<T>() {}.type

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)

inline fun <reified T : Any> Any.cast(): T = this as T