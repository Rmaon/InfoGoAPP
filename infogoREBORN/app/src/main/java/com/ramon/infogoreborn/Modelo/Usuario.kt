package com.ramon.infogoreborn.Modelo

class Usuario {
    // Propiedades privadas de la clase
    private var uid: String = ""
    private var n_usuario: String = ""
    private var email: String = ""
    private var proveedor: String = ""
    private var telefono: String = ""
    private var imagen: String = ""
    private var buscar: String = ""
    private var nombres: String = ""
    private var apellidos: String = ""
    private var edad: String = ""
    private var profesion: String = ""
    private var domicilio: String = ""
    private var estado: String = ""

    // Constructor vacío necesario para Firebase
    constructor()

    // Constructor principal para inicializar la clase con valores
    constructor(
        uid: String,
        n_usuario: String,
        email: String,
        proveedor: String,
        telefono: String,
        imagen: String,
        buscar: String,
        nombres: String,
        apellidos: String,
        edad: String,
        profesion: String,
        domicilio: String,
        estado: String
    ) {
        this.uid = uid
        this.n_usuario = n_usuario
        this.email = email
        this.proveedor = proveedor
        this.telefono = telefono
        this.imagen = imagen
        this.buscar = buscar
        this.nombres = nombres
        this.apellidos = apellidos
        this.edad = edad
        this.profesion = profesion
        this.domicilio = domicilio
        this.estado = estado
    }

    // Getters y setters para acceder y modificar las propiedades privadas

    // Getter para obtener el UID del usuario
    fun getUid(): String? {
        return uid
    }

    // Setter para establecer el UID del usuario
    fun setUid(uid: String) {
        this.uid = uid
    }

    // Getter para obtener el nombre de usuario
    fun getN_Usuario(): String? {
        return n_usuario
    }

    // Setter para establecer el nombre de usuario
    fun setN_Usuario(n_usuario: String) {
        this.n_usuario = n_usuario
    }

    // Getter para obtener el correo electrónico del usuario
    fun getEmail(): String? {
        return email
    }

    // Setter para establecer el correo electrónico del usuario
    fun setEmail(email: String) {
        this.email = email
    }

    // Getter para obtener el proveedor del usuario
    fun getProveedor(): String? {
        return proveedor
    }

    // Setter para establecer el proveedor del usuario
    fun setProveedor(proveedor: String) {
        this.proveedor = proveedor
    }

    // Getter para obtener el número de teléfono del usuario
    fun getTelefono(): String? {
        return telefono
    }

    // Setter para establecer el número de teléfono del usuario
    fun setTelefono(telefono: String) {
        this.telefono = telefono
    }

    // Getter para obtener la URL de la imagen de perfil del usuario
    fun getImagen(): String? {
        return imagen
    }

    // Setter para establecer la URL de la imagen de perfil del usuario
    fun setImagen(imagen: String) {
        this.imagen = imagen
    }

    // Getter para obtener la cadena de búsqueda asociada al usuario
    fun getBuscar(): String? {
        return buscar
    }

    // Setter para establecer la cadena de búsqueda asociada al usuario
    fun setBuscar(buscar: String) {
        this.buscar = buscar
    }

    // Getter para obtener los nombres del usuario
    fun getNombres(): String? {
        return nombres
    }

    // Setter para establecer los nombres del usuario
    fun setNombres(nombres: String) {
        this.nombres = nombres
    }

    // Getter para obtener los apellidos del usuario
    fun getApellidos(): String? {
        return apellidos
    }

    // Setter para establecer los apellidos del usuario
    fun setApellidos(apellidos: String) {
        this.apellidos = apellidos
    }

    // Getter para obtener la edad del usuario
    fun getEdad(): String? {
        return edad
    }

    // Setter para establecer la edad del usuario
    fun setEdad(edad: String) {
        this.edad = edad
    }

    // Getter para obtener la profesión del usuario
    fun getProfesion(): String? {
        return profesion
    }

    // Setter para establecer la profesión del usuario
    fun setProfesion(profesion: String) {
        this.profesion = profesion
    }

    // Getter para obtener el domicilio del usuario
    fun getDomicilio(): String? {
        return domicilio
    }

    // Setter para establecer el domicilio del usuario
    fun setDomicilio(domicilio: String) {
        this.domicilio = domicilio
    }

    // Getter para obtener el estado del usuario
    fun getEstado(): String? {
        return estado
    }

    // Setter para establecer el estado del usuario
    fun setEstado(estado: String) {
        this.estado = estado
    }
}
