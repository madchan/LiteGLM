package space.codeboy.liteglm.network.bean.resp

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import space.codeboy.liteglm.service.WeChatAccessibilityService

class CompletionsResp {
    val choices: List<Choice>? = null
    val created: Int? = null
    val id: String? = null

    @SerializedName("object")
    val obj: String? = null
    val usage: Usage? = null
}

class Choice {
    val finish_reason: String? = null
    val index: Int? = null
    val message: Message? = null
}

class Usage {
    val completion_tokens: Int? = null
    val prompt_tokens: Int? = null
    val total_tokens: Int? = null
}

data class Message(
    val content: String,
    val role: String,
    @SerializedName("tool_calls")
    val toolCalls: List<ToolCall>
)

class ToolCall {
    val function: Function? = null
    val id: String? = null
    val type: String? = null

    fun invoke() {
        Thread.sleep(2000)
        val argument = function?.parseArgument()
        when {
            function?.name?.contains("findAndClickById") == true -> {
                WeChatAccessibilityService.instance?.findAndClickById(
                    viewId = argument?.viewId ?: "",
                    bounds = android.graphics.Rect(
                        argument?.bound?.left ?: 0,
                        argument?.bound?.top ?: 0,
                        argument?.bound?.right ?: 0,
                        argument?.bound?.bottom ?: 0
                    )
                )
            }

            function?.name?.contains("inputTextById") == true-> {
                WeChatAccessibilityService.instance?.inputTextById(
                    viewId = argument?.viewId ?: "",
                    bounds = android.graphics.Rect(
                        argument?.bound?.left ?: 0,
                        argument?.bound?.top ?: 0,
                        argument?.bound?.right ?: 0,
                        argument?.bound?.bottom ?: 0
                    ),
                    text = argument?.text ?: ""
                )
            }
        }
    }
}

class Function {
    val arguments: String? = null
    val name: String? = null

    fun parseArgument(): Argument? {
        return Gson().fromJson(arguments, Argument::class.java)
    }
}

class Argument {
    val bound: Bound? = null
    val viewId: String? = null
    val text: String? = null
}

class Bound {
    val bottom: Int? = null
    val left: Int? = null
    val right: Int? = null
    val top: Int? = null
}
