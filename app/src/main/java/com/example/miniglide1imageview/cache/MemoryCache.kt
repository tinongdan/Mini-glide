package com.example.miniglide1imageview.cache

import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache

class MemoryCache {
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()   // 192 MB
    private val cacheSize = maxMemory / 1   // 64 MB
    private val memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    fun get(url: String): Bitmap? {
        val key = hashKeyForUrl(url)
        val result = memoryCache.get(key)
//        Log.d("8888", "Get bitmap from MEMORY for $url - $key -> ${result != null}")
        return result
    }

    fun put(url: String, bitmap: Bitmap) {
        val key = hashKeyForUrl(url)
//        Log.d("8888", "Put bitmap to MEMORY for $url - $key")
        memoryCache.put(key, bitmap)
    }

    fun debug() {
        Log.d("8888", "Memory cache size: ${memoryCache.size()}")
    }
}