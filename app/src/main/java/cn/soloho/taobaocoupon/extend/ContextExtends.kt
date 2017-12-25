package cn.soloho.oraclebb.extend

import android.app.Activity
import android.content.Context
import android.support.annotation.StringRes
import com.blankj.utilcode.util.ToastUtils

/**
 * Created by kjsolo on 2017/8/22.
 */
fun Context.showToast(@StringRes message: Int) {
    if (this is Activity) {
        ToastUtils.showShort(message)
    }
}

fun Context.showToast(message: String, vararg args: Any) {
    if (this is Activity) {
        ToastUtils.showShort(message, args)
    }
}