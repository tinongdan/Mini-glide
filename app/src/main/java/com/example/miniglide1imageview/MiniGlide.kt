package com.example.miniglide1imageview

import android.content.Context

object MiniGlide {
    fun with(context: Context): RequestManager {
        return RequestManager(context)
    }
}