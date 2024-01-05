package com.ramon.infogoreborn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ramon.infogoreborn.databinding.ActivityRegisterBinding

class Register : AppCompatActivity() {

    private lateinit var registerBinding: ActivityRegisterBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        Log.d("RegisterActivity", "registerBinding: $registerBinding")
        setContentView(registerBinding.root)


        //supportActionBar!!.title = "Registro"
        auth = FirebaseAuth.getInstance()

        registerBinding.btnSubmit.setOnClickListener{
            validarDatos()
        }

    }

    private fun validarDatos() {
        val nombre_usuario : String = registerBinding.ettUser.text.toString()
        val email : String = registerBinding.ettEmail.text.toString()
        val password : String = registerBinding.ettPassword.text.toString()
        val password2 : String = registerBinding.ettPassword2.text.toString()

        if (nombre_usuario.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese nombre de usuario", Toast.LENGTH_SHORT).show()
        } else if (email.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese email", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty() or password2.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese los dos campos de contraseña", Toast.LENGTH_SHORT).show()
        } else if (!password.equals(password2)){
            Toast.makeText(applicationContext, "Las contraseña nos coinciden", Toast.LENGTH_SHORT).show()
        } else{
            RegistrarUsuario(email, password)

        }
    }

    private fun RegistrarUsuario(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task->
            if (task.isSuccessful){
                var uId: String = ""
                uId = auth.currentUser!!.uid
                reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(uId)

                val hashmap = HashMap<String, Any>()
                val hUserName : String = registerBinding.ettUser.text.toString()
                val hEmail : String = registerBinding.ettEmail.text.toString()

                hashmap["uid"] = uId
                hashmap["n_usuario"] = hUserName
                hashmap["email"] = hEmail
                hashmap["proveedor"] = "mail"
                hashmap["telefono"] = ""
                hashmap["imagen"] = ""
                hashmap["buscar"] = hUserName.lowercase()
                hashmap["nombres"] = ""
                hashmap["apellidos"] = ""
                hashmap["edad"] = ""
                hashmap["profesion"] = ""
                hashmap["domicilio"] = ""
                hashmap["telefono"]= ""
                hashmap["estado"] = "offline"

                Log.d("FirebaseData", "HashMap values: $hashmap")


                reference.updateChildren(hashmap).addOnCompleteListener{task->
                    val intent = Intent(this@Register, MainActivity::class.java)
                    Toast.makeText(applicationContext, "se ha registrado con exito", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                }.addOnFailureListener{e->
                    Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(applicationContext, "Ha ocurrido un error inesperado", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener{e->
            Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
        }

    }
}