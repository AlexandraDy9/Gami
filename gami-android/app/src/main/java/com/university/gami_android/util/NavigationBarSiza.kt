package com.university.gami_android.util

import android.content.res.Resources


fun getNavigationBarSize(resources: Resources): Int {
    if (!hasNavBar(resources))
        return 0
    val id: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    if (id > 0) {
        return resources.getDimensionPixelSize(id)
    }
    return 0
}

private fun hasNavBar(resources: Resources): Boolean {
    val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
    return id > 0 && resources.getBoolean(id)
}