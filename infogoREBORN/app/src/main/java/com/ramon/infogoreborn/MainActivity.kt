package com.ramon.infogoreborn

import android.content.Intent
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addItem(FragmentUsuario(), "Usuarios")
        viewPagerAdapter.addItem(FragmentChat(), "Chats")

        binding.vpMain.adapter = viewPagerAdapter
        binding.tbMain.setupWithViewPager(binding.vpMain)
        setSupportActionBar(binding.toolbarMain)
        nombre_usuario = findViewById(R.id.UserName)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(firebaseUser!!.uid)

        ObtenerDato()


    }

    fun ObtenerDato(){
        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val usuario : Usuario? = snapshot.getValue(Usuario::class.java)
                    nombre_usuario.text = " - "+usuario!!.getN_Usuario()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
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

        fun addItem(fragment: Fragment, titulo:String){
            listaFragmento.add(fragment)
            listaTitulo.add(titulo)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_perfil->{
                val intent = Intent(this@MainActivity, PerfilActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_acerca_de->{
                Toast.makeText(applicationContext, "Acerca de", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.mSalir->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@MainActivity, Intro::class.java)
                startActivity(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}