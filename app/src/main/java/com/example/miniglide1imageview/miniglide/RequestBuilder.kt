package com.example.miniglide1imageview.miniglide

import android.widget.ImageView
import androidx.core.view.doOnPreDraw

class RequestBuilder(private val requestManager: RequestManager, private val url: String) {
    fun into(imageView: ImageView) {
        requestManager.clear(imageView)
        if (requestManager.canLoad(imageView)) {
            requestManager.loadImage(url, imageView)
        } else {
            imageView.doOnPreDraw {
                requestManager.loadImage(url, imageView)
            }
        }
    }
}