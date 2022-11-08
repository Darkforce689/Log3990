package com.example.polyscrabbleclient.message.model

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

enum class MessageType {SYSTEM, ME, OTHER}
data class Message(val content: String, val from: String, val type: MessageType, val date: ZonedDateTime?, val conversation: String)
data class MessageDTO(val content: String, val from: String,  val date: String?, val conversation: String)


fun messageDTOToMessage(messageDTO: MessageDTO, type: MessageType, userName: String): Message {
    fun parseDate(dateStr: String): ZonedDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSX")
        return ZonedDateTime.parse(dateStr.replace('T',' '), formatter)
            .withZoneSameInstant(TimeZone.getDefault().toZoneId())
    }
    val dateStr = messageDTO.date
    val date: ZonedDateTime? = if (dateStr !== null) parseDate(dateStr) else null
    return Message(messageDTO.content, userName, type, date, messageDTO.conversation)
}
