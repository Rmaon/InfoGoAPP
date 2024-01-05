package com.ramon.infogoreborn.Adaptador

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ramon.infogoreborn.Chat.MensajesActivity
import com.ramon.infogoreborn.Modelo.Usuario
import com.ramon.infogoreborn.R

class AdaptadorUsuario (context: Context, listaUsuarios : List<Usuario>) : RecyclerView.Adapter<AdaptadorUsuario.ViewHolder?>(){
    private val context : Context
    private val listaUsuarios : List<Usuario>
    init {
        this.context = context
        this.listaUsuarios = listaUsuarios
    }
    class ViewHolder (itemview : View) : RecyclerView.ViewHolder(itemview) {
        var nombre_usuario : TextView
        var email_usuario : TextView
        var imagen_usario : ImageView

        init {
            nombre_usuario = itemview.findViewById(R.id.itemUserName)
            email_usuario = itemview.findViewById(R.id.Item_email_usuario)
            imagen_usario = itemview.findViewById(R.id.item_imagen)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.item_usuario, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario : Usuario = listaUsuarios[position]
        holder.nombre_usuario.text = usuario.getN_Usuario()
        holder.email_usuario.text = usuario.getEmail()
        Glide.with(context).load(usuario.getImagen()).placeholder(R.drawable.ic_item_usuario).into(holder.imagen_usario)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, MensajesActivity::class.java)
            intent.putExtra("uid_usuario", usuario.getUid())
            context.startActivity(intent)
        }
    }
}