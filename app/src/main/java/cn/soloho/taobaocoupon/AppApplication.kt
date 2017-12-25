package cn.soloho.taobaocoupon

import android.app.Application
import com.blankj.utilcode.util.Utils

/**
 * Created by kjsolo on 2017/12/25.
 */
class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Utils.init(this)
        AppHolder.init(this)
    }

}