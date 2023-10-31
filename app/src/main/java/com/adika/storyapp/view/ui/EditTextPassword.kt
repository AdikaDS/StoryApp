package com.adika.storyapp.view.ui

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.adika.storyapp.R

class EditTextPassword : AppCompatEditText {
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
        hint = resources.getString(R.string.password)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        background = ResourcesCompat.getDrawable(resources, R.drawable.bg_text_field, null)
        maxLines = 1
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
               val errorMessage = when {
                    s.isNullOrEmpty() -> resources.getString(R.string.password_warning_empty)
                    s.length < 8 -> resources.getString(R.string.password_warning_length)
                    !isValidPassword(s.toString()) ->  resources.getString(R.string.password_warning_unique)
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

    private fun isValidPassword(password: String): Boolean {
        val upperCase = password.any { it.isUpperCase() }
        val specialChar = password.any { it.isLetterOrDigit().not() }
        val digit = password.any { it.isDigit() }

        return upperCase && specialChar && digit
    }
}