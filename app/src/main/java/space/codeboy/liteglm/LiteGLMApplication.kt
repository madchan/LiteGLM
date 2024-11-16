package space.codeboy.liteglm

import android.app.Application
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import space.codeboy.liteglm.util.LogManager
import java.io.IOException
import java.net.URLDecoder




class LiteGLMApplication : Application() {

    companion object {
        lateinit var instance: LiteGLMApplication
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
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(Interceptor { chain ->
                val original: Request = chain.request()
                val requestBuilder: Request.Builder = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
//                    .header("Authorization", "Bearer b7daebec94ae582a875ef3053997da80.0nFvHEiQ2612Jcmg")
                    .header("Authorization", "Bearer ff8fb34e49d4076b25036b25d7f53c3c.FE2pApWZamMSEUXC")
                val request: Request = requestBuilder.build()

                val params = try {
                    val buffer = Buffer()
                    request.body?.writeTo(buffer)
                    URLDecoder.decode(buffer.readUtf8(), "utf-8")
                } catch (e: IOException) {
                    e.printStackTrace()
                    ""
                }
                LogManager.log("Interceptor: params = $params")

                chain.proceed(request)
            }).build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://open.bigmodel.cn/api/paas/v4/")
//            .baseUrl("https://api.302.ai/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}