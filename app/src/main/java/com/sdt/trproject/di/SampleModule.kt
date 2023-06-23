package com.sdt.trproject.di

import android.content.Context
import android.content.SharedPreferences
import com.sdt.trproject.BuildConfig
import com.sdt.trproject.SharedPrefKeys
import com.sdt.trproject.network.AppCookieJar
import com.sdt.trproject.services.TrainApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Headers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


interface AnalyticsService {
    fun analyticsMethods()
}

// Constructor-injected, because Hilt needs to know how to
// provide instances of AnalyticsServiceImpl, too.
class AnalyticsServiceImpl @Inject constructor(

) : AnalyticsService {
    override fun analyticsMethods() {

    }
}

@Module
@InstallIn(ActivityComponent::class)
object SampleModule { // 싱글톤 객체, 상수화된 객체

    @Provides
    fun provideSharedPref(
        @ApplicationContext context: Context
    ): SharedPreferences =
        context.getSharedPreferences(SharedPrefKeys.PREF_NAME, Context.MODE_PRIVATE)

    @Provides
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        sharedPreferences: SharedPreferences
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cookieJar(AppCookieJar(context))
            .addInterceptor {
                val newRequest = it.request().newBuilder().headers(
                    Headers.headersOf(
                        //SharedPrefKeys.SET_COOKIE
                        SharedPrefKeys.COOKIE,
                        sharedPreferences.getString(SharedPrefKeys.SET_COOKIE, "") ?: ""
                        //, sharedPreferences.getString(SharedPrefKeys.SET_COOKIE, "") !!
                    )
                ).build()

                println(
                    "쿠키 확인 : ${sharedPreferences.getString(SharedPrefKeys.SET_COOKIE, "") ?: ""}"
                )

                val response = it.proceed(newRequest)
                response
            }
            .build()

    }

    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_ADDR)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideTrainApiService(
        retrofit: Retrofit
    ): TrainApiService {
        return retrofit.create(TrainApiService::class.java)
    }

//    abstract fun bindAnalyticsService(
//        analyticsServiceImpl: AnalyticsServiceImpl
//    ): AnalyticsService
}