package com.example.miniglide1imageview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min

class RequestManager(private val context: Context) {
    private val client = OkHttpClient()
    private val imageViewHashSet = mutableSetOf<Int>()

    fun load(url: String): RequestBuilder {
        return RequestBuilder(this, url)
    }

    fun loadImage(url: String, imageView: ImageView) {
        Log.d("8888", "Load url $url")
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = downloadAndDecodeImage(url, imageView)
            if (bitmap != null) {
                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(bitmap)
                }
            }
        }
        imageViewHashSet.add(imageView.hashCode())
    }

    /*
        If imageView has already been created (with specified width & height),
        then it doesn't have to wait for doOnPreDraw.
     */
    fun canLoad(imageView: ImageView): Boolean {
        return imageViewHashSet.contains(imageView.hashCode())
    }

    fun downloadAndDecodeImage(url: String, imageView: ImageView): Bitmap? {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val bytes = response.body().bytes()

        val options = BitmapFactory.Options()

        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

        val reqWidth = if (imageView.layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            min(options.outWidth, context.resources.displayMetrics.widthPixels)
        } else {
            imageView.width
        }
        val reqHeight = if (imageView.layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            min(options.outHeight, context.resources.displayMetrics.heightPixels)
        } else {
            imageView.height
        }

        options.inSampleSize = calculateInSampleSize(options, reqWidth = reqWidth, reqHeight = reqHeight)

        options.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    }
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    // Raw height and width of image
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}