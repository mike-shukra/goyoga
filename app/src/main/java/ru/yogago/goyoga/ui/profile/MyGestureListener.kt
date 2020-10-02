package ru.yogago.goyoga.ui.profile

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs


class MyGestureListener: GestureDetector.SimpleOnGestureListener() {

    companion object {
        private const val SWIPE_MIN_DISTANCE = 130
        private const val SWIPE_MAX_DISTANCE = 300
        private const val SWIPE_MIN_VELOCITY = 200
    }

    private var i = 1

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (abs(e1.y - e2.y) > SWIPE_MAX_DISTANCE) return false
        if (e2.x - e1.x > SWIPE_MIN_DISTANCE && abs(velocityX) > SWIPE_MIN_VELOCITY) {
            i++

        }
        return false
    }

}