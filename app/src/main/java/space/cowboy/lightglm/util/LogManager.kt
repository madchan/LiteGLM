package space.cowboy.lightglm.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object LogManager {
    private val logs = mutableListOf<String>()
    private var listener: ((String) -> Unit)? = null

    fun log(message: String) {
        val timeStamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val logMessage = "[$timeStamp] $message"
        logs.add(logMessage)
        Log.d("space.cowboy.lightglm.util.LogManager", logMessage);
        listener?.invoke(logMessage)
    }

    fun setLogListener(listener: (String) -> Unit) {
        LogManager.listener = listener
    }

    fun getAllLogs(): String = logs.joinToString("\n")
} 