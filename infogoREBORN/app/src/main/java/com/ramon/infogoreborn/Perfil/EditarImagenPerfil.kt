package com.ramon.infogoreborn.Perfil

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.ramon.infogoreborn.R

class EditarImagenPerfil : AppCompatActivity() {

    private lateinit var ImagenPerfilActualizar: ImageView
    private lateinit var BtnElegirImagen: Button
    private lateinit var BtnActualizarImagen: Button
    private var imageUri: Uri? = null

    private lateinit var firebaseAuth: FirebaseAuth
    var firebaseUser: FirebaseUser? = null
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_imagen_perfil)

        ImagenPerfilActualizar = findViewById(R.id.ImagenPerfilActualizar)
        BtnElegirImagen = findViewById(R.id.BtnElegirImagenDe)
        BtnActualizarImagen = findViewById(R.id.BtnActualizarImagen)

        progressDialog = ProgressDialog(this@EditarImagenPerfil)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        BtnElegirImagen.setOnClickListener {
            // Mostrar el diálogo para seleccionar la imagen
            MostrarDialog()
        }

        BtnActualizarImagen.setOnClickListener {
            // Validar si se ha seleccionado una imagen antes de intentar actualizarla
            ValidarImagen()
        }
    }

    private fun ValidarImagen() {
        if (imageUri == null) {
            Toast.makeText(applicationContext, "Es necesario una imagen", Toast.LENGTH_SHORT).show()
        } else {
            // Subir la imagen a Firebase Storage
            SubirImagen()
        }
    }

    private fun SubirImagen() {
        progressDialog.setMessage("Actualizando imagen... ")
        progressDialog.show()
        val rutaImagen = "Perfil_usuario/" + firebaseAuth.uid
        val referenceStorage = FirebaseStorage.getInstance().getReference(rutaImagen)

        // Subir la imagen al almacenamiento Firebase
        referenceStorage.putFile(imageUri!!).addOnSuccessListener { tarea ->
            val uriTarea: Task<Uri> = tarea.storage.downloadUrl
            while (!uriTarea.isSuccessful);
            val urlImagen = "${uriTarea.result}"

            // Actualizar la URL de la imagen en la base de datos de Firebase
            ActualizarImagenBD(urlImagen)

        }.addOnFailureListener { e ->
            Toast.makeText(
                applicationContext,
                "No se ha podido subir la imagen debido a: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun ActualizarImagenBD(urlImagen: String) {
        progressDialog.setMessage("Actualizando imagen de perfil")
        val hashmap: HashMap<String, Any> = HashMap()
        if (imageUri != null) {
            hashmap["imagen"] = urlImagen
        }

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(firebaseAuth.uid!!).updateChildren(hashmap).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(applicationContext, "Su imagen ha sido actualizada", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener { e ->
            Toast.makeText(
                applicationContext,
                "No se ha actualizado su imagen debido a: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun AbrirGaleria() {
        Log.d("Permiso", "Solicitando permiso para abrir galería")
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galeriaActivityResultLauncher.launch(intent)
    }

    // Resultado del permiso para acceder a la galería
    private val requestGalleryPermiso =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { Permiso_concedido ->
            Log.d("Permiso", "Permiso concedido: $Permiso_concedido")
            if (Permiso_concedido) {
                // Permiso concedido, abrir la galería
                AbrirGaleria()
            } else {
                // Permiso denegado, mostrar un mensaje
                Toast.makeText(
                    applicationContext,
                    "El permiso para acceder a la galería no ha sido concedido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // Resultado de la selección de la galería
    private val galeriaActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                val data = resultado.data
                imageUri = data!!.data
                ImagenPerfilActualizar.setImageURI(imageUri)
            } else {
                Toast.makeText(applicationContext, "Cancelado por el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Función para abrir la cámara y tomar una foto
    private fun AbrirCamara() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Titulo")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripcion")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        camaraActivityResultLauncher.launch(intent)
    }

    // Resultado del permiso para acceder a la cámara
    private val requestCameraPermiso =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { Permiso_concedido ->
            if (Permiso_concedido) {
                // Permiso concedido, abrir la cámara
                AbrirCamara()
            } else {
                // Permiso denegado, mostrar un mensaje
                Toast.makeText(
                    applicationContext,
                    "El permiso para acceder a la cámara no ha sido concedido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // Resultado de la captura de la cámara
    private val camaraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado_camara ->
            if (resultado_camara.resultCode == RESULT_OK) {
                // Imagen capturada con éxito
                ImagenPerfilActualizar.setImageURI(imageUri)
            } else {
                Toast.makeText(applicationContext, "Cancelado por el usuario", Toast.LENGTH_SHORT).show()
            }
        }

    // Función para mostrar el diálogo con opciones de galería y cámara
    private fun MostrarDialog() {
        val Btn_abrir_galeria: Button
        val Btn_abrir_camara: Button

        val dialog = Dialog(this@EditarImagenPerfil)

        dialog.setContentView(R.layout.cuadro_d_seleccionar)

        Btn_abrir_galeria = dialog.findViewById(R.id.Btn_abrir_galeria)
        Btn_abrir_camara = dialog.findViewById(R.id.Btn_abrir_camara)

        // Acción al hacer clic en el botón de abrir galería
        Btn_abrir_galeria.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permiso concedido, abrir la galería
                AbrirGaleria()
                dialog.dismiss()
            } else {
                // Solicitar permiso para acceder a la galería
                requestGalleryPermiso.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        // Acción al hacer clic en el botón de abrir cámara
        Btn_abrir_camara.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permiso concedido, abrir la cámara
                AbrirCamara()
                dialog.dismiss()
            } else {
                // Solicitar permiso para acceder a la cámara
                requestCameraPermiso.launch(Manifest.permission.CAMERA)
            }
        }

        // Mostrar el diálogo
        dialog.show()
    }

    // Función para actualizar el estado del usuario (online/offline) en la base de datos
    private fun ActualizarEstado(estado: String) {
        val reference =
            FirebaseDatabase.getInstance().reference.child("Usuarios")
                .child(firebaseUser!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["estado"] = estado
        reference!!.updateChildren(hashMap)
    }

    // Función que se llama cuando la actividad se reanuda
    override fun onResume() {
        super.onResume()
        ActualizarEstado("online")
    }

    // Función que se llama cuando la actividad se pausa
    override fun onPause() {
        super.onPause()
        ActualizarEstado("offline")
    }
}
