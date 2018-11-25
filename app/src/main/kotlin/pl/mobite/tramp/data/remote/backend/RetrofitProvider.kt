package pl.mobite.tramp.data.remote.backend

import com.google.gson.GsonBuilder
import com.jaredsburrows.retrofit2.adapter.synchronous.SynchronousCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.mobite.tramp.BuildConfig
import pl.mobite.tramp.R
import pl.mobite.tramp.TrampApp
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitProvider private constructor() {

    companion object {

        val instance: Retrofit by lazy {
            Retrofit.Builder()
                    .baseUrl(TrampApp.instance.getString(R.string.tramp_backend_url))
                    .client(createHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
                    .addCallAdapterFactory(SynchronousCallAdapterFactory.create())
                    .build()
        }

        private fun createHttpClient(): OkHttpClient {
            val builder = OkHttpClient.Builder()
            builder.addInterceptor(createHttpLoginInterceptor())
            return builder.build()
        }

        private fun createHttpLoginInterceptor(): Interceptor {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            return interceptor
        }
    }
}


