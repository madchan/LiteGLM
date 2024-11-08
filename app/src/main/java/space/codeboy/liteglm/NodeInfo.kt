package space.codeboy.liteglm

import android.graphics.Rect
import com.google.gson.Gson

class NodeInfo {
    var viewIdResourceName: CharSequence? = null
    var className: CharSequence? = null
    var contentDescription: CharSequence? = null
    var bounds: Rect? = null
    var text: CharSequence? = null
    var isSelected: Boolean? = null
    var isChecked: Boolean? = null
    var children: MutableList<NodeInfo>? = null

    override fun toString(): String {
        return Gson().toJson(this)
    }
}