package space.codeboy.liteglm.network.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import space.codeboy.liteglm.network.bean.req.CompletionReq
import space.codeboy.liteglm.network.bean.resp.CompletionsResp

interface ModelAPI {
    @POST("chat/completions")
    fun chatCompletions(
        @Body body: CompletionReq,
    ): Call<CompletionsResp>
}