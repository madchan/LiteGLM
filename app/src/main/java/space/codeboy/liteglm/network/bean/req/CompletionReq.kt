package space.codeboy.liteglm.network.bean.req

data class CompletionReq(
    val messages: List<ReqMessage>?,
    val model: String?,
    val tools: List<Tool>? = null
)

data class ReqMessage(
    val content: String,
    val role: String
)

data class Tool(
    val function: Function,
    val type: String
)

data class Function(
    val description: String,
    val name: String,
    val parameters: Parameters
)

data class Parameters(
    val properties: Map<String, Property>,
    val required: List<String>,
    val type: String
)

data class Property(
    val description: String,
    val type: String
)
