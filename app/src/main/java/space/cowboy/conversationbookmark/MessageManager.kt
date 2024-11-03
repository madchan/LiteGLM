package space.cowboy.conversationbookmark

import space.cowboy.conversationbookmark.ChatMessage

object MessageManager {
    private val messageMap = mutableMapOf<String, ChatMessage>()
    
    fun saveLastMessage(groupName: String, message: String) {
        messageMap[groupName] = ChatMessage(groupName, message)
    }
    
    fun getLastMessage(groupName: String): String? {
        return messageMap[groupName]?.lastMessage
    }
} 