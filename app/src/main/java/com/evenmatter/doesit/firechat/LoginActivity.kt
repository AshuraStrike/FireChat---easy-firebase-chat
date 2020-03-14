package com.evenmatter.doesit.firechat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setToolbar("Login")

        mAuth = FirebaseAuth.getInstance()

        val register = findViewById(R.id.register) as TextView
        val login = findViewById(R.id.loginButton) as Button
        val email = findViewById(R.id.textEditMail) as EditText
        val password = findViewById(R.id.textEditPassword) as EditText

        register.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
        login.setOnClickListener{
            signIn(email.text.toString(),password.text.toString())
        }
    }

    private fun setToolbar(title: String){
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
    }

    private fun signIn(email: String, password: String){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (!task.isSuccessful) {
                Toast.makeText(this, "eMail/Password error",Toast.LENGTH_SHORT).show()
            }else{
                if(mAuth.currentUser?.isEmailVerified == true){
                    Toast.makeText(this,"Sign in Success!",Toast.LENGTH_SHORT).show()
                    finish()
                }else{
                    mAuth.currentUser?.sendEmailVerification()
                    Toast.makeText(this,"eMail not verified",Toast.LENGTH_SHORT).show()
                    mAuth.signOut()
                }
            }
        }
    }

}