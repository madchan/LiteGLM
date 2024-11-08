package space.cowboy.liteglm

data class ChatMessage(
    val groupName: String,
    val lastMessage: String,
    val timestamp: Long = System.currentTimeMillis()
) 