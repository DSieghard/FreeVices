package com.sgtech.freevices.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
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

        signInButton.setOnClickListener {
            /*if (emailEditText.toString().isNotEmpty() && passwordEditText.toString().isNotEmpty()) {
                FirebaseUtils.signInWithEmail(this, emailEditText.toString(), passwordEditText.toString(), true)
            }*/
            startActivity(Intent(this, MainActivity::class.java))
        }

        createAccountButton.setOnClickListener{
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }
}