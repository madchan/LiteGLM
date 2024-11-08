package space.cowboy.lightglm.network.bean.resp

import com.google.gson.annotations.SerializedName

data class CompletionsResp(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    @SerializedName("object")
    val obj: String,
    val usage: Usage
)

data class Choice(
    val finish_reason: String,
    val index: Int,
    val message: Message
)

data class Usage(
    val completion_tokens: Int,
    val prompt_tokens: Int,
    val total_tokens: Int
)

data class Message(
    val content: String,
    val role: String
)