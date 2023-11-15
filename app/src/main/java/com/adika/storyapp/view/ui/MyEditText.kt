package com.adika.storyapp.view.ui

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.adika.storyapp.R

class MyEditText : AppCompatEditText {
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
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                when (id) {
                    R.id.nameEditText -> validateName(s.toString())
                    R.id.emailEditText -> validateEmail(s.toString())
                    R.id.passwordEditText -> validatePassword(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun validateName(text: String) {
        val errorMessage = when {
            text.isNullOrEmpty() -> resources.getString(R.string.name_warning_empty)
            else -> null
        }

        if (errorMessage != null) {
            error = errorMessage
        } else {
            error = null
        }
    }

    private fun validateEmail(text: String) {
        val errorMessage = when {
            text.isNullOrEmpty() -> resources.getString(R.string.email_warning_empty)
            !Patterns.EMAIL_ADDRESS.matcher(text)
                .matches() -> resources.getString(R.string.email_warning_invalid)

            else -> null
        }

        if (errorMessage != null) {
            error = errorMessage
        } else {
            error = null
        }
    }

    private fun validatePassword (text: String) {
        val errorMessage = when {
            text.isNullOrEmpty() -> resources.getString(R.string.password_warning_empty)
            text.length < 8 -> resources.getString(R.string.password_warning_length)
            !isValidPassword(text.toString()) ->  resources.getString(R.string.password_warning_unique)
            else -> null
        }

        if (errorMessage != null) {
            error = errorMessage
        } else {
            error = null
        }
    }
    private fun isValidPassword(password: String): Boolean {
        val upperCase = password.any { it.isUpperCase() }
        val specialChar = password.any { it.isLetterOrDigit().not() }
        val digit = password.any { it.isDigit() }

        return upperCase && specialChar && digit
    }
}