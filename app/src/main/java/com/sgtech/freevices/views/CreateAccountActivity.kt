package com.sgtech.freevices.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils

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


        signUpButton.setOnClickListener {
            val firstname = firstNameEditText.text.toString()
            val lastname = lastNameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val phoneConvert = phoneEditText.text.toString()
            val phone = phoneConvert.toInt()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && firstname.isNotEmpty() && lastname.isNotEmpty() && phoneConvert.isNotEmpty()) {
                if (password == confirmPassword) {
                    FirebaseUtils.createAccount(this, email, password, true)
                    try {
                        FirebaseUtils.createDataOnFirestore(this, firstname, lastname, email, phone)
                    } catch (e: Exception) {
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else {
                Toast.makeText(this, getString(R.string.error_field_incomplete), Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}