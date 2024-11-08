package space.codeboy.liteglm

import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import space.codeboy.liteglm.network.bean.req.CompletionReq
import space.codeboy.liteglm.network.bean.req.Message
import space.codeboy.liteglm.network.bean.req.Tool
import space.codeboy.liteglm.LiteGLMApplication.Companion.retrofit
import space.codeboy.liteglm.network.api.ModelAPI
import space.codeboy.liteglm.network.bean.resp.CompletionsResp
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
                    resp.body()?.choices?.get(0)?.message?.toolCalls?.forEach {
                        LogManager.log("ChatBot toolCall: $it")
                        it.invoke()
                    }
                }

                override fun onFailure(p0: Call<CompletionsResp>, p1: Throwable) {
                    LogManager.log("ChatBot completions onFailure: $p1")
                }
            }
            )
    }

    private fun createCompletionReq(content: String): CompletionReq {
        return CompletionReq(
            messages = listOf(Message(content.trimIndent(),"user")),
            model = model,
            tools = createTools()
        )
    }

    private fun createTools(): List<Tool> {
        val tools = mutableListOf<Tool>()
        val findAndClickByIdJson = """
            {
              "type": "function",
              "function": {
                "name": "findAndClickByIdJson",
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
                    }
                  },
                  "required": [
                    "viewId",
                    "bound"
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
                      "description": "要输入的文本"
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
        tools.add(Gson().fromJson(inputTextByIdJson, Tool::class.java))
        return tools
    }

}