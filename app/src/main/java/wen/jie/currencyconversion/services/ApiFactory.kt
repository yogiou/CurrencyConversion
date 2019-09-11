package wen.jie.currencyconversion.services

import android.util.Log
import wen.jie.currencyconversion.Config
import wen.jie.currencyconversion.utils.ApiErrorInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.*
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

interface IApiClient {
    var mCookieManager: CookieManager?
    var mCertificatePinner: CertificatePinner?
    var mClient: OkHttpClient

    fun createHeaderInterceptor(): Interceptor {
        // Create Interceptor for adding common headers, may add headers if needed later
        return Interceptor { chain ->
            val request = chain.request().newBuilder().build()
            chain.proceed(request)
        }
    }

    fun createMoshi(): Moshi {
        // Create Moshi instance and add support for Kotlin annotation and Datetime conversion
        return Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .build()
    }

    fun <T> createService(baseUrl: String, apiClass: Class<T>, oOAuthOrNot: Boolean = true): T {
        val moshi = createMoshi()
        // Build the retrofit with the above objects,
        // and use RxJava2CallAdapter to wrap the response to Observable
        // and use ScalarsConverter to support raw application/json in send request

        //  (security and cookies support) if we need Cookie and Certificate Pinning support later, we can create CookieManager and CertificatePinner and pass to createOkhttpClient()
        mClient = createOkhttpClient(ApiFactory.mCookieManager, ApiFactory.mCertificatePinner)

        val builder = Retrofit.Builder()
            .client(mClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(baseUrl)
        return builder.build().create(apiClass)
    }

    fun createOkhttpClient(cookieManager: CookieManager? = null, certificatePinner: CertificatePinner? = null): OkHttpClient {
        // logging interceptor
        val logging = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> Log.v("OkHttp", message) })
        logging.level = HttpLoggingInterceptor.Level.BODY

        //  can add authentication interceptor if needed later
        var okHttpBuilder = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(ApiErrorInterceptor) // api error interceptor
            .addInterceptor(createHeaderInterceptor())
            .connectTimeout(Config.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Config.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Config.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .followRedirects(true)

        if (cookieManager != null) {
            okHttpBuilder.cookieJar(JavaNetCookieJar(cookieManager))
        }

        if (certificatePinner != null) {
            okHttpBuilder.certificatePinner(certificatePinner)
        }

        return okHttpBuilder.build()
    }

    //  certificate pin support
    fun createCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            .build()
    }

    //  cookies support
    fun createCookieManager(): CookieManager {
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        return cookieManager
    }
}

object ApiFactory : IApiClient {
    override var mCookieManager: CookieManager? = null // createCookieManager()
    override var mCertificatePinner: CertificatePinner? = null // createCertificatePinner()
    override var mClient: OkHttpClient = createOkhttpClient(mCookieManager, mCertificatePinner)
}