package space.cowboy.liteglm.network.bean.req

data class ChatCompletionReq(
    val messages: List<Message>,
    val model: String
)

data class Message(
    val content: String,
    val role: String
)