package com.example.polyscrabbleclient.utils

import com.google.gson.Gson
import io.socket.client.Socket
import org.json.JSONObject

abstract class SocketEvent (open val eventName: String)
abstract class OnEvent (override val eventName: String) : SocketEvent(eventName)
abstract class EmitEvent (override val eventName: String) : SocketEvent(eventName)

abstract class SocketHandler(private val EventTypes: Map<SocketEvent, Class<out Any>>) {

    abstract fun setSocket()
    abstract fun ensureConnection()
    abstract fun disconnect()
    protected lateinit var socket: Socket

    @JvmName("onObject") // To prevent platform declaration clash
    @Synchronized
    fun <T> on(event: OnEvent, callback: (formattedContent: T?) -> Unit) {
        socket.on(event.eventName) { args ->
            val contentType = EventTypes[event]!!
            val formattedContent = formatObjectResponse(args, contentType as Class<T>)
            callback(formattedContent)
        }
    }

    @JvmName("onArray") // To prevent platform declaration clash
    @Synchronized
    fun <T> on(event: OnEvent, callback: (formattedContent: List<T>?) -> Unit) {
        socket.on(event.eventName) { args ->
            val contentType = EventTypes[event]
            val formattedContent = formatArrayResponse(args, contentType as Class<Array<T>>)
            callback(formattedContent)
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
