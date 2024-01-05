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
    var user : FirebaseUser?=null
    var reference : DatabaseReference?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        perfilBinding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(perfilBinding.root)

        user = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(user!!.uid)

        ObtenerDatos()

        perfilBinding.BtnGuardar.setOnClickListener{
            ActualizarInformacion()
        }
        perfilBinding.EditarImagen.setOnClickListener{
            val intent = Intent(applicationContext, EditarImagenPerfil::class.java)
            startActivity(intent)
        }

    }
    private fun ActualizarInformacion(){
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

        reference!!.updateChildren(hashmap).addOnCompleteListener{task->
            if (task.isSuccessful){
                Toast.makeText(applicationContext,"Se han actualizado los datos", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext,"No se han actualizado los datos", Toast.LENGTH_SHORT).show()

            }
        }.addOnFailureListener{e->
            Toast.makeText(applicationContext,"Ha ocurrido un error ${e.message}", Toast.LENGTH_SHORT).show()

        }


    }

    private fun ObtenerDatos(){
        reference!!.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val usuario : Usuario?= snapshot.getValue(Usuario::class.java)
                    val str_n_usuario = usuario!!.getN_Usuario()
                    val str_email = usuario.getEmail()
                    val str_proveedor = usuario.getProveedor()
                    val str_nombres = usuario.getNombres()
                    val str_apellidos = usuario.getApellidos()
                    val str_profesion = usuario.getProfesion()
                    val str_domicilio = usuario.getDomicilio()
                    val str_edad = usuario.getEdad()
                    val str_telefono = usuario.getTelefono()

                    perfilBinding.PNUsuario.text = str_n_usuario
                    perfilBinding.PEmail.text = str_email
                    perfilBinding.PProveedor.text = str_proveedor
                    perfilBinding.PNombres.setText(str_nombres)
                    perfilBinding.PApellidos.setText(str_apellidos)
                    perfilBinding.PProfesion.setText(str_profesion)
                    perfilBinding.PDomicilio.setText(str_domicilio)
                    perfilBinding.PEdad.setText(str_edad)
                    perfilBinding.PTelefono.setText(str_telefono)
                    Glide.with(applicationContext).load(usuario.getImagen()).placeholder(R.drawable.ic_item_usuario).into(perfilBinding.PImagen)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}