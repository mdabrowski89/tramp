package pl.mobite.tramp.di

import android.content.Context
import com.google.gson.GsonBuilder
import com.jaredsburrows.retrofit2.adapter.synchronous.SynchronousCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import pl.mobite.tramp.BuildConfig
import pl.mobite.tramp.R
import pl.mobite.tramp.data.remote.backend.TrampBackend
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val retrofitModule = module {

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(get<String>("backendUrl"))
            .client(get())
            .addConverterFactory(get())
            .addCallAdapterFactory(get())
            .build()
    }

    single<String>("backendUrl") {
        get<Context>().getString(R.string.tramp_backend_url)
    }

    single<Converter.Factory> {
        GsonConverterFactory.create(GsonBuilder().serializeNulls().create())
    }

    single<CallAdapter.Factory> {
        SynchronousCallAdapterFactory.create()
    }

    single<Interceptor>("logging") {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        interceptor
    }

    single<OkHttpClient> {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(get("logging"))
        builder.build()
    }

    factory<TrampBackend> { get<Retrofit>().create(TrampBackend::class.java) }
}