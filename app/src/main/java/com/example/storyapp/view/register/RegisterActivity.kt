package com.example.storyapp.view.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.login.LoginActivity
import com.example.storyapp.view.main.dataStore
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this, dataStore)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        registerViewModel.snackbarText.observe(this) {
            it.getContentIfNotHandled()?.let { snackBarText ->
                Snackbar.make(
                    window.decorView.rootView,
                    snackBarText,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        registerViewModel.isRegister.observe(this) {
            it.getContentIfNotHandled()?.let { isRegister ->
                if (isRegister) {
                    AlertDialog.Builder(this).apply {
                        setTitle("Noice")
                        setMessage("Akunnya sudah jadi nih. Yuk, login dan berbagi cerita kamu!")
                        setPositiveButton("Lanjut") { _, _ ->
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun setupAction() {
        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                name.isEmpty() -> {
                    binding.nameEditTextLayout.error = "Masukkan email"
                }
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = "Masukkan email"
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = "Masukkan password"
                }
                else -> {
                    registerViewModel.postRegister(name, email, password)
                }
            }
        }

        binding.loginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}