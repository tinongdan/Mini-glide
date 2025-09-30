package com.example.miniglide1imageview

import android.content.Context

class Datasource(val context: Context) {
    fun getUrlList(): Array<String> {
        return context.resources.getStringArray(R.array.url_array)
    }
}