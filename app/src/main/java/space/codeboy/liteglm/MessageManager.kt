package space.codeboy.liteglm


object MessageManager {

    private val messageMap = mutableMapOf<String, ChatMessage>()
    
    fun saveLastMessage(groupName: String, message: String) {
        messageMap[groupName] = ChatMessage(groupName, message)
    }
    
    fun getLastMessage(groupName: String): String? {
        return messageMap[groupName]?.lastMessage
    }
} 