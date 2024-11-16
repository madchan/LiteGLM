package space.codeboy.liteglm

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Call
import retrofit2.Response
import space.codeboy.liteglm.LiteGLMApplication.Companion.retrofit
import space.codeboy.liteglm.network.api.ModelAPI
import space.codeboy.liteglm.network.bean.req.CompletionReq
import space.codeboy.liteglm.network.bean.req.ReqMessage
import space.codeboy.liteglm.network.bean.req.Tool
import space.codeboy.liteglm.network.bean.resp.CompletionsResp
import space.codeboy.liteglm.network.bean.resp.RespMessage
import space.codeboy.liteglm.util.LogManager

class ChatBot {

    companion object {
        val instance = ChatBot()
    }

    val model = "glm-4-plus"
    val modelAPI = retrofit.create(ModelAPI::class.java);

    var waiting = false

    fun chatCompletions(content: String) {
        if (waiting) {
            return
        }
        waiting = true

        modelAPI.chatCompletions(createCompletionReq(content))
            .enqueue(object : retrofit2.Callback<CompletionsResp> {
                override fun onResponse(
                    call: Call<CompletionsResp>,
                    resp: Response<CompletionsResp>
                ) {
                    LogManager.log("ChatBot completions onResponse: body = ${Gson().toJson(resp.body())}")
                    val message = resp.body()?.choices?.get(0)?.message
                    var toolCalls = message?.toolCalls
                    if (toolCalls?.isNotEmpty() == true) {
                        toolCalls.forEach {
                            LogManager.log("ChatBot completions onResponse toolCall: ${Gson().toJson(it)}")
                            it.invoke()
                        }
                    } else {
                        val toolCallsJson = extractJson(message?.content ?: "")
                        if (toolCallsJson?.isNotEmpty() == true) {
                            try {
                                toolCalls = Gson().fromJson(toolCallsJson, RespMessage::class.java).toolCalls
                                toolCalls?.forEach {
                                    LogManager.log("ChatBot completions onResponse toolCall: ${Gson().toJson(it)}")
                                    it.invoke()
                                }
                            } catch (e: JsonSyntaxException) {
                                LogManager.log("ChatBot completions onError: $e")
                            }

                        }
                    }
                }

                override fun onFailure(p0: Call<CompletionsResp>, p1: Throwable) {
                    LogManager.log("ChatBot completions onFailure: $p1")
                }
            }
            )
    }

    fun extractJson(text: String): String? {
        // 尝试找到JSON字符串，并忽略任何非JSON文本
        val jsonStart = text.indexOf('{')
        val jsonEnd = text.lastIndexOf('}')

        if (jsonStart == -1 || jsonEnd == -1 || jsonStart >= jsonEnd) {
            return null // 没有找到有效的JSON
        }

        val jsonString = text.substring(jsonStart, jsonEnd + 1)
        return removeEscapeCharacters(jsonString)
    }

    // 去除Json转义符的方法
    private fun removeEscapeCharacters(input: String): String {
        return input.replace("\\\"", "\"")
    }

    private fun createCompletionReq(content: String): CompletionReq {
        return CompletionReq(
            messages = listOf(ReqMessage(content.trimIndent(),"user")),
            model = model
        )
    }

    private fun createTools(): List<Tool> {
        val tools = mutableListOf<Tool>()
        val findAndClickByIdJson = """
            {
              "type": "function",
              "function": {
                "name": "findAndClickById",
                "description": "根据ViewId找到可点击控件并点击",
                "parameters": {
                  "type": "object",
                  "properties": {
                    "viewId": {
                      "type": "string",
                      "description": "控件的Id"
                    },
                    "bound": {
                      "type": "rect",
                      "description": "控件的显示区域"
                    },
                    "text": {
                      "type": "string",
                      "description": "控件的文本"
                    }
                  },
                  "required": [
                    "viewId",
                    "bound",
                    "text"
                  ]
                }
              }
            }
        """
        tools.add(Gson().fromJson(findAndClickByIdJson, Tool::class.java))
        val inputTextByIdJson = """
            {
              "type": "function",
              "function": {
                "name": "inputTextById",
                "description": "根据ViewId找到输入框并输入文本",
                "parameters": {
                  "type": "object",
                  "properties": {
                    "viewId": {
                      "type": "string",
                      "description": "控件的Id"
                    },
                    "bound": {
                      "type": "rect",
                      "description": "控件的显示区域"
                    },
                    "text": {
                      "type": "string",
                      "description": "控件的文本"
                    },
                    "input": {
                      "type": "string",
                      "description": "要输入的文本"
                    }
                  },
                  "required": [
                    "viewId",
                    "bound",
                    "input"
                  ]
                }
              }
            }
        """
        tools.add(Gson().fromJson(inputTextByIdJson, Tool::class.java))
        return tools
    }

}