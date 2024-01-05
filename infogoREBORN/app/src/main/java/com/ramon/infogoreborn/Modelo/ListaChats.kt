package com.ramon.infogoreborn.Modelo

class ListaChats {
    // Propiedad privada que almacena el identificador único (UID)
    private var uid: String = ""

    // Constructor vacío necesario para Firebase
    constructor()

    // Constructor principal para inicializar la clase con un UID
    constructor(uid: String) {
        this.uid = uid
    }

    // Getter para obtener el UID
    fun getUid(): String? {
        return uid
    }

    // Setter para establecer el UID
    fun setUid(uid: String?) {
        this.uid = uid!!
    }
}
