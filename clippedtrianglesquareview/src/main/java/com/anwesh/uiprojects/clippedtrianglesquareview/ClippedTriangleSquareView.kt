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
val deg : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawTriangleSquare(sf : Float, size : Float, paint : Paint) {
    val sf1 : Float = sf.divideScale(0, parts)
    val sf2 : Float = sf.divideScale(1, parts)
    val sf3 : Float = sf.divideScale(2, parts)
    val sf4 : Float = sf.divideScale(3, parts)
    val sf5 : Float = sf.divideScale(4, parts)
    drawLine(-size / 2, 0f, -size / 2 + size * 0.5f * sf1, size * 0.5f * sf1, paint)
    drawLine(0f, size / 2, size * 0.5f * sf2, size / 2 - size * 0.5f * sf2, paint)
    drawLine(size / 2, 0f, size / 2, -size * 0.5f * sf3, paint)
    drawLine(size / 2, -size * 0.5f, size / 2 - size * sf4, -size * 0.5f, paint)
    drawLine(-size / 2, -size / 2, size / 2, size * 0.5f * (sf5 - 1), paint)
}

fun Canvas.drawTriangleSquareFillPath(sf6 : Float, size : Float, paint : Paint) {
    save()
    val path : Path = Path()
    path.moveTo(-size / 2, 0f)
    path.lineTo(0f, size / 2)
    path.lineTo(size / 2, 0f)
    path.lineTo(size / 2, -size / 2)
    path.lineTo(-size / 2, -size / 2)
    path.lineTo(-size / 2, 0f)
    clipPath(path)
    drawRect(RectF(-size / 2, size / 2 - size * sf6, size / 2, size / 2), paint)
    restore()
}

fun Canvas.drawClippedTriangleSquare(scale : Float, w : Float, h : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val sf6 : Float = sf.divideScale(5, parts)
    val sf7 : Float = sf.divideScale(6, parts)
    val size : Float = Math.min(w, h) / sizeFactor
    save()
    translate(w / 2, h / 2)
    rotate(deg * sf7)
    drawTriangleSquare(sf, size, paint)
    drawTriangleSquareFillPath(sf6, size, paint)
    restore()
}

fun Canvas.drawCTSNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawClippedTriangleSquare(scale, w, h, paint)
}

class ClippedTriangleSquareView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class CTSNode(var i : Int, val state : State = State()) {

        private var next : CTSNode? = null
        private var prev : CTSNode? = null

        init {

        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = CTSNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawCTSNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : CTSNode {
            var curr : CTSNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }
}
