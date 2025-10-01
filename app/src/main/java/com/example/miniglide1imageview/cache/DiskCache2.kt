package com.example.miniglide1imageview.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.jakewharton.disklrucache.DiskLruCache
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


private const val DISK_CACHE_SIZE = 1024 * 1024 * 500L       // 500 MB
private const val DISK_CACHE_SUBDIR = "mini_glide_disk_cache"
private const val APP_VERSION = 1
private const val VALUE_COUNT = 1
private const val COMPRESS_QUALITY = 100
private val COMPRESS_FORMAT = Bitmap.CompressFormat.PNG



class DiskCache2(context: Context) {
    private var diskLruCache: DiskLruCache? = null
    private val diskCacheLock = ReentrantLock()
    private val diskCacheLockCondition: Condition = diskCacheLock.newCondition()
    private var diskCacheStarting = true

    init {
        val cacheDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR)
        initDiskCache(cacheDir)
    }

    fun getDiskCacheDir(context: Context, uniqueName: String): File {
        val cachePath = context.cacheDir.path
        return File(cachePath + File.separator + uniqueName)
    }

    fun initDiskCache(cacheDir: File) {
        diskCacheLock.withLock {
            try {
                diskLruCache = DiskLruCache.open(cacheDir, APP_VERSION, VALUE_COUNT, DISK_CACHE_SIZE)
                diskCacheStarting = false
                diskCacheLockCondition.signalAll()
            } catch (e: Exception) {
                Log.d("8888", "Failed to initialize disk cache.")
            }
        }
    }

    fun get(url: String): Bitmap? {
        diskCacheLock.withLock {
            while (diskCacheStarting) {
                try {
                    diskCacheLockCondition.await()
                } catch (e: InterruptedException) {

                }
            }
            val key = hashKeyForUrl(url)
            return try {
                diskLruCache?.get(key)?.let { snapshot ->
                    snapshot.getInputStream(0).use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                }
            } catch (e: Exception) {
                Log.d("8888", "Failed to read from disk cache.")
                null
            }
        }
    }

    fun put(url: String, bitmap: Bitmap) {
        diskCacheLock.withLock {
            while (diskCacheStarting) {
                try {
                    diskCacheLockCondition.await()
                } catch (e: InterruptedException) {

                }
            }
            val key = hashKeyForUrl(url)
            // if not contain key, then put
            try {
                if (diskLruCache?.get(key) == null) {
                    diskLruCache?.edit(key)?.let { editor ->
                        editor.newOutputStream(0).use { outputStream ->
                            bitmap.compress(COMPRESS_FORMAT, COMPRESS_QUALITY, outputStream)
                        }
                        editor.commit()
                    }
                    Log.d("8888", "Added $url - $key to Disk cache")
                }
            } catch (e: Exception) {
                Log.d("8888", "Failed to write to disk cache.")
            }
        }
    }

    private fun hashKeyForUrl(url: String): String {
        val md5 = MessageDigest.getInstance("MD5").digest(url.toByteArray())
        return md5.joinToString("") { "%02x".format(it) }
    }
}
