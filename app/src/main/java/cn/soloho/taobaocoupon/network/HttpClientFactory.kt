package cn.soloho.oraclebb.network

import cn.soloho.taobaocoupon.AppHolder
import cn.soloho.taobaocoupon.BuildConfig
import com.blankj.utilcode.util.EncryptUtils
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by kjsolo on 16/3/10.
 */
class HttpClientFactory private constructor() {

    companion object {

        private const val CLIENT_NORMAL = "NORMAL"
        private const val CLIENT_TAOBAO = "TAOBAO"

        private val USER_AGENTS = arrayOf(
                "Mozilla/5.0 (Windows NT 6.1)AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.124 Safari/537.36)",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36",
                "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
                "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0;", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv,2.0.1) Gecko/20100101 Firefox/4.0.1",
                "Mozilla/5.0 (Windows NT 6.1; rv,2.0.1) Gecko/20100101 Firefox/4.0.1",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon 2.0)",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; TencentTraveler 4.0)",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SE 2.X MetaSr 1.0; SE 2.X MetaSr 1.0; .NET CLR 2.0.50727; SE 2.X MetaSr 1.0)",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; 360SE)",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.71 Safari/537.1 LBBROWSER",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")

        fun get() = SingletonHolder.instance

        val randomUserAgent: String by lazy {
            val random = Random()
            val index = random.nextInt(USER_AGENTS.size)
            USER_AGENTS[index]
        }
    }

    private val httpClients = HashMap<String, OkHttpClient>()
    private val httpClient = OkHttpClient.Builder().build()

    private object SingletonHolder {
        val instance = HttpClientFactory()
    }

    val defaultHttpClient: OkHttpClient
        get() {
            var newHttpClient: OkHttpClient? = httpClients[CLIENT_NORMAL]
            if (newHttpClient == null) {
                newHttpClient = httpClient.newBuilder()
                        .addInterceptor(HeaderInterceptor())
                        .addInterceptor(HttpLoggingInterceptor()
                                .apply {
                                    level = if (BuildConfig.DEBUG)
                                        HttpLoggingInterceptor.Level.HEADERS
                                    else
                                        HttpLoggingInterceptor.Level.BASIC
                                })
                        .readTimeout(15, TimeUnit.SECONDS)
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(AppHolder.context)))
                        .build()
                httpClients.put(CLIENT_NORMAL, newHttpClient)
            }
            return newHttpClient!!
        }

    private class HeaderInterceptor : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val builder = originalRequest.newBuilder()
            try {
                val headers = ArrayList<String>()
                headers.add("User-Agent")
                headers.add(randomUserAgent)
                headers.add("Accept-Language")
                headers.add("zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                headers.add("Referer")
                headers.add(originalRequest.url().toString())
                headers.add("Host")
                headers.add(originalRequest.url().host())

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
