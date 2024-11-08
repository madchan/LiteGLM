package space.cowboy.liteglm

import space.cowboy.liteglm.service.WeChatAccessibilityService.Companion.WECHAT_PACKAGE
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import space.cowboy.liteglm.service.WeChatAccessibilityService

class FloatingButtonService : Service() {
    private var windowManager: WindowManager? = null
    private var floatingButton: View? = null
    private var params: WindowManager.LayoutParams? = null

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createFloatingButton()
    }

    private fun createFloatingButton() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        floatingButton = LayoutInflater.from(this)
            .inflate(R.layout.floating_button, null)
        
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params?.gravity = Gravity.TOP or Gravity.START
        params?.x = 0
        params?.y = 100

        floatingButton?.setOnClickListener {
            performSearchAction()
        }

        windowManager?.addView(floatingButton, params)
    }

    private fun performSearchAction() {
        val service = WeChatAccessibilityService.instance ?: return
        
//        // 点击搜索按钮
//        service.findAndClickById("$WECHAT_PACKAGE:id/search_btn")
//
//        // 获取群名并填入最后消息
//        val groupName = service.findTextById("$WECHAT_PACKAGE:id/gas")
//        val lastMessage = MessageManager.getLastMessage(groupName.toString())
//
//        if (lastMessage != null) {
//            // 填入搜索内容
//            service.inputText(lastMessage)
//            // 点击第一条搜索结果
//            service.findAndClickById("$WECHAT_PACKAGE:id/search_result_item")
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager?.removeView(floatingButton)
    }
} 