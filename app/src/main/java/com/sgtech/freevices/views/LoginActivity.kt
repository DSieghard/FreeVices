package com.sgtech.freevices.views

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sgtech.freevices.utils.FirebaseUtils.checkIfUserIsLoggedIn
import com.sgtech.freevices.views.ui.EmailEditText
import com.sgtech.freevices.views.ui.LoginButton
import com.sgtech.freevices.views.ui.LoginText
import com.sgtech.freevices.views.ui.PasswordEditText
import com.sgtech.freevices.views.ui.SignUpButton
import com.sgtech.freevices.views.ui.overview.ui.theme.FreeVicesTheme

class LoginActivity : AppCompatActivity() {




    override fun onStart() {
        super.onStart()
        checkIfUserIsLoggedIn(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            FreeVicesTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        LoginText()
                        Spacer(modifier = Modifier.size(240.dp))
                        EmailEditText(email) { newValue -> email = newValue }
                        Spacer(modifier = Modifier.size(48.dp))
                        PasswordEditText(password) { newValue -> password = newValue }
                    }
                    Spacer(modifier = Modifier.size(128.dp))
                    Column(modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Bottom)
                    {

                            LoginButton(email, password)
                            Spacer(modifier = Modifier.size(24.dp))
                            SignUpButton()

                    }
                }
            }
        }
    }
}