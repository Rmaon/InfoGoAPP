package com.ramon.infogoreborn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.ramon.infogoreborn.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    lateinit var loginBinding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        //supportActionBar!!.title = "Login"
        auth = FirebaseAuth.getInstance()

        loginBinding.btnLogIn.setOnClickListener{
            validarDatos()
        }

    }

    private fun validarDatos() {
        val email : String = loginBinding.ettEmailLog.text.toString()
        val password : String = loginBinding.ettPasswordLog.text.toString()
        if (email.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese un correo", Toast.LENGTH_SHORT).show()
        }else if (password.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese una contraseña", Toast.LENGTH_SHORT).show()
        }else{
            LoginUser(email, password)
        }
    }

    private fun LoginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{task->
            if (task.isSuccessful){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(applicationContext, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{e->
            Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}