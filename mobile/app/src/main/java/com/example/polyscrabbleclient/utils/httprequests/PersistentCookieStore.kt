package com.example.polyscrabbleclient.utils.httprequests

import android.app.Activity
import android.content.SharedPreferences
import java.net.CookieStore
import java.net.HttpCookie
import java.net.URI

class PersistentCookieStore(activity: Activity): CookieStore {

    private val uriCookies: MutableMap<String, MutableList<HttpCookie>> = HashMap()

    private val spePreferences = activity.getSharedPreferences("CookiePrefsFile", 0)
    init {
        val prefsMap: MutableMap<String, *> = spePreferences.getAll()
        for ((key, value) in prefsMap.entries) {
            for (cookie in value as HashSet<*>) {
                val cookies = uriCookies[key] ?: mutableListOf()
                cookies.addAll(HttpCookie.parse(cookie as String))
                uriCookies[key] = cookies
            }
        }
    }

    override fun add(uri: URI, cookie: HttpCookie) {
        val uriString = uri.host
        val cookies: MutableList<HttpCookie> = uriCookies[uriString] ?: ArrayList();
        cookies.add(cookie)
        uriCookies[uriString] = cookies

        val ediWriter: SharedPreferences.Editor = spePreferences.edit()
        val setCookies = setOf(cookie.toString())

        ediWriter.putStringSet(
            uriString,
            spePreferences.getStringSet(uriString, setCookies)
        )
        ediWriter.commit()
    }

    override fun get(uri: URI): List<HttpCookie> {
        val uriStr = uri.host
        val cookies = uriCookies[uriStr]
        return cookies ?: ArrayList()
    }

    override fun removeAll(): Boolean {
        val ediWriter: SharedPreferences.Editor = spePreferences.edit()
        ediWriter.clear()
        val isClearedFromDisk = ediWriter.commit()
        if (!isClearedFromDisk) {
            return false
        }
        uriCookies.clear()
        return true
    }

    override fun getCookies(): List<HttpCookie> {
        val splitCookies = uriCookies.values
        val allCookies = mutableListOf<HttpCookie>()
        for (cookies in splitCookies) {
            allCookies.addAll(cookies)
        }
        return allCookies
    }

    override fun getURIs(): List<URI> {
        return uriCookies.keys.map { s -> URI(s) }
    }

    override fun remove(uri: URI, cookie: HttpCookie): Boolean {
        val uriStr = uri.host
        val cookies = uriCookies[uriStr]
        val ediWriter: SharedPreferences.Editor = spePreferences.edit()
        ediWriter.remove(uriStr)
        ediWriter.commit()
        return cookies?.remove(cookie) ?: false
    }
}
