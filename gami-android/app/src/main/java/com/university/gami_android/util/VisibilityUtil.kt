package com.university.gami_android.util

import android.view.View

fun View.goneUnless(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}