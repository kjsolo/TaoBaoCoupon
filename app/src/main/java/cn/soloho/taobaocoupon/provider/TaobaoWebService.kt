package cn.soloho.taobaocoupon.provider

import cn.soloho.oraclebb.network.HttpClientFactory
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by kjsolo on 2017/12/25.
 */
class TaobaoWebService {
    companion object {
        fun get() = SingleHolder.instance
        fun getService() = get().remoteService
    }

    private object SingleHolder {
        val instance = TaobaoWebService()
    }

    private val remoteService = newRemoteService();

    private fun newRemoteService(): RemoteService {
        val retrofit = Retrofit.Builder()
                .baseUrl(RemoteService.ENDPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(HttpClientFactory.get().defaultHttpClient)
                .build()
        return retrofit.create(RemoteService::class.java)
    }

    interface RemoteService {
        companion object {
            val ENDPOINT = "http://alicili.com/"
        }

        @GET("/")
        fun mainPage(): Observable<ResponseBody>

        // page-sort-category
        @GET("list/{keyword}/{page}-0-2/")
        fun listPage(@Path("keyword") keyword: String, @Path("page") page: Int): Observable<ResponseBody>

    }
}