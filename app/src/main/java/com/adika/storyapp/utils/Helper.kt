package com.adika.storyapp.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun String.withDateFormat(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val date = inputFormat.parse(this)

    val outputFormat = SimpleDateFormat("E, dd MMMM yyyy HH:mm", Locale.US)

    return outputFormat.format(date!!)
}