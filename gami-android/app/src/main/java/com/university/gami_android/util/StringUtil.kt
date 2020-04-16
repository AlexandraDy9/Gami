package com.university.gami_android.util

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("SimpleDateFormat")
fun String.formatDate(sourceFormat: String, resultFormat: String): String {
    val parser = SimpleDateFormat(sourceFormat)
    val formatter = SimpleDateFormat(resultFormat)
    return formatter.format(parser.parse(this)!!)
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatOnlyDate(value: String): String {
    val formatterDate = DateTimeFormatter.ofPattern("dd, MMM, yyyy")
    val localDateTime = LocalDateTime.parse(value)

    return formatterDate.format(localDateTime)
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatOnlyHour(value: String): String {
    val formatterHour = DateTimeFormatter.ofPattern("HH:mm")
    val localDateTime = LocalDateTime.parse(value)

    return formatterHour.format(localDateTime)
}