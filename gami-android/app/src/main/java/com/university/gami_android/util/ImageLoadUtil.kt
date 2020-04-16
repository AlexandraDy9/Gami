package com.university.gami_android.util

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide


fun ImageView.load(context: Context, image:Int){
    Glide.with(context).load(image).into(this)
}
