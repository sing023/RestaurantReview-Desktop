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

class Register: AppCompatActivity() , Serializable{
    // Initialize Firebase Auth
    var firebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.register)

        val emailText = findViewById<EditText>(R.id.input_email)
        val passText = findViewById<EditText>(R.id.input_password)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener {   view ->
            firebaseAuth?.createUserWithEmailAndPassword(

                emailText.text.toString(),
                passText.text.toString(),
            )?.addOnCompleteListener(
                this
            ) {
                    task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth?.currentUser


                    finish()


                }   else {
                    closeKeyBoard()
                    showMessage(view,"Couldn't register an account ")
                }
            }

        }
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

        Log.d("InHERE", "not fk")

        when (item.itemId){
            R.id.back_arrow -> {

                finish()
                return true
            }
            R.id.action_logout -> {

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
