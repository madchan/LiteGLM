package space.codeboy.liteglm.util

class PromptTemplate {

    companion object {
        fun getTemplate(instruction: String, nodeTree: String): String {
            return """
                #{用户指令}
                $instruction

                #{函数调用}
                findAndClickById(viewId: String, bound: Rect)：根据ViewId找到可点击空间并点击;
                inputTextById(viewId: String, bound: Rect, text: String): 根据ViewId找到输入框并输入文本;

                #{任务}
                首先，请深入解析提示词中所提供的Android应用页面的{无障碍节点树结构}，但不需要输出；
                其次，分析当前的页面类型、包含哪些关键信息，有哪些可交互元素，但不需要输出；
                最后，思考要完成{用户指令}分别需要找到哪些节点，以及依次执行哪些{函数调用}，但不需要输出；

                #{输出格式}
                将要执行的{函数调用}类型以及{函数调用}所必要的参数以JSON格式输出；
                另外，用done参数表示{用户指令}已完成或者当前页面的操作还不足以完成。
                ```
                {"actions":[{"function":string,"params":{"viewId":string,"bounds":{"left":int,"top":int,"right":int,"bottom":int}},"done":bool}]}
                ```
                
                #{无障碍节点树结构}
                $nodeTree
            """
        }
    }
}