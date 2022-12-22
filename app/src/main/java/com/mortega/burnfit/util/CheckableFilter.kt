package com.mortega.burnfit.util

import android.content.Context
import android.widget.TextView
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.Checkable
import androidx.cardview.widget.CardView
import com.mortega.burnfit.R


class CheckableFilter : CardView, Checkable {

    private var isChecked: Boolean = false

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.checkable_filter, this, true)

        isClickable = true
        setChecked(false)

        setCardBackgroundColor(ContextCompat.getColorStateList(context, R.color.selector_card_view_background))

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CheckableFilter, 0, 0)
            try {
                val text = ta.getString(R.styleable.CheckableGender_card_text)
                val itemText = findViewById<View>(R.id.text) as TextView

                if (text != null) {
                    itemText.text = text
                }

            } finally {
                ta.recycle()
            }
        }
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked()) {
            View.mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }

    override fun setChecked(checked: Boolean) {
        this.isChecked = checked
    }

    override fun isChecked(): Boolean {
        return isChecked
    }

    override fun toggle() {
        setChecked(!this.isChecked)
    }

    companion object {

        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }
}