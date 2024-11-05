package space.cowboy.conversationbookmark

import android.graphics.Rect
import com.google.gson.Gson
import org.json.JSONObject

class NodeInfo {
    var text: CharSequence? = null
    var viewIdResourceName: CharSequence? = null
    var className: CharSequence? = null
    var isSelected: Boolean = false
    var isChecked: Boolean = false
    var isVisibleToUser: Boolean = false
    var contentDescription: CharSequence? = null
    var bounds: Rect? = null
    var children: MutableList<NodeInfo> = mutableListOf()

    override fun toString(): String {
        return Gson().toJson(this)
    }
}