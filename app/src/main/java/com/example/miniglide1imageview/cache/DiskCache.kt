package com.example.miniglide1imageview.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.security.MessageDigest

private const val DISK_CACHE_SIZE = 1024 * 1024 * 500L       // 500 MB
private const val DISK_CACHE_SUBDIR = "mini_glide_disk_cache"
private const val COMPRESS_QUALITY = 100
private val COMPRESS_FORMAT = Bitmap.CompressFormat.PNG


class DiskCache(context: Context) {
    private val cacheDir = File(context.cacheDir, DISK_CACHE_SUBDIR).apply { mkdirs() }

    fun put(url: String, bitmap: Bitmap) {
        val key = hashKeyForUrl(url)
        val file = File(cacheDir, key)
        file.outputStream().use { outputStream ->
            bitmap.compress(COMPRESS_FORMAT, COMPRESS_QUALITY, outputStream)
        }
        Log.d("11111", "Add $url - $key to ${file.absolutePath} (Exist: ${file.exists()}, Length: ${file.length()})")
        trimToSize()
    }

    fun get(url: String): Bitmap? {
        val key = hashKeyForUrl(url)
        val file = File(cacheDir, key)
        if (!file.exists()) return null

        file.setLastModified(System.currentTimeMillis())
        return file.inputStream().use { inputStream ->
            Log.d("11111", "Get $url - $key from ${file.absolutePath}")
            BitmapFactory.decodeStream(inputStream)
        }
    }

    private fun trimToSize() {
        var totalSize = cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
        if (totalSize <= DISK_CACHE_SIZE) return

        val files = cacheDir.listFiles()?.sortedBy { it.lastModified() } ?: return
        for (file in files) {
            if (totalSize <= DISK_CACHE_SIZE) break
            totalSize -= file.length()
            file.delete()
        }
    }
}