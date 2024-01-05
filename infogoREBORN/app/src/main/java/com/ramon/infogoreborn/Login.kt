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

        // Inicializar el objeto de enlace para la vista mediante View Binding
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configurar el clic en el botón de inicio de sesión
        loginBinding.btnLogIn.setOnClickListener{
            validarDatos()
        }
    }

    // Validar los datos ingresados por el usuario
    private fun validarDatos() {
        val email : String = loginBinding.ettEmailLog.text.toString()
        val password : String = loginBinding.ettPasswordLog.text.toString()

        // Verificar si el campo de correo está vacío
        if (email.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese un correo", Toast.LENGTH_SHORT).show()
        }
        // Verificar si el campo de contraseña está vacío
        else if (password.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese una contraseña", Toast.LENGTH_SHORT).show()
        }
        // Si ambos campos están completos, intentar iniciar sesión
        else{
            LoginUser(email, password)
        }
    }

    // Iniciar sesión con el correo y la contraseña proporcionados
    private fun LoginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{task->
            if (task.isSuccessful){
                // Si la autenticación es exitosa, abrir la actividad principal
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                // Si la autenticación falla, mostrar un mensaje de error
                Toast.makeText(applicationContext, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{e->
            // Si hay una excepción durante la autenticación, mostrar el mensaje de excepción
            Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
