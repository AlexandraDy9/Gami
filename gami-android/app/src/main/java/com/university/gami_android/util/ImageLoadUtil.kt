package com.university.gami_android.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide


fun ImageView.load(context: Context, image:Int){
    Glide.with(context).load(image).into(this)
}
