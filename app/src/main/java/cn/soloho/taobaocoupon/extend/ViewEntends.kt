package cn.soloho.oraclebb.extend

import android.view.View

/**
 * Created by kjsolo on 2017/8/7.
 */
fun View.canScrollDown() = this.canScrollVertically(-1)
fun View.canScrollRight() = this.canScrollHorizontally(-1)