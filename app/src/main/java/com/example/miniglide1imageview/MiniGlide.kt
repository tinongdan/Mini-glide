package com.example.miniglide1imageview

import android.content.Context

object MiniGlide {
    private val requestManagers = mutableMapOf<Context, RequestManager>()
    fun with(context: Context): RequestManager {
        return requestManagers.getOrPut(context) { RequestManager(context) }
    }
}