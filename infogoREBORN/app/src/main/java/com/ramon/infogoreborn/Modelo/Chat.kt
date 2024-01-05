package com.ramon.infogoreborn.Modelo

class Chat {
    // Propiedades privadas de la clase
    private var id_mensaje: String = ""
    private var emisor: String = ""
    private var receptor: String = ""
    private var mensaje: String = ""
    private var url: String = ""
    private var visto = false

    // Constructor vacío necesario para Firebase
    constructor()

    // Constructor principal para inicializar la clase con valores
    constructor(
        id_mensaje: String,
        emisor: String,
        receptor: String,
        mensaje: String,
        url: String,
        visto: Boolean
    ) {
        this.id_mensaje = id_mensaje
        this.emisor = emisor
        this.receptor = receptor
        this.mensaje = mensaje
        this.url = url
        this.visto = visto
    }

    // Getters y setters para acceder y modificar las propiedades privadas

    // Getter para obtener el ID del mensaje
    fun getId_Mensaje(): String? {
        return id_mensaje
    }

    // Setter para establecer el ID del mensaje
    fun setId_Mensaje(id_mensaje: String?) {
        this.id_mensaje = id_mensaje!!
    }

    // Getter para obtener el emisor del mensaje
    fun getEmisor(): String? {
        return emisor
    }

    // Setter para establecer el emisor del mensaje
    fun setEmisor(emisor: String?) {
        this.emisor = emisor!!
    }

    // Getter para obtener el receptor del mensaje
    fun getReceptor(): String? {
        return receptor
    }

    // Setter para establecer el receptor del mensaje
    fun setReceptor(receptor: String?) {
        this.receptor = receptor!!
    }

    // Getter para obtener el contenido del mensaje
    fun getMensaje(): String? {
        return mensaje
    }

    // Setter para establecer el contenido del mensaje
    fun setMensaje(mensaje: String?) {
        this.mensaje = mensaje!!
    }

    // Getter para obtener la URL de la imagen (si es un mensaje de imagen)
    fun getUrl(): String? {
        return url
    }

    // Setter para establecer la URL de la imagen
    fun setUrl(url: String?) {
        this.url = url!!
    }

    // Getter para verificar si el mensaje ha sido visto
    fun isVisto(): Boolean {
        return visto
    }

    // Setter para establecer si el mensaje ha sido visto
    fun setIsVisto(visto: Boolean?) {
        this.visto = visto!!
    }
}
