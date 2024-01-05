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

    // Declaraciones de variables para el enlace de vistas y Firebase
    private lateinit var registerBinding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización del enlace de vistas mediante View Binding
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        Log.d("RegisterActivity", "registerBinding: $registerBinding")
        setContentView(registerBinding.root)

        // Inicialización de la instancia de Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configuración del listener del botón de registro
        registerBinding.btnSubmit.setOnClickListener {
            validarDatos()
        }
    }

    // Función para validar los datos de entrada del usuario
    private fun validarDatos() {
        val nombre_usuario: String = registerBinding.ettUser.text.toString()
        val email: String = registerBinding.ettEmail.text.toString()
        val password: String = registerBinding.ettPassword.text.toString()
        val password2: String = registerBinding.ettPassword2.text.toString()

        // Validaciones de entrada
        if (nombre_usuario.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese nombre de usuario", Toast.LENGTH_SHORT).show()
        } else if (email.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese email", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty() or password2.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese los dos campos de contraseña", Toast.LENGTH_SHORT).show()
        } else if (!password.equals(password2)) {
            Toast.makeText(applicationContext, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
        } else {
            RegistrarUsuario(email, password)
        }
    }

    // Función para registrar al usuario en Firebase Auth y almacenar información adicional en la base de datos
    private fun RegistrarUsuario(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Obtener el ID único del usuario y crear una referencia a la base de datos
                var uId: String = ""
                uId = auth.currentUser!!.uid
                reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(uId)

                // Crear un HashMap con la información del usuario
                val hashmap = HashMap<String, Any>()
                val hUserName: String = registerBinding.ettUser.text.toString()
                val hEmail: String = registerBinding.ettEmail.text.toString()

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
                hashmap["telefono"] = ""
                hashmap["estado"] = "offline"

                // Actualizar la información del usuario en la base de datos
                reference.updateChildren(hashmap).addOnCompleteListener { task ->
                    // Redirigir a la actividad principal después del registro exitoso
                    val intent = Intent(this@Register, MainActivity::class.java)
                    Toast.makeText(applicationContext, "Se ha registrado con éxito", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                }.addOnFailureListener { e ->
                    // Manejar errores durante la actualización de la base de datos
                    Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Manejar errores durante el registro en Firebase Auth
                Toast.makeText(applicationContext, "Ha ocurrido un error inesperado", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            // Manejar errores durante el registro en Firebase Auth
            Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
