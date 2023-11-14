package com.adika.storyapp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.adika.storyapp.R
import com.adika.storyapp.databinding.ActivitySignupBinding
import com.adika.storyapp.view.UserModelFactory
import com.adika.storyapp.view.login.LoginActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val viewModel by viewModels<SignupViewModel> {
        UserModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()
        checkEditText()
        signup()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun signup() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.register(name, email, password)
        }
            viewModel.status.observe(this) { isSuccess ->
                if (isSuccess) {
                    AlertDialog.Builder(this).apply {
                        setTitle("Yeah!")
                        setMessage("Akun sudah jadi nih. Yuk, login dan cobalah buat story.")
                        setPositiveButton("Lanjut") { _, _ ->
                            val intent = Intent(context, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }

                } else {
                    viewModel.error.observe(this) { errorMessage ->
                        if (errorMessage != null) {
                            Toast.makeText(this, "$errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            viewModel.loading.observe(this) { loading ->
                showLoading(loading)
            }

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(250)
        val tvName =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(250)
        val etName =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val tvEmail = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(250)
        val etEmail =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val tvPassword =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(250)
        val etPassword =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                title,
                tvName,
                etName,
                tvEmail,
                etEmail,
                tvPassword,
                etPassword,
                signup
            )
            startDelay = 100
        }.start()
    }

    private fun checkEditText() {
        binding.nameEditText. addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val errorMessage = when {
                    s.isNullOrEmpty() -> resources.getString(R.string.email_warning_empty)
                    else -> null
                }

                if (errorMessage != null) {
                    binding.nameEditTextLayout.error = errorMessage
                } else {
                    binding.nameEditTextLayout.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.emailEditText. addTextChangedListener(object : TextWatcher {
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
                    binding.emailEditTextLayout.error = errorMessage
                } else {
                    binding.emailEditTextLayout.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.passwordEditText. addTextChangedListener(object : TextWatcher {
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
                    binding.passwordEditTextLayout.error = errorMessage
                } else {
                    binding.passwordEditTextLayout.error = null
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

    private fun showLoading(isLoading: Boolean) { binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE }
}