package space.cowboy.conversationbookmark

import LogManager
import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.google.gson.Gson

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
        LogManager.log("onAccessibilityEvent: eventType = ${event.eventType}, contentChangeTypes = ${event.contentChangeTypes}")
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                val rootNode = parseNode(event.source).toString()
                LogManager.log("onAccessibilityEvent nodeTree = $rootNode")
            }
        }
    }

    // 递归遍历获取节点信息
    private fun parseNode(node: AccessibilityNodeInfo?): NodeInfo? {
        if (node == null || !node.isVisibleToUser) return null

        val bounds = Rect()
        node.getBoundsInScreen(bounds)

        val nodeInfo = NodeInfo()
        nodeInfo.viewIdResourceName = node.viewIdResourceName
        nodeInfo.className = node.className.toString()
        nodeInfo.text = node.text
        nodeInfo.isSelected = node.isSelected
        if (node.isCheckable) {
            nodeInfo.isChecked = node.isChecked
        }
        nodeInfo.contentDescription = node.contentDescription
        nodeInfo.bounds = bounds

        // 遍历子节点
        if (node.childCount > 0) {
            val children = mutableListOf<NodeInfo>()
            for (i in 0 until node.childCount) {
                val childInfo = parseNode(node.getChild(i))
                if (childInfo != null) {
                    children.add(childInfo)
                }
            }
            nodeInfo.children = children
        }

        return nodeInfo
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

        val editText =
            rootNode.findAccessibilityNodeInfosByViewId("$WECHAT_PACKAGE:id/search_input")
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