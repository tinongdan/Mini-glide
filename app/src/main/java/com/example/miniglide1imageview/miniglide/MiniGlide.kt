package com.example.miniglide1imageview.miniglide

import android.content.Context
import com.example.miniglide1imageview.miniglide.RequestManager

object MiniGlide {
    private val requestManagers = mutableMapOf<Context, RequestManager>()
    fun with(context: Context): RequestManager {
        return requestManagers.getOrPut(context) { RequestManager(context) }
    }
}