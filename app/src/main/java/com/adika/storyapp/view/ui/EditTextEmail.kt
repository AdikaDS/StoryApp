package com.adika.storyapp.view.ui

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.adika.storyapp.R

class EditTextEmail : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        background = ResourcesCompat.getDrawable(resources, R.drawable.bg_text_field, null)
        isSingleLine = true
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val errorMessage = when {
                    s.isNullOrEmpty() -> resources.getString(R.string.email_warning_empty)
                    !Patterns.EMAIL_ADDRESS.matcher(s)
                        .matches() -> resources.getString(R.string.email_warning_invalid)

                    else -> null
                }

                if (errorMessage != null) {
                    setError(errorMessage, null)
                } else {
                    setError(null, null)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }
}