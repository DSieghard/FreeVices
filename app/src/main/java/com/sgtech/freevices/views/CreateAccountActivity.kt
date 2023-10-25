package com.sgtech.freevices.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils
import com.sgtech.freevices.utils.FirebaseUtils.createAccount

class CreateAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val backButton = findViewById<Button>(R.id.backButton)
        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<TextInputEditText>(R.id.confirmPasswordEditText)
        val firstNameEditText = findViewById<TextInputEditText>(R.id.firstNameEditText)
        val lastNameEditText = findViewById<TextInputEditText>(R.id.lastNameEditText)
        val phoneEditText = findViewById<TextInputEditText>(R.id.phoneEditText)
        val usernameEditText = findViewById<TextInputEditText>(R.id.usernameEditText)

        phoneEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signUpButton.performClick()
                true
            } else {
                false
            }
        }

        signUpButton.setOnClickListener {

            fun String.toIntOrDefault(defaultValue: Int): Int {
                return try {
                    toInt()
                } catch (e: NumberFormatException) {
                    defaultValue
                }
            }

            val firstname = firstNameEditText.text.toString().trim()
            val lastname = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val phoneConvert = phoneEditText.text.toString().trim()
            val phone = phoneConvert.toIntOrDefault(0)

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && firstname.isNotEmpty() && lastname.isNotEmpty()) {
                if (password == confirmPassword) {
                    createAccount(this, email, password) {
                        FirebaseUtils.createDataOnFirestore(
                            firstname,
                            lastname,
                            username,
                            email,
                            phone
                        )
                    }
                } else {
                    Toast.makeText(this,
                        getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show()
                }
                Log.d("CreateAccountActivity", "createDataOnFirestore:success")
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}