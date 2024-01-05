package com.ramon.infogoreborn.Adaptador

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ramon.infogoreborn.Chat.MensajesActivity
import com.ramon.infogoreborn.Modelo.Usuario
import com.ramon.infogoreborn.R

class AdaptadorUsuario(context: Context, listaUsuarios: List<Usuario>, b: Boolean) :
    RecyclerView.Adapter<AdaptadorUsuario.ViewHolder?>() {

    private val context: Context
    private val listaUsuarios: List<Usuario>

    init {
        this.context = context
        this.listaUsuarios = listaUsuarios
    }

    // Clase interna que representa la vista de cada elemento del RecyclerView
    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var nombre_usuario: TextView
        var email_usuario: TextView
        var imagen_usuario: ImageView

        init {
            nombre_usuario = itemview.findViewById(R.id.itemUserName)
            email_usuario = itemview.findViewById(R.id.Item_email_usuario)
            imagen_usuario = itemview.findViewById(R.id.item_imagen)
        }
    }

    // Método que crea una nueva instancia de ViewHolder cuando se necesita
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_usuario, parent, false)
        return ViewHolder(view)
    }

    // Método que devuelve la cantidad de elementos en la lista de usuarios
    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    // Método que asigna los datos de un usuario a un ViewHolder específico
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario: Usuario = listaUsuarios[position]

        // Asigna los valores del usuario a las vistas correspondientes en el ViewHolder
        holder.nombre_usuario.text = usuario.getN_Usuario()
        holder.email_usuario.text = usuario.getEmail()
        Glide.with(context).load(usuario.getImagen()).placeholder(R.drawable.ic_item_usuario)
            .into(holder.imagen_usuario)

        // Configura un Listener para el evento de clic en un elemento del RecyclerView
        holder.itemView.setOnClickListener {
            // Crea un Intent para iniciar la actividad de Mensajes con el UID del usuario seleccionado
            val intent = Intent(context, MensajesActivity::class.java)
            intent.putExtra("uid_usuario", usuario.getUid())
            context.startActivity(intent)
        }
    }
}
