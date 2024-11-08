package space.cowboy.lightglm

import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import space.cowboy.lightglm.LightGLMApplication.Companion.retrofit
import space.cowboy.lightglm.network.api.ModelAPI
import space.cowboy.lightglm.network.bean.req.ChatCompletionReq
import space.cowboy.lightglm.util.LogManager

class ChatBot {

    companion object {
        val instance = ChatBot()
    }

    val model = "claude-3-5-sonnet-20241022"
    val modelAPI = retrofit.create(ModelAPI::class.java);

    var waiting = false

    fun chatCompletions(content: String) {
        if (waiting) {
            return
        }
        waiting = true
        val json = """
            {
              "model": "claude-3-5-sonnet-20241022",
              "messages": [
                {
                  "role": "user",
                  "content": "Hello!"
                }
              ]
            }
        """.trimIndent()
        modelAPI.chatCompletions(Gson().fromJson(json, ChatCompletionReq::class.java))
//        modelAPI.chatCompletions(model, listOf(Message("user", content)), createTools())
            .enqueue(object : retrofit2.Callback<Any> {
                override fun onResponse(
                    call: Call<Any>,
                    resp: Response<Any>
                ) {
                    val completionsResp = resp.body()
                    if (completionsResp != null) {
                        val content = resp.body()
                        LogManager.log("ChatBot chatCompletions: $content")
                    }
                }

                override fun onFailure(p0: Call<Any>, p1: Throwable) {
                }
            }
            )
    }

//    fun createTools(): List<Tool> {
//        val tools = mutableListOf<Tool>()
//        val findAndClickByIdJson = """
//            {
//              "type": "function",
//              "function": {
//                "name": "findAndClickByIdJson",
//                "description": "根据ViewId找到可点击控件并点击",
//                "parameters": {
//                  "type": "object",
//                  "properties": {
//                    "viewId": {
//                      "type": "string",
//                      "description": "控件的Id"
//                    },
//                    "bound": {
//                      "type": "rect",
//                      "description": "控件的显示区域"
//                    }
//                  },
//                  "required": [
//                    "viewId",
//                    "bound"
//                  ]
//                }
//              }
//            }
//        """
//        tools.add(Gson().fromJson(findAndClickByIdJson, Tool::class.java))
//        val inputTextByIdJson = """
//            {
//              "type": "function",
//              "function": {
//                "name": "inputTextById",
//                "description": "根据ViewId找到输入框并输入文本",
//                "parameters": {
//                  "type": "object",
//                  "properties": {
//                    "viewId": {
//                      "type": "string",
//                      "description": "控件的Id"
//                    },
//                    "bound": {
//                      "type": "rect",
//                      "description": "控件的显示区域"
//                    },
//                    "text": {
//                      "type": "string",
//                      "description": "要输入的文本"
//                    }
//                  },
//                  "required": [
//                    "viewId",
//                    "bound",
//                    "text"
//                  ]
//                }
//              }
//            }
//        """
//        tools.add(Gson().fromJson(inputTextByIdJson, Tool::class.java))
//        return tools
//    }

}