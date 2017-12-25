package cn.soloho.oraclebb.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Html
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by kjsolo on 2017/11/23.
 */
@GlideModule
class GlideModuleImpl : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.setDefaultRequestOptions(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888))
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(HeaderInterceptor())
        builder.readTimeout(15, TimeUnit.SECONDS)
        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.cookieJar(HttpClientFactory.get().defaultHttpClient.cookieJar())

        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(builder.build()))
        //registry.register(Bitmap::class.java, PaletteBitmap::class.java, PaletteBitmapTranscoder(glide.bitmapPool))
        //registry.append(Uri::class.java, Drawable::class.java, AppIconDecoderWithPkgName(context))
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    private class HeaderInterceptor : Interceptor {

        private val userAgent = Html.escapeHtml(HttpClientFactory.randomUserAgent)

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val builder = originalRequest.newBuilder()
            try {
                val headers = ArrayList<String>()
                headers.add("User-Agent")
                headers.add(userAgent)
                headers.add("Accept-Language")
                headers.add("zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")

                if (originalRequest.url().host() == "i.meizitu.net") {
                    headers.add("Referer")
                    headers.add(originalRequest.url().toString())
                }
                var i = 0
                while (i < headers.size) {
                    val name = headers[i]
                    val value = headers[i + 1]
                    builder.header(name, value)
                    i += 2
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return chain.proceed(builder.build())
        }
    }

}