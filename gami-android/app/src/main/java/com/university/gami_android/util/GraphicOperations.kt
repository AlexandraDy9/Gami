package com.university.gami_android.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat


fun getBitmapFromDrawable(context: Context, drawableId: Int, colorId: Int = 0): Bitmap {
    val drawable = AppCompatResources.getDrawable(context, drawableId)

    if (drawable is BitmapDrawable)
        return drawable.bitmap
    if (drawable is VectorDrawableCompat || drawable is VectorDrawable) {
        if (colorId != 0)
            drawable.setTint(context.resources.getColor(colorId))
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
//        var paint = Paint()
//        paint.setColorFilter(PorterDuffColorFilter(context.resources.getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN))
//        canvas.drawColor(context.resources.getColor(R.color.colorPrimary))
        drawable.draw(canvas)

        return bitmap
    } else {
        throw IllegalArgumentException("unsupported drawable type")
    }
}

fun scaleBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
    var scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
    var scaleX: Double = newWidth / bitmap.width.toDouble()
    var scaleY: Double = newHeight / bitmap.height.toDouble()
    var pivotX: Double = 0.0
    var pivotY: Double = 0.0

    var scaleMatrix = Matrix()
    scaleMatrix.setScale(scaleX.toFloat(), scaleY.toFloat(), pivotX.toFloat(), pivotY.toFloat());

    var canvas = Canvas(scaledBitmap);
    canvas.setMatrix(scaleMatrix);
    canvas.drawBitmap(bitmap, 0.0f, 0.0f, Paint(Paint.FILTER_BITMAP_FLAG));

    return scaledBitmap;
}

fun mergeBitmaps(
    background: Bitmap,
    foreground: Bitmap,
    horizontalOffset: Int = 0,
    verticalOffset: Int = 0
): Bitmap {
    val result = Bitmap.createBitmap(background.width, background.height, background.config)
    val canvas = Canvas(result)
    val widthBack = background.width
    val widthFront = foreground.width
    val move = ((widthBack - widthFront) / 2).toFloat()
    canvas.drawBitmap(background, 0f, 0f, null)
    canvas.drawBitmap(foreground, move, move + verticalOffset, null)
    return result
}
