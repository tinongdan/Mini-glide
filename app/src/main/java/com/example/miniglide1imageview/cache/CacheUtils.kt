package com.example.miniglide1imageview.cache

import java.security.MessageDigest

fun hashKeyForUrl(url: String): String {
    val md5 = MessageDigest.getInstance("MD5").digest(url.toByteArray())
    return md5.joinToString("") { "%02x".format(it) }
}