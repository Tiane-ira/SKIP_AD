package com.android.skip.manager

import android.content.Context
import android.graphics.Rect

object RectManager {
    var maxRectX = 0
    var maxRectY = 0

    fun setMaxRect(context: Context) {
        val metrics = context.resources.displayMetrics
        maxRectX = metrics.widthPixels
        maxRectY = metrics.heightPixels
    }

    fun getPointRect(percentX: Float, percentY: Float): Rect {
        val rect = Rect()
        val actualX = (percentX * maxRectX).toInt()
        val actualY = (percentY * maxRectY).toInt()
        rect.set(actualX - 1, actualY - 1, actualX + 1, actualY + 1)
        return rect
    }

    fun getMaxRect(): String {
        return "${maxRectX}x${maxRectY}"
    }
}
