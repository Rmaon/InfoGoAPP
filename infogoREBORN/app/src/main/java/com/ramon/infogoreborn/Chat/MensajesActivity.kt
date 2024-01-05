package com.ramon.infogoreborn.Chat

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.ramon.infogoreborn.Adaptador.AdaptadorChat
import com.ramon.infogoreborn.Modelo.Chat
import com.ramon.infogoreborn.Modelo.Usuario
import com.ramon.infogoreborn.R
import com.ramon.infogoreborn.databinding.ActivityMensajesBinding

class MensajesActivity : AppCompatActivity() {

    private var imagenUri: Uri? = null
    lateinit var mensajesBinding: ActivityMensajesBinding
    var uid_usuario_seleccionado: String = ""
    var firebaseUser: FirebaseUser? = null
    var notificar = false

    var chatAdapter: AdaptadorChat? = null
    var chatList: List<Chat>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mensajesBinding = ActivityMensajesBinding.inflate(layoutInflater)
        setContentView(mensajesBinding.root)
        mensajesBinding.RVChats.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        mensajesBinding.RVChats.layoutManager = linearLayoutManager

        firebaseUser = FirebaseAuth.getInstance().currentUser
        ObtenerUid()
        LeerInfoUser()

        mensajesBinding.IBAdjuntar.setOnClickListener {
            notificar = true
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                AbrirGaleria()
            } else {
                requestGalleryPermiso.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        mensajesBinding.IBEnviar.setOnClickListener {
            val mensaje = mensajesBinding.EtMensaje.text.toString()
            if (mensaje.isEmpty()) {
                Toast.makeText(applicationContext, "Por favor escriba algo", Toast.LENGTH_SHORT).show()
            } else {
                EnviarMensaje(firebaseUser!!.uid, uid_usuario_seleccionado, mensaje)
                mensajesBinding.EtMensaje.setText("")
            }
        }
    }

    // Método para abrir la galería
    private fun AbrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galeriaARL.launch(intent)
    }

    // Callback para la solicitud de permisos de la galería
    private val requestGalleryPermiso =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { Permiso_concedido ->
            if (Permiso_concedido) {
                AbrirGaleria()
            } else {
                Toast.makeText(
                    applicationContext,
                    "El permiso para acceder a la galería no ha sido concedido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // Callback para la selección de imágenes desde la galería
    private val galeriaARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                val data = resultado.data
                imagenUri = data!!.data

                val cargandoImagen = ProgressDialog(this@MensajesActivity)
                cargandoImagen.setMessage("Por favor espere, la imagen se está enviando")
                cargandoImagen.setCanceledOnTouchOutside(false)
                cargandoImagen.show()

                val carpetaImagenes = FirebaseStorage.getInstance().reference.child("Imágenes de mensajes")
                val reference = FirebaseDatabase.getInstance().reference
                val idMensaje = reference.push().key
                val nombreImagen = carpetaImagenes.child("$idMensaje.jpg")

                val uploadTask: StorageTask<*>
                uploadTask = nombreImagen.putFile(imagenUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation nombreImagen.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        cargandoImagen.dismiss()
                        val downloadUrl = task.result
                        val url = downloadUrl.toString()

                        val infoMensajeImagen = HashMap<String, Any?>()
                        infoMensajeImagen["id_mensaje"] = idMensaje
                        infoMensajeImagen["emisor"] = firebaseUser!!.uid
                        infoMensajeImagen["receptor"] = uid_usuario_seleccionado
                        infoMensajeImagen["mensaje"] = "Se ha enviado la imagen"
                        infoMensajeImagen["url"] = url
                        infoMensajeImagen["visto"] = false

                        reference.child("Chats").child(idMensaje!!).setValue(infoMensajeImagen)
                            .addOnCompleteListener { tarea ->
                                if (tarea.isSuccessful) {
                                    val usuarioReference =
                                        FirebaseDatabase.getInstance().reference
                                            .child("Usuarios").child(firebaseUser!!.uid)
                                    usuarioReference.addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val usuario = snapshot.getValue(Usuario::class.java)
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            // Manejar error de Firebase
                                        }
                                    })
                                }
                            }

                        reference.child("Chats").child(idMensaje!!).setValue(infoMensajeImagen)
                            .addOnCompleteListener { tarea ->
                                if (tarea.isSuccessful) {
                                    val listaMensajesEmisor =
                                        FirebaseDatabase.getInstance().reference.child("ListaMensajes")
                                            .child(firebaseUser!!.uid)
                                            .child(uid_usuario_seleccionado)

                                    listaMensajesEmisor.addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (!snapshot.exists()) {
                                                listaMensajesEmisor.child("uid")
                                                    .setValue(uid_usuario_seleccionado)
                                            }

                                            val listaMensajesReceptor =
                                                FirebaseDatabase.getInstance().reference.child("ListaMensajes")
                                                    .child(uid_usuario_seleccionado)
                                                    .child(firebaseUser!!.uid)
                                            listaMensajesReceptor.child("uid")
                                                .setValue(firebaseUser!!.uid)
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            // Manejar error de Firebase
                                        }
                                    })
                                }
                            }
                        Toast.makeText(
                            applicationContext,
                            "La imagen se ha enviado con éxito",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Cancelado por el usuario", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    )

    // Método para obtener el UID del usuario seleccionado
    private fun ObtenerUid() {
        intent = intent
        uid_usuario_seleccionado = intent.getStringExtra("uid_usuario").toString()
    }

    // Método para leer la información del usuario seleccionado
    private fun LeerInfoUser() {
        val reference =
            FirebaseDatabase.getInstance().reference.child("Usuarios").child(uid_usuario_seleccionado)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario: Usuario? = snapshot.getValue(Usuario::class.java)
                mensajesBinding.NUsuarioChat.text = usuario!!.getN_Usuario()
                Glide.with(applicationContext).load(usuario.getImagen())
                    .placeholder(R.drawable.ic_item_usuario).into(mensajesBinding.imagenPerfilChat)

                RecuperarMensajes(
                    firebaseUser!!.uid,
                    uid_usuario_seleccionado,
                    usuario.getImagen()
                )
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error de Firebase
            }
        })
    }

    // Método para recuperar los mensajes de la conversación
    private fun RecuperarMensajes(uid: String, uidUsuarioSeleccionado: String, imagen: String?) {
        chatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (chatList as ArrayList<Chat>).clear()
                for (sn in snapshot.children) {
                    val chat = sn.getValue(Chat::class.java)

                    if (chat!!.getReceptor().equals(uid) && chat.getEmisor().equals(uidUsuarioSeleccionado) || chat.getReceptor().equals(
                            uidUsuarioSeleccionado
                        ) && chat.getEmisor().equals(uid)
                    ) {
                        (chatList as ArrayList<Chat>).add(chat)
                    }

                    chatAdapter = AdaptadorChat(
                        this@MensajesActivity,
                        (chatList as ArrayList<Chat>),
                        imagen!!
                    )
                    mensajesBinding.RVChats.adapter = chatAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error de Firebase
            }
        })
    }

    // Método para enviar un mensaje de texto
    private fun EnviarMensaje(uid: String, uid_usuario_seleccionado: String, mensaje: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val mensajeKey = reference.push().key

        val infoMensaje = HashMap<String, Any?>()
        infoMensaje["id_mensaje"] = mensajeKey
        infoMensaje["emisor"] = uid
        infoMensaje["receptor"] = uid_usuario_seleccionado
        infoMensaje["mensaje"] = mensaje
        infoMensaje["url"] = ""
        infoMensaje["visto"] = false

        reference.child("Chats").child(mensajeKey!!).setValue(infoMensaje).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val listaMensajesEmisor =
                    FirebaseDatabase.getInstance().reference.child("ListaMensajes")
                        .child(firebaseUser!!.uid)
                        .child(uid_usuario_seleccionado)

                listaMensajesEmisor.addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            listaMensajesEmisor.child("uid").setValue(uid_usuario_seleccionado)
                        }

                        val listaMensajesReceptor =
                            FirebaseDatabase.getInstance().reference.child("ListaMensajes")
                                .child(uid_usuario_seleccionado)
                                .child(firebaseUser!!.uid)
                        listaMensajesReceptor.child("uid").setValue(firebaseUser!!.uid)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Manejar error de Firebase
                    }
                })
            }
        }
    }
}
