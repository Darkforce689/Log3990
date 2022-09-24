package com.example.polyscrabbleclient.utils.httprequests

import com.example.polyscrabbleclient.BuildConfig
import com.example.polyscrabbleclient.Score
import com.google.gson.Gson
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.*
import java.nio.charset.Charset

object ScrabbleHttpClient {
    val cookieManager = CookieManager()
    init {
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        CookieHandler.setDefault(cookieManager)
    }

    private fun InputStream.readTextAndClose(charset: Charset = Charsets.UTF_8): String {
        return this.bufferedReader(charset).use { it.readText() }
    }

    fun getAuthCookie(): String? {
        val cookies = cookieManager.cookieStore.get(URI(BuildConfig.COMMUNICATION_URL))
        if (cookies.size == 0) {
            return null
        }
        return cookies[0].toString()
    }

    fun <T: Any> get(url: URL, returnType: Class<T>): T? {
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        return try {
            val urlConnectionBodyStream = if (urlConnection.responseCode == 200) urlConnection.inputStream else urlConnection.errorStream
            val inputStream: InputStream = BufferedInputStream(urlConnectionBodyStream)
            val body: String = inputStream.readTextAndClose()
            Gson().fromJson(body, returnType)
        } catch (e: Exception) {
            null
        } finally {
            urlConnection.disconnect()
        }
    }

    private fun <T: Any, U: Any> postPutRequest(url: URL, objectToSend: U, returnType: Class<T>, postOrPut: String): T? {
        val requestBody = Gson().toJson(objectToSend)
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = postOrPut
        urlConnection.setRequestProperty("Content-Type", "application/json")
        urlConnection.setRequestProperty("Accept", "application/json")
        return try {
            urlConnection.doOutput = true
            val os: OutputStream = urlConnection.outputStream
            os.write(requestBody.toByteArray())
            os.flush()
            os.close()

            val urlConnectionBodyStream = if (urlConnection.responseCode == 200) urlConnection.inputStream else urlConnection.errorStream
            val inputStream: InputStream = BufferedInputStream(urlConnectionBodyStream)
            val responseBody = inputStream.readTextAndClose()
            Gson().fromJson(responseBody, returnType)
        } catch (e: Exception) {
            null
        } finally {
            urlConnection.disconnect()
        }
    }

    fun <T: Any, U: Any> post(url: URL, body: U, returnType: Class<T>): T? {
        return postPutRequest(url, body, returnType, "POST")
    }

    fun <T: Any, U: Any> put(url: URL, body: U, returnType: Class<T>): T? {
        return postPutRequest(url, body, returnType, "PUT")
    }

    fun <T: Any> delete(url: URL, returnType: Class<T>): T? {
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "DELETE"
        return try {
            val urlConnectionBodyStream = if (urlConnection.responseCode == 200) urlConnection.inputStream else urlConnection.errorStream
            val inputStream: InputStream = BufferedInputStream(urlConnectionBodyStream)
            val body: String = inputStream.readTextAndClose()
            Gson().fromJson(body, returnType)
        } catch (e: Exception) {
            null
        } finally {
            urlConnection.disconnect()
        }
    }

}
