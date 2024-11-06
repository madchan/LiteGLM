package space.cowboy.lightglm

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var logTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logTextView = findViewById(R.id.logTextView)
        setupLogListener()
    }

    fun performRequestAccessibilityService(view: View?) {
        if (!isAccessibilityServiceEnabled()) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    fun performRequestFloatWindowPermission(view: View?) {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, 1234)
        }
        startServices()
    }

    private fun setupLogListener() {
        LogManager.setLogListener { logMessage ->
            runOnUiThread {
                logTextView.append("$logMessage\n")
                // 自动滚动到底部
                val scrollView = logTextView.parent as ScrollView
                scrollView.post {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        }
    }

    private fun startServices() {
        if (Settings.canDrawOverlays(this)) {
            startService(Intent(this, FloatingButtonService::class.java))
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityEnabled = Settings.Secure.getInt(
            contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED
        )

        if (accessibilityEnabled == 1) {
            val service = "${packageName}/${WeChatAccessibilityService::class.java.canonicalName}"
            val settingValue = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return settingValue?.contains(service) == true
        }
        return false
    }
} 