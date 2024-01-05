package com.ramon.infogoreborn.Perfil

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ramon.infogoreborn.Modelo.Usuario
import com.ramon.infogoreborn.R
import com.ramon.infogoreborn.databinding.ActivityPerfilBinding

class PerfilActivity : AppCompatActivity() {

    lateinit var perfilBinding: ActivityPerfilBinding
    var user: FirebaseUser? = null
    var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar el objeto de enlace para la vista mediante View Binding
        perfilBinding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(perfilBinding.root)

        // Obtener la instancia actual del usuario y la referencia a la base de datos
        user = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(user!!.uid)

        // Obtener y mostrar los datos del usuario
        ObtenerDatos()

        // Configurar el botón de guardar para actualizar la información del perfil
        perfilBinding.BtnGuardar.setOnClickListener {
            ActualizarInformacion()
        }

        // Configurar el botón para editar la imagen de perfil
        perfilBinding.EditarImagen.setOnClickListener {
            val intent = Intent(applicationContext, EditarImagenPerfil::class.java)
            startActivity(intent)
        }
    }

    // Función para actualizar la información del perfil en la base de datos
    private fun ActualizarInformacion() {
        val str_nombres = perfilBinding.PNombres.text.toString()
        val str_apellidos = perfilBinding.PApellidos.text.toString()
        val str_profesion = perfilBinding.PProfesion.text.toString()
        val str_domicilio = perfilBinding.PDomicilio.text.toString()
        val str_edad = perfilBinding.PEdad.text.toString()
        val str_telefono = perfilBinding.PTelefono.text.toString()

        val hashmap = HashMap<String, Any>()
        hashmap["nombres"] = str_nombres
        hashmap["apellidos"] = str_apellidos
        hashmap["profesion"] = str_profesion
        hashmap["domicilio"] = str_domicilio
        hashmap["edad"] = str_edad
        hashmap["telefono"] = str_telefono

        reference!!.updateChildren(hashmap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Se han actualizado los datos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "No se han actualizado los datos", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(applicationContext, "Ha ocurrido un error ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para obtener los datos del usuario desde la base de datos y mostrarlos en la interfaz de usuario
    private fun ObtenerDatos() {
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Obtener el objeto Usuario de la base de datos
                    val usuario: Usuario? = snapshot.getValue(Usuario::class.java)

                    // Obtener los atributos del usuario
                    val str_n_usuario = usuario!!.getN_Usuario()
                    val str_email = usuario.getEmail()
                    val str_proveedor = usuario.getProveedor()
                    val str_nombres = usuario.getNombres()
                    val str_apellidos = usuario.getApellidos()
                    val str_profesion = usuario.getProfesion()
                    val str_domicilio = usuario.getDomicilio()
                    val str_edad = usuario.getEdad()
                    val str_telefono = usuario.getTelefono()

                    // Mostrar los datos del usuario en la interfaz de usuario
                    perfilBinding.PNUsuario.text = str_n_usuario
                    perfilBinding.PEmail.text = str_email
                    perfilBinding.PProveedor.text = str_proveedor
                    perfilBinding.PNombres.setText(str_nombres)
                    perfilBinding.PApellidos.setText(str_apellidos)
                    perfilBinding.PProfesion.setText(str_profesion)
                    perfilBinding.PDomicilio.setText(str_domicilio)
                    perfilBinding.PEdad.setText(str_edad)
                    perfilBinding.PTelefono.setText(str_telefono)

                    // Cargar la imagen del usuario usando Glide
                    Glide.with(applicationContext).load(usuario.getImagen()).placeholder(R.drawable.ic_item_usuario).into(perfilBinding.PImagen)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el caso en que la lectura de datos sea cancelada (no implementado en este ejemplo)
            }
        })
    }
}
