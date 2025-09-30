package com.example.miniglide1imageview

import android.widget.ImageView
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw

class RequestBuilder(private val requestManager: RequestManager, private val url: String) {
    fun into(imageView: ImageView) {
        imageView.doOnLayout {
            requestManager.loadImage(url, imageView)
        }
    }
}