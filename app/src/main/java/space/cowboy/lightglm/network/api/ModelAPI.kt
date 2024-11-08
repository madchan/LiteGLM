package space.cowboy.liteglm.network.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import space.cowboy.liteglm.network.bean.req.ChatCompletionReq
import space.cowboy.liteglm.network.bean.req.Message
import space.cowboy.liteglm.network.bean.resp.CompletionsResp

interface ModelAPI {
    @POST("chat/completions")
    fun chatCompletions(
        @Body body: ChatCompletionReq,
//        @Field("model") model: String?,
//        @Field("messages") messages: List<Message>,
//        @Field("tools") tools: List<Tool>
    ): Call<Any>
}