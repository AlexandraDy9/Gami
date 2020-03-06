package com.university.gami_android.util

import java.text.SimpleDateFormat

fun String.formatDate(sourceFormat: String, resultFormat: String): String {
    val parser = SimpleDateFormat(sourceFormat)
    val formatter = SimpleDateFormat(resultFormat)
    val output = formatter.format(parser.parse(this))
    return output
}