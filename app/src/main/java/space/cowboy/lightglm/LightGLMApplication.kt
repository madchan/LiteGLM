package space.cowboy.lightglm

import android.app.Application
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LightGLMApplication : Application() {

    companion object {
        lateinit var instance: LightGLMApplication
        lateinit var retrofit: Retrofit
    }

    init {
        instance = this
    }


    override fun onCreate() {
        super.onCreate()
        initNetworkComponent()
    }

    private fun initNetworkComponent() {
        val client = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original: Request = chain.request()
                val requestBuilder: Request.Builder = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer sk-7wl220IPd8XhKT9qDyD6zkwa6m8iM92y78vetFDVzWG8AgQp")
                val request: Request = requestBuilder.build()
                chain.proceed(request)
            }).build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://api.302.ai/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}