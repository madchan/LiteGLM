package space.cowboy.conversationbookmark

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class WeChatAccessibilityService : AccessibilityService() {

    companion object {
        const val WECHAT_PACKAGE = "com.tencent.mm"
        var instance: WeChatAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.packageName != WECHAT_PACKAGE) return

        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val className = event.className.toString()
                LogManager.log("窗口变化: $className")

                if (className.contains("LauncherUI")) {
                    val rootNode = rootInActiveWindow ?: return
                    val messageNode = rootNode.findAccessibilityNodeInfosByViewId(
                        "$WECHAT_PACKAGE:id/b4k"
                    ).lastOrNull()

                    val groupNameNode = rootNode.findAccessibilityNodeInfosByViewId(
                        "$WECHAT_PACKAGE:id/gas"
                    ).firstOrNull()

                    if (messageNode != null && groupNameNode != null) {
                        val groupName = groupNameNode.text.toString()
                        val message = messageNode.text.toString()
                        MessageManager.saveLastMessage(groupName, message)
                        LogManager.log("保存群消息 - 群: $groupName, 消息: $message")
                    } else {
                        LogManager.log("未找到群名或消息节点")
                    }
                }
            }
        }
    }

    fun findAndClickById(viewId: String): Boolean {
        LogManager.log("尝试点击: $viewId")
        val rootNode = rootInActiveWindow ?: return false.also {
            LogManager.log("根节点为空")
        }

        val targetNode = rootNode.findAccessibilityNodeInfosByViewId(viewId).firstOrNull()
        return if (targetNode?.isClickable == true) {
            val result = targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            LogManager.log("点击${if (result) "成功" else "失败"}")
            result
        } else {
            LogManager.log("未找到可点击节点")
            false
        }
    }

    fun findTextById(viewId: String): String? {
        LogManager.log("查找文本: $viewId")
        val rootNode = rootInActiveWindow ?: return null.also {
            LogManager.log("根节点为空")
        }

        return rootNode.findAccessibilityNodeInfosByViewId(viewId)
            .firstOrNull()
            ?.text?.toString()
            ?.also { LogManager.log("找到文本: $it") }
            ?: run {
                LogManager.log("未找到文本")
                null
            }
    }

    fun inputText(text: String): Boolean {
        LogManager.log("尝试输入文本: $text")
        val rootNode = rootInActiveWindow ?: return false.also {
            LogManager.log("根节点为空")
        }

        val editText = rootNode.findAccessibilityNodeInfosByViewId("$WECHAT_PACKAGE:id/search_input")
            .firstOrNull() ?: return false.also {
            LogManager.log("未找到输入框")
        }

        val arguments = Bundle()
        arguments.putCharSequence(
            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
            text
        )

        val result = editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        LogManager.log("文本输入${if (result) "成功" else "失败"}")
        return result
    }

    override fun onInterrupt() {
        instance = null
    }
}