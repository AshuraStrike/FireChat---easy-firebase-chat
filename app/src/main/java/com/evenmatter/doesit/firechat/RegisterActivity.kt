package com.evenmatter.doesit.firechat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        setToolbar("Register")

        val register = findViewById(R.id.registerButton) as Button
        val nickName = findViewById(R.id.textEditNickname) as EditText
        val email = findViewById(R.id.textEditMail) as EditText
        val password = findViewById(R.id.textEditPassword) as EditText

        register.setOnClickListener {
            if(email.text.matches(Regex("[a-zA-Z0-9\\-_.]+@[a-zA-Z0-9]+\\.[a-z]{2,4}"))) {
                //Toast.makeText(this,"yes Yes YES!",Toast.LENGTH_SHORT).show()
                signUp(email.text.toString(), password.text.toString(), nickName.text.toString())
            }else{
                Toast.makeText(this,"Invalid eMail",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setToolbar(title: String){
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun signUp(email: String, password: String, nickName: String){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (!task.isSuccessful) {
                Toast.makeText(this,"An error has occurred", Toast.LENGTH_SHORT).show()
            }else{
                val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                val profileUpdates: UserProfileChangeRequest = UserProfileChangeRequest.Builder()
                        .setDisplayName(nickName)
                        .build()
                user?.updateProfile(profileUpdates)
                user?.sendEmailVerification()
                Toast.makeText(this,"Verification email sent", Toast.LENGTH_SHORT).show()
                mAuth.signOut()
                finish()
            }
        }
    }

}