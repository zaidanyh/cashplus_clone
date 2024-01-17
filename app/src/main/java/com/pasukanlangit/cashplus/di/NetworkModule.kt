package com.pasukanlangit.cashplus.di

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.pasukanlangit.cashplus.BuildConfig
import com.pasukanlangit.cashplus.BuildConfig.BASE_URL_COMMON
import com.pasukanlangit.cashplus.data.api.ApiHelper
import com.pasukanlangit.cashplus.data.api.ApiHelperImplement
import com.pasukanlangit.cashplus.data.api.ApiService
import com.pasukanlangit.cashplus.ui.login.LoginActivity
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import okhttp3.*
import okhttp3.internal.http.promisesBody
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.internal.isProbablyUtf8
import okio.Buffer
import okio.GzipSource
import okio.IOException
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

val networkModule = module {
    factory(named("InterceptorDebug")){ provideInterceptorDebug(get(), get()) }
    factory(named("InterceptorRelease")){ provideInterceptorRelease(get(), get()) }
    single { provideOkHttpClient(
        interceptorDebug = get(named("InterceptorDebug")),
        interceptorRelease = get(named("InterceptorRelease"))
    )}
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }

    single<ApiHelper> {
        return@single ApiHelperImplement(get())
    }
}

private fun provideInterceptorDebug(application: Application, sharedPref: SharedPrefDataSource): Interceptor =
    ResponseHeaderInterceptor(application, sharedPref)

private fun provideInterceptorRelease(application: Application, sharedPref: SharedPrefDataSource): Interceptor =
    ReleaseResponseHeaderInterceptor(application, sharedPref)

private fun provideOkHttpClient(interceptorDebug: Interceptor, interceptorRelease: Interceptor) =  if (BuildConfig.DEBUG) {
    OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(interceptorDebug)
        .build()
} else OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .addInterceptor(interceptorRelease)
    .build()

class ReleaseResponseHeaderInterceptor(
    application: Application,
    private val sharedPref: SharedPrefDataSource,
): Interceptor {
    private var activeActivity: Activity? = null

    init {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activeActivity = activity
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {
                activeActivity = activity
            }
            override fun onActivityPaused(activity: Activity) {
                activeActivity = null
            }
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                activeActivity = null
            }
        })
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response: Response
        try {
            response = chain.proceed(request)
            if(response.code != 200){
                try {
                    val responseBodyCopy = response.peekBody(Long.MAX_VALUE)
                    val errorBody =
                        Gson().fromJson(responseBodyCopy.string(), ErrorMessageResponse::class.java)
                    val message = errorBody.msg ?: errorBody.message ?: ""
                    if (message.contains(other = "not login", ignoreCase = true) ||
                        message.contains(other = "not valid", ignoreCase = true) ||
                        message.contains(other = "tidak masuk", ignoreCase = true) ||
                        message.contains(other = "tidak valid", ignoreCase = true)) {
//                    if (message == "uuid not login" || message == "token not valid" || message == "uuid tidak masuk" || message == "token tidak valid") {
                        if(activeActivity != null) {
                            sharedPref.deleteAuth()
                            sharedPref.deleteProfile()

                            val intent = Intent(activeActivity, LoginActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            activeActivity?.startActivity(intent)
                        }
                    }
                }catch (e: Exception){
                    Log.d("okHttp", "Failed parsing error interceptor")
                }
            }
        } catch (io: IOException) {
            throw io
        }
        return response
    }
}

class ResponseHeaderInterceptor(
    application: Application,
    private val sharedPref: SharedPrefDataSource,
    private val logger: HttpLoggingInterceptor.Logger = HttpLoggingInterceptor.Logger.DEFAULT
): Interceptor {
    private var activeActivity: Activity? = null

    enum class Level {
        NONE,
        BASIC,
        HEADERS,
        BODY
    }

    init {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activeActivity = activity
            }
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {
                activeActivity = activity
            }
            override fun onActivityPaused(activity: Activity) {
                activeActivity = null
            }
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                activeActivity = null
            }
        })
    }

    @Volatile private var headersToRedact = emptySet<String>()
    private val level = Level.BODY

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }

        val logBody = level == Level.BODY
        val logHeaders = logBody || level == Level.HEADERS

        val requestBody = request.body

        val connection = chain.connection()
        var requestStartMessage =
            ("--> ${request.method} ${request.url}${if (connection != null) " " + connection.protocol() else ""}")
        if (!logHeaders && requestBody != null) {
            requestStartMessage += " (${requestBody.contentLength()}-byte body)"
        }
        logger.log(requestStartMessage)

        if (logHeaders) {
            val headers = request.headers

            if (requestBody != null) {
                // Request body headers are only present when installed as a network interceptor. When not
                // already present, force them to be included (if available) so their values are known.
                requestBody.contentType()?.let {
                    if (headers["Content-Type"] == null) {
                        logger.log("Content-Type: $it")
                    }
                }
                if (requestBody.contentLength() != -1L) {
                    if (headers["Content-Length"] == null) {
                        logger.log("Content-Length: ${requestBody.contentLength()}")
                    }
                }
            }

            for (i in 0 until headers.size) {
                logHeader(headers, i)
            }

            if (!logBody || requestBody == null) {
                logger.log("--> END ${request.method}")
            } else if (bodyHasUnknownEncoding(request.headers)) {
                logger.log("--> END ${request.method} (encoded body omitted)")
            } else if (requestBody.isDuplex()) {
                logger.log("--> END ${request.method} (duplex request body omitted)")
            } else if (requestBody.isOneShot()) {
                logger.log("--> END ${request.method} (one-shot body omitted)")
            } else {
                val buffer = Buffer()
                requestBody.writeTo(buffer)

                val contentType = requestBody.contentType()
                val charset: Charset = contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

                logger.log("")
                if (buffer.isProbablyUtf8()) {
                    logger.log(buffer.readString(charset))
                    logger.log("--> END ${request.method} (${requestBody.contentLength()}-byte body)")
                } else {
                    logger.log(
                        "--> END ${request.method} (binary ${requestBody.contentLength()}-byte body omitted)")
                }
            }
        }

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
            if(!response.isSuccessful) {
                try {
                    val responseBodyCopy = response.peekBody(Long.MAX_VALUE)
                    val errorBody = Gson().fromJson(responseBodyCopy.string(), ErrorMessageResponse::class.java)
                    val message = errorBody.msg ?: errorBody.message ?: ""
                    if (message.contains(other = "not login", ignoreCase = true) ||
                        message.contains(other = "not valid", ignoreCase = true) ||
                        message.contains(other = "tidak masuk", ignoreCase = true) ||
                        message.contains(other = "tidak valid", ignoreCase = true)) {
//                    if (message == "uuid not login" || message == "token not valid" || message == "uuid tidak masuk" || message == "token tidak valid") {
                        if (activeActivity != null) {
                            sharedPref.deleteAuth()
                            sharedPref.deleteProfile()

                            val intent = Intent(activeActivity, LoginActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            activeActivity?.startActivity(intent)
                        }
                    }
                }catch (IOE: IOException){
                    Log.d("okHttp", IOE.message.toString())
                }
            }
        } catch (e: Exception) {
            logger.log("<-- HTTP FAILED: $e")
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body!!
        val contentLength = responseBody.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        logger.log(
            "<-- ${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${response.request.url} (${tookMs}ms${if (!logHeaders) ", $bodySize body" else ""})")

        if (logHeaders) {
            val headers = response.headers
            for (i in 0 until headers.size) {
                logHeader(headers, i)
            }

            if (!logBody || !response.promisesBody()) {
                logger.log("<-- END HTTP")
            } else if (bodyHasUnknownEncoding(response.headers)) {
                logger.log("<-- END HTTP (encoded body omitted)")
            } else {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.
                var buffer = source.buffer

                var gzippedLength: Long? = null
                if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                    gzippedLength = buffer.size
                    GzipSource(buffer.clone()).use { gzippedResponseBody ->
                        buffer = Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    }
                }

                val contentType = responseBody.contentType()
                val charset: Charset = contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

                if (!buffer.isProbablyUtf8()) {
                    logger.log("")
                    logger.log("<-- END HTTP (binary ${buffer.size}-byte body omitted)")
                    return response
                }

                if (contentLength != 0L) {
                    logger.log("")
                    logger.log(buffer.clone().readString(charset))
                }

                if (gzippedLength != null) {
                    logger.log("<-- END HTTP (${buffer.size}-byte, $gzippedLength-gzipped-byte body)")
                } else {
                    logger.log("<-- END HTTP (${buffer.size}-byte body)")
                }
            }
        }
        return response
    }

    private fun logHeader(headers: Headers, i: Int) {
        val value = if (headers.name(i) in headersToRedact) "██" else headers.value(i)
        logger.log(headers.name(i) + ": " + value)
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }
}

private fun provideRetrofit(
    okHttpClient: OkHttpClient
): Retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL_COMMON)
    .client(okHttpClient)
    .build()

private fun provideApiService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)