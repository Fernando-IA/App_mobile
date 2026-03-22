package com.fernando.appmobile.terms

import android.content.Context
import android.util.AttributeSet

class ScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : android.widget.ScrollView(context, attrs) {

    private var onBottomReachedChanged: ((Boolean) -> Unit)? = null

    init {
        setOnScrollChangeListener { view, _, scrollY, _, _ ->
            val child = getChildAt(0) ?: return@setOnScrollChangeListener
            val reachedBottom = scrollY + view.height >= child.measuredHeight
            onBottomReachedChanged?.invoke(reachedBottom)
        }
    }

    fun setOnBottomReachedChanged(listener: (Boolean) -> Unit) {
        onBottomReachedChanged = listener
        post { notifyCurrentState() }
    }

    private fun notifyCurrentState() {
        val child = getChildAt(0) ?: return
        val reachedBottom = scrollY + height >= child.measuredHeight
        onBottomReachedChanged?.invoke(reachedBottom)
    }
}
