package com.example.polyscrabbleclient.utils

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.utils.httprequests.ScrabbleHttpClient
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

abstract class SocketEvent(open val eventName: String)
abstract class OnEvent(override val eventName: String) : SocketEvent(eventName)
abstract class EmitEvent(override val eventName: String) : SocketEvent(eventName)

private const val URL = BuildConfig.COMMUNICATION_URL

abstract class SocketHandler(private val EventTypes: Map<SocketEvent, Class<out Any>>) {

    abstract val name: String
    abstract val path: String

    protected lateinit var socket: Socket

    @Synchronized
    fun setSocket() {
        val opts = IO.Options()
        opts.path = path
        val cookie = ScrabbleHttpClient.getAuthCookie()

        val headers: MutableMap<String, List<String>> = mutableMapOf()
        headers["Cookie"] = listOf(cookie.toString())

        opts.extraHeaders = headers
        try {
            opts.transports = arrayOf(WebSocket.NAME)
            socket = IO.socket(URL, opts)
            socket.on(Socket.EVENT_CONNECT) { onEvent(Socket.EVENT_CONNECT) }
            socket.on(Socket.EVENT_DISCONNECT) { onEvent(Socket.EVENT_DISCONNECT) }
            socket.on(Socket.EVENT_CONNECT_ERROR) { onEvent(Socket.EVENT_CONNECT_ERROR) }
        } catch (e: Throwable) {
            println("$name Error : $e")
        }
    }

    open fun onEvent(event: String) {
        println("$name $event")
    }

    @Synchronized
    fun ensureConnection() {
        if (socket.connected()) {
            return
        }
        socket.connect()
    }

    @Synchronized
    fun disconnect() {
        socket.disconnect()
    }

    @JvmName("onObject") // To prevent platform declaration clash
    @OptIn(DelicateCoroutinesApi::class)
    @Synchronized
    fun <T> on(event: OnEvent, callback: (formattedContent: T?) -> Unit) {
        socket.on(event.eventName) { args ->
            val contentType = EventTypes[event]!!
            val formattedContent = formatObjectResponse(args, contentType as Class<T>)
            try {
                GlobalScope.launch(Dispatchers.Main) {
                    callback(formattedContent)
                }
            } catch (e: Exception) {
                println("SocketHandler -> onObject -> Error")
                e.printStackTrace()
            }
        }
    }

    @JvmName("onArray") // To prevent platform declaration clash
    @OptIn(DelicateCoroutinesApi::class)
    @Synchronized
    fun <T> on(event: OnEvent, callback: (formattedContent: List<T>?) -> Unit) {
        socket.on(event.eventName) { args ->
            val contentType = EventTypes[event]
            val formattedContent = formatArrayResponse(args, contentType as Class<Array<T>>)
            try {
                GlobalScope.launch(Dispatchers.Main) {
                    callback(formattedContent)
                }
            } catch (e: Exception) {
                println("SocketHandler -> onArray -> Error")
                e.printStackTrace()
            }
        }
    }

    @Synchronized
    fun <T> emit(event: EmitEvent, content: T) {
        val contentType = EventTypes[event] as Class<T>
        val formattedContent: Any? =
            if (isContentPrimitive(content)) {
                content
            } else {
                formatRequest(content, contentType)
            }
        socket.emit(event.eventName, formattedContent)
    }

    private fun <T> isContentPrimitive(content: T): Boolean {
        return content is String || content is Int || content is Boolean
    }

    private fun <T> formatArrayResponse(args: Array<Any>, type: Class<Array<T>>): List<T>? {
        return try {
            Gson().fromJson(args[0].toString(), type).toList()
        } catch (e: Exception) {
            println("Error -> formatArrayResponse -> $type")
            e.printStackTrace()
            null
        }
    }

    private fun <T> formatObjectResponse(args: Array<Any>, type: Class<T>): T? {
        return try {
            Gson().fromJson(args[0].toString(), type) as T
        } catch (e: Exception) {
            println("Error -> formatObjectResponse -> $type")
            e.printStackTrace()
            null
        }
    }

    private fun <T> formatRequest(content: T, type: Class<T>): JSONObject? {
        return try {
            JSONObject(Gson().toJson(content))
        } catch (e: Exception) {
            println("Error -> formatRequest -> $type")
            e.printStackTrace()
            null
        }
    }
}
