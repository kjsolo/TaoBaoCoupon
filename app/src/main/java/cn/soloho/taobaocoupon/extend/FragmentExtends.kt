package cn.soloho.oraclebb.extend

import android.support.annotation.StringRes
import android.view.LayoutInflater
import com.blankj.utilcode.util.ToastUtils

/**
 * Created by kjsolo on 2017/6/7.
 */

fun android.support.v4.app.Fragment.getLayoutInflater() = LayoutInflater.from(activity)!!

fun android.support.v4.app.Fragment.showToast(@StringRes message: Int) = ToastUtils.showShort(message)

fun android.support.v4.app.Fragment.showToast(message: String, vararg args: Any) = ToastUtils.showShort(message, args)