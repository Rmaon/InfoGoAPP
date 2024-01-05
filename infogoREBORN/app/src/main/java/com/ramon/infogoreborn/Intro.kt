package com.ramon.infogoreborn

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.telephony.mbms.DownloadProgressListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.ramon.infogoreborn.databinding.ActivityIntroBinding

class Intro : AppCompatActivity() {

    private lateinit var bindingIntro: ActivityIntroBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var googleSignInClient: GoogleSignInClient

    var firebaseUser : FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar el objeto de enlace para la vista mediante View Binding
        bindingIntro = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(bindingIntro.root)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        // Configurar las opciones de inicio de sesión con Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Configurar el cliente de inicio de sesión con Google
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Configurar el clic en los botones
        bindingIntro.btnLogin.setOnClickListener {
            val intent = Intent(this@Intro, Login::class.java)
            startActivity(intent)
        }

        bindingIntro.btnRegister.setOnClickListener {
            val intent = Intent(this@Intro, Register::class.java)
            startActivity(intent)
        }

        bindingIntro.btnGoogle.setOnClickListener {
            SingInGOOGLE()
        }
    }

    // Función para iniciar sesión con Google
    private fun SingInGOOGLE() {
        val googlesignIntent = googleSignInClient.signInIntent
        googleSignInARL.launch(googlesignIntent)
    }

    // Resultado de la actividad para iniciar sesión con Google
    private val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { resultado->
        if (resultado.resultCode == RESULT_OK){
            val data = resultado.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                AutenticarGoogleFirebase(account.idToken)
            }catch (e: Exception){
                Toast.makeText(applicationContext, "Ha ocurrido una excepción debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(applicationContext, "Cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    // Autenticar en Firebase usando las credenciales de Google
    private fun AutenticarGoogleFirebase(idToken: String?) {
        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credencial)
            .addOnSuccessListener { authResult->
                /*Si el usuario es nuevo*/
                if (authResult.additionalUserInfo!!.isNewUser){
                    GuardarInfoBD()
                }
                /*Si el usuario ya se registró previamente*/
                else{
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
            }.addOnFailureListener { e->
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Guardar la información del usuario en la base de datos
    private fun GuardarInfoBD() {
        progressDialog.setMessage("Se está registrando su información...")
        progressDialog.show()

        /*Obtener información de una cuenta de Google*/
        val uidGoogle = auth.uid
        val correoGoogle = auth.currentUser?.email
        val n_Google = auth.currentUser?.displayName
        val nombre_usuario_G : String = n_Google.toString()

        val hashmap = HashMap<String, Any?>()
        hashmap["uid"] = uidGoogle
        hashmap["n_usuario"] = nombre_usuario_G
        hashmap["email"] = correoGoogle
        hashmap["imagen"] = ""
        hashmap["buscar"] = nombre_usuario_G.lowercase()

        /*Nuevos datos de usuario*/
        hashmap["nombres"] = ""
        hashmap["apellidos"] =""
        hashmap["edad"] = ""
        hashmap["profesion"] = ""
        hashmap["domicilio"] = ""
        hashmap["telefono"] = ""
        hashmap["estado"] = "offline"
        hashmap["proveedor"] = "Google"

        /*Referencia a la base de datos*/
        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uidGoogle!!)
            .setValue(hashmap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                Toast.makeText(applicationContext, "Se ha registrado exitosamente", Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Comprobar si ya hay una sesión iniciada al iniciar la actividad
    private fun ComprobarSesion(){
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser!=null){
            val intent = Intent(this@Intro, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        ComprobarSesion()
        super.onStart()
    }
}