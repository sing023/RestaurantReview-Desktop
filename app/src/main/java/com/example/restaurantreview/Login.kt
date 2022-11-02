package com.example.restaurantreview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.io.Serializable

class Login: AppCompatActivity() , Serializable{
    // Initialize Firebase Auth
    private val  firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val authListener:FirebaseAuth.AuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if (firebaseAuth.currentUser != null) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.login)

        val emailText = findViewById<EditText>(R.id.input_email)
        val passText = findViewById<EditText>(R.id.input_password)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {   view ->
            firebaseAuth?.signInWithEmailAndPassword(
                emailText.text.toString(),
                passText.text.toString(),
            )?.addOnCompleteListener(
                this
            ) {
                    task ->
                if (task.isSuccessful) {

                    FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                            showMessage(view,"You have logged out")
                            finish()

                            //val intent = Intent(this, Activity2::class.java)


                            //intent.putExtra("user_ID",user!!)

                            //startActivity(intent)
                        }
                    }


                }   else {
                    closeKeyBoard()
                    showMessage(view,"Couldn't sign you in")
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(authListener)
    }


    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    private fun closeKeyBoard() {
        val view =this.currentFocus
        if (view !=null)    {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate((R.menu.toolbar_layout), menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // val myView = findViewById<View>(R.id.main_toolbar)
        Log.d("InHERE", "not fk")

        when (item.itemId){
            R.id.back_arrow -> {
                //  val snackbar = Snackbar.make(myView, getString(R.string.refresh), Snackbar.LENGTH_LONG)
                //  snackbar.show()
                finish()
                return true
            }
            R.id.action_logout -> {
                // val snackbar = Snackbar.make(myView, getString(R.string.logoff), Snackbar.LENGTH_LONG)
                // snackbar.show()
                FirebaseAuth.getInstance().signOut();


                return true
            }
            R.id.login -> {
                Log.d("inlogin","not bas")

                startActivity(Intent(this, Login::class.java))
            }

            R.id.register -> {
                startActivity(Intent(this, Register::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
