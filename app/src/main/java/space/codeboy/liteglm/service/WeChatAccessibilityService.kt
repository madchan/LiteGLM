package space.codeboy.liteglm.service

import space.codeboy.liteglm.util.LogManager
import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import space.codeboy.liteglm.ChatBot
import space.codeboy.liteglm.NodeInfo
import space.codeboy.liteglm.util.PromptTemplate

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

    fun findAndClickById(viewId: String, bounds: Rect, text: String?): Boolean {
        val targetBounds = Rect(bounds.left, bounds.bottom, bounds.right, bounds.top)
        LogManager.log("尝试点击: $viewId")
        val rootNode = rootInActiveWindow ?: return false.also {
            LogManager.log("根节点为空")
        }

        /// 目标节点：符合条件的可点击节点
        val targetNodes = rootNode.findAccessibilityNodeInfosByViewId(viewId);

        var targetNode: AccessibilityNodeInfo? = null
        for (node in targetNodes) {
//            val nodeBounds = Rect()
//            node.getBoundsInScreen(nodeBounds)
//            val isContains = targetBounds.contains(nodeBounds)
//            LogManager.log("节点范围: $nodeBounds， 目标范围: $targetBounds， 是否包含: $isContains, 节点信息: ${node.text}")
//            if (isContains) {
//                targetNode = node
//                break
//            }
            LogManager.log("节点信息: ${node.text}, 目标信息: $text")
            if (node.text.toString() == text) {
                targetNode = node
                break
            }
        }

        return if (targetNode != null) {
            return attemptClick(targetNode)
        } else {
            LogManager.log("未找到可点击节点")
            false
        }
    }

    private fun attemptClick(node: AccessibilityNodeInfo): Boolean {
        if (!node.isClickable) {
            LogManager.log("节点不可点击, 尝试点击父节点")
            return attemptClick(node.parent)
        } else {
            val result = node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            LogManager.log("点击${if (result) "成功" else "失败"}")
            return result
        }
    }

    fun inputTextById(viewId: String, bounds: Rect, text: String): Boolean {
        LogManager.log("尝试输入文本: $text")
        val rootNode = rootInActiveWindow ?: return false.also {
            LogManager.log("根节点为空")
        }

        val editText =
            rootNode.findAccessibilityNodeInfosByViewId(viewId)
                .firstOrNull() {
                    if (it == null) return false
                    val nodeBounds = Rect()
                    it.getBoundsInScreen(nodeBounds)
                    bounds.contains(nodeBounds)
                }

        if (editText == null) {
            LogManager.log("未找到可输入节点")
            return false
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