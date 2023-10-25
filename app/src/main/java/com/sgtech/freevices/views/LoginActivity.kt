package com.sgtech.freevices.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import com.sgtech.freevices.R
import com.sgtech.freevices.utils.FirebaseUtils

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signInButton = findViewById<Button>(R.id.signUpButton)
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        val emailEditText = findViewById<TextInputEditText>(R.id.signInEmailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.signInPasswordEditText)

        passwordEditText.setOnEditorActionListener() { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signInButton.performClick()
                true
            } else {
                false
            }
        }

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            Log.d("FirebaseUtils", "$email $password")
            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseUtils.signInWithEmail(this, email, password)
            }
        }

        createAccountButton.setOnClickListener{
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }
}