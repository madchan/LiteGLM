package space.cowboy.lightglm.network.bean.req

data class FunctionCall(
    val actions: List<Action>
)

data class Action(
    val done: Boolean,
    val function: String,
    val params: Params
)

data class Params(
    val bounds: Bounds,
    val viewId: String
)

data class Bounds(
    val bottom: Int,
    val left: Int,
    val right: Int,
    val top: Int
)