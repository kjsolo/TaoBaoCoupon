package cn.soloho.taobaocoupon

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import java.io.File


/**
 * Created by kjsolo on 2017/6/1.
 */

@SuppressLint("StaticFieldLeak")
object AppHolder {

    lateinit var context: Context
        private set

    val cacheDir: File
        get() {
            var dir = context.externalCacheDir
            if (dir == null) {
                dir = context.cacheDir
            }
            return dir
        }

    fun init(context: Application) {
        this.context = context;
    }

}
