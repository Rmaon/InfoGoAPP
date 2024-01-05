package com.ramon.infogoreborn

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ramon.infogoreborn.Modelo.Usuario
import com.ramon.infogoreborn.Perfil.PerfilActivity
import com.ramon.infogoreborn.databinding.ActivityMainBinding
import com.ramon.infogoreborn.fragmentos.FragmentChat
import com.ramon.infogoreborn.fragmentos.FragmentUsuario

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var nombre_usuario : TextView
    var reference : DatabaseReference?=null
    var firebaseUser : FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar el objeto de enlace para la vista mediante View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Crear un adaptador de fragmentos para la vista de páginas
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        // Agregar fragmentos al adaptador
        viewPagerAdapter.addItem(FragmentUsuario(), "Usuarios")
        viewPagerAdapter.addItem(FragmentChat(), "Chats")

        // Configurar el adaptador para la vista de páginas
        binding.vpMain.adapter = viewPagerAdapter
        binding.tbMain.setupWithViewPager(binding.vpMain)

        // Configurar la barra de herramientas
        setSupportActionBar(binding.toolbarMain)
        nombre_usuario = findViewById(R.id.UserName)

        // Inicializar Firebase Auth y obtener datos del usuario
        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(firebaseUser!!.uid)
        ObtenerDato()
    }

    // Obtener datos del usuario desde la base de datos
    fun ObtenerDato(){
        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val usuario : Usuario? = snapshot.getValue(Usuario::class.java)
                    nombre_usuario.text = " - "+usuario!!.getN_Usuario()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar eventos de cancelación si es necesario
            }
        })
    }

    // Adaptador de fragmentos para la vista de páginas
    class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        private val listaFragmento : MutableList<Fragment> = ArrayList()
        private val listaTitulo : MutableList<String> = ArrayList()

        override fun getCount(): Int {
            return listaFragmento.size
        }

        override fun getItem(position: Int): Fragment {
            return listaFragmento[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return listaTitulo[position]
        }

        // Agregar un fragmento al adaptador
        fun addItem(fragment: Fragment, titulo:String){
            listaFragmento.add(fragment)
            listaTitulo.add(titulo)
        }
    }

    // Inflar el menú en la barra de herramientas
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    // Manejar eventos de clic en elementos del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_perfil->{
                // Abrir la actividad del perfil del usuario
                val intent = Intent(this@MainActivity, PerfilActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_acerca_de -> {
                // Mostrar un mensaje acerca de la aplicación
                val mensajeAcercaDe = getString(R.string.acerca_de_mensaje)
                Toast.makeText(applicationContext, mensajeAcercaDe, Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.Mensaje_sms->{
                // Enviar un mensaje de texto a un número de contacto
                val numeroContacto = "638933256"
                val uri = Uri.parse("smsto:$numeroContacto")
                val intent = Intent(Intent.ACTION_SENDTO, uri)
                intent.putExtra("sms_body", "Hola, este es mi mensaje predeterminado")

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No se encontró ninguna aplicación de mensajes", Toast.LENGTH_SHORT).show()
                }
                return true
            }
            R.id.Mensaje_mail->{
                // Enviar un correo electrónico
                val correoDestinatario = "ramoncamineroarroyo@gmail.com"
                val asuntoCorreo = "Asunto del correo"
                val cuerpoCorreo = "Cuerpo del correo"

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(correoDestinatario))
                intent.putExtra(Intent.EXTRA_SUBJECT, asuntoCorreo)
                intent.putExtra(Intent.EXTRA_TEXT, cuerpoCorreo)

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No se encontró ninguna aplicación de correo electrónico", Toast.LENGTH_SHORT).show()
                }

                return true
            }
            R.id.mSalir->{
                // Cerrar sesión y regresar a la actividad de inicio
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@MainActivity, Intro::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
