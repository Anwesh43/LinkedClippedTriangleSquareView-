package com.anwesh.uiprojects.clippedtrianglesquareview

/**
 * Created by anweshmishra on 24/10/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Path
import android.content.Context
import android.app.Activity

val colors : Array<Int> = arrayOf(
        "#F44336",
        "#3F51B5",
        "#FF9800",
        "#2196F3",
        "#4CAF50"
).map {
    Color.parseColor(it)
}.toTypedArray()
val parts : Int = 8
val scGap : Float = 0.02f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 3.7f
val delay : Long = 20
val backColor : Int = Color.parseColor("#BDBDBD")
