package cn.soloho.taobaocoupon.view

import android.content.ClipboardManager
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import cn.soloho.oraclebb.extend.LOG_TAG
import cn.soloho.oraclebb.extend.cast
import cn.soloho.oraclebb.extend.showToast
import com.blankj.utilcode.util.RegexUtils
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import kotlinx.coroutines.experimental.async
import okhttp3.Request
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.net.URL


/**
 * Created by kjsolo on 2017/12/25.
 */
class MainActivity : BaseActivity(), ClipboardManager.OnPrimaryClipChangedListener {

    companion object {
        const val REGEX_TAOBAO_MESSAGE = "复制这条信息￥\\w+￥后打开"
        const val REGEX_URL = "http(s?)://[^\\s]*"
    }

    val manager by lazy { getSystemService(CLIPBOARD_SERVICE).cast<ClipboardManager>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        manager.addPrimaryClipChangedListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.removePrimaryClipChangedListener(this)
    }

    override fun onPrimaryClipChanged() {
        if (manager.hasPrimaryClip() && manager.primaryClip.itemCount > 0) {
            val clipText = manager.primaryClip.getItemAt(0).text
            if (clipText != null) {
                if (isTaobaoMessage(clipText)) {
                    val url = getUrlFromMessage(clipText)
                    if (url != null) {
                        showToast(url)
                        getUrl(url)
                    }
                }
            }
        }
    }

    fun isTaobaoMessage(message: CharSequence): Boolean {
        return RegexUtils.getMatches(REGEX_TAOBAO_MESSAGE, message).isNotEmpty()
    }

    fun getUrlFromMessage(message: CharSequence): String? {
        val matches = RegexUtils.getMatches(REGEX_URL, message)
        if (matches.isNotEmpty()) {
            return matches[0]
        }
        return null
    }

    fun getUrl(url: String) {
        val fakeView = FrameLayout(this)
        AgentWeb.with(this)
                .setAgentWebParent(fakeView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator()
                .defaultProgressBarColor()
                .setWebViewClient(WebViewClientImpl())
                .setOpenOtherAppWays(DefaultWebClient.OpenOtherAppWays.DISALLOW)
                .createAgentWeb()
                .ready()
                .go(url)
//        async {
//            val request = Request.Builder()
//                    .url(url)
//                    .build()
//            val client = OkHttpClient()
//            val response = client.newCall(request).execute()
//            val url = response.request().url().toString()
//            Log.w(LOG_TAG, url)
//        }
    }

    fun getGoodsDetail(id: String) {
        async {
            val request = Request.Builder()
                    .url("http://api.taokezhushou.com/api/v1/detail?app_key=e952a245d7021600&goods_id=${id}")
                    .build()
            val client = OkHttpClient()
            val response = client.newCall(request).execute()
            val dataString = response.body()?.string()

            //Log.w(LOG_TAG, url)
        }
    }

    private inner class WebViewClientImpl : WebViewClient() {

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.d(LOG_TAG, "onPageStarted $url")
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            Log.d(LOG_TAG, "onPageFinished $url")
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            Log.d(LOG_TAG, "shouldOverrideUrlLoading ${request.url}")
            if (request.url.scheme == "taobao") {
                val id = request.url.getQueryParameter("id")
            }
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
            //Log.d(LOG_TAG, "shouldInterceptRequest ${request.url}")
            return super.shouldInterceptRequest(view, request)
        }
    }

}