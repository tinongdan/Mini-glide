package com.example.miniglide1imageview

import android.util.Log
import android.widget.ImageView
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw

class RequestBuilder(private val requestManager: RequestManager, private val url: String) {
    fun into(imageView: ImageView) {
        if (requestManager.canLoad(imageView)) {
            requestManager.loadImage(url, imageView)
        } else {
            imageView.doOnPreDraw {
                requestManager.loadImage(url, imageView)
            }
        }
    }
}