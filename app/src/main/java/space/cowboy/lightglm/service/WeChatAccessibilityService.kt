package space.cowboy.liteglm.service

import space.cowboy.liteglm.util.LogManager
import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import space.cowboy.liteglm.ChatBot
import space.cowboy.liteglm.NodeInfo
import space.cowboy.liteglm.util.PromptTemplate

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
                LogManager.log("onAccessibilityEvent: nodeTrees = $rootNode")
                val template = PromptTemplate.getTemplate("给小三打声招呼", rootNode)
                ChatBot.instance.chatCompletions(template)
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
        nodeInfo.className = node.className?.toString()
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

    fun findAndClickById(viewId: String, bounds: Rect): Boolean {
        LogManager.log("尝试点击: $viewId")
        val rootNode = rootInActiveWindow ?: return false.also {
            LogManager.log("根节点为空")
        }

        /// 目标节点：符合条件的可点击节点
        val targetNode = rootNode.findAccessibilityNodeInfosByViewId(viewId).first {
            val nodeBounds = Rect()
            it.getBoundsInScreen(nodeBounds)
            bounds.contains(nodeBounds)
        }
        return if (targetNode?.isClickable == true) {
            val result = targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            LogManager.log("点击${if (result) "成功" else "失败"}")
            result
        } else {
            LogManager.log("未找到可点击节点")
            false
        }
    }
    fun inputTextById(viewId: String, bounds: Rect, text: String, ): Boolean {
        LogManager.log("尝试输入文本: $text")
        val rootNode = rootInActiveWindow ?: return false.also {
            LogManager.log("根节点为空")
        }

        val editText =
            rootNode.findAccessibilityNodeInfosByViewId(viewId)
                .first {
                    val nodeBounds = Rect()
                    it.getBoundsInScreen(nodeBounds)
                    bounds.contains(nodeBounds)
                } ?: return false.also {
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