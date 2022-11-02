package com.example.restaurantreview

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class Activity2: AppCompatActivity(){



    private lateinit var listOfReviews : java.util.ArrayList<Review>
    private lateinit var myReviewList : java.util.ArrayList<Review>
    private lateinit var myadapter :MyAdapter2
    var isMyReviews: Boolean=false



    var firebase = Firebase.firestore
    private lateinit var restaurant:Restaurants

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        restaurant= intent.getSerializableExtra("restaurantModel") as Restaurants
        Log.d("AHHH","${restaurant.restaurant_name}")




        setContentView(R.layout.activity_main2)

        val fab: View = findViewById(R.id.fab)
        fab.bringToFront();
        fab.setOnClickListener { view ->
            Log.d("TQ1.1","YES")
            if (FirebaseAuth.getInstance().currentUser?.uid !=null) {
                Log.d("TQ1.2","YES2222")
                addReview(view)
            }
            else {
                showMessage(view,"You have to be login to add reviews")
            }

        }

       // val imageModelArrayList = populateList2()

        val recyclerView = findViewById<View>(R.id.relative_Layout2) as RecyclerView // Bind to the recyclerview in the layout
        val layoutManager = LinearLayoutManager(this) // Get the layout manager
        recyclerView.layoutManager = layoutManager


        //val mAdapter = MyAdapter2(ima
        // geModelArrayList)
        //recyclerView.adapter = mAdapter
        listOfReviews = arrayListOf()
        myadapter =MyAdapter2(listOfReviews)


        recyclerView.adapter = myadapter
        EventChangeListener()


        val toggleButton:MaterialButtonToggleGroup = findViewById<ToggleButton>(R.id.toggleButtonGroup) as MaterialButtonToggleGroup;
        toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            if (isChecked) { //split to whether login first
                Log.d("Lop","WE re HERERERE")
                isMyReviews= !isMyReviews
                for (i in 0..(listOfReviews.size-1))   {
                    Log.d("review.user_ID","${listOfReviews[i].user_ID}")
                  //  Log.d("fireba","${firebaseAuth?.currentUser?.uid}")
                    if (listOfReviews[i].user_ID !="f5wy4ssTuUcGYAB41B1fnnMyZn62")    {

                        myadapter.notifyItemRemoved(i)
                    }
                }
                Log.d(" myadapter.itemCount","${ myadapter.itemCount}")

                myadapter.notifyDataSetChanged()

                Log.d("my reviews","${isMyReviews}")
            } else {
                // The toggle is disabled
                Log.d("all reviews","${isMyReviews}")
            }
        }

    }

    fun addReview(view: View) {

        val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
        /*
        builder.setTitle("Add review")
        val input1 = EditText(view.context)
        input1.inputType =
            InputType.TYPE_CLASS_TEXT
        builder.setView(input1)
        builder.setTitle("Add location")
        val input2 = EditText(view.context)
        input2.inputType =
            InputType.TYPE_CLASS_TEXT
        builder.setView(input2)
        builder.setTitle("Add image")
        val input3 = EditText(view.context)
        input3.inputType =
            InputType.TYPE_CLASS_TEXT
        builder.setView(input3)*/
        Log.d("T55","IN")
        val context: Context = view.getContext()
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL


        val reviewBox = EditText(context)
        reviewBox.inputType = InputType.TYPE_CLASS_TEXT
        reviewBox.setTextIsSelectable(true);
        reviewBox.hint = "Add review"
        layout.addView(reviewBox)

        val locationBox = EditText(context)
        locationBox.inputType = InputType.TYPE_CLASS_TEXT
        locationBox.setTextIsSelectable(true);
        locationBox.hint = "Add location"
        Log.d("Resa ", "${locationBox.getText().toString()}")
        layout.addView(locationBox)


        val imageBox = EditText(context)
        imageBox.inputType = InputType.TYPE_CLASS_TEXT
        imageBox.setTextIsSelectable(true);
        imageBox.hint = "Add image"
        Log.d("Image Addc ","${imageBox.text}")
        layout.addView(imageBox)

        builder.setView(layout) // Again this is a set method, not add


        val currentTime = Calendar.getInstance().time

        showMessage(view, "${locationBox.getText().toString()}")

        builder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, which ->

                val review = hashMapOf(

                    "review_description" to reviewBox.getText().toString(),
                    "location" to locationBox.getText().toString(),
                    "meal_image" to imageBox.getText().toString(),
                    "user_ID" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
                    "res_ID" to restaurant.restaurant_name.toString(),
                    "date_edited" to currentTime.toString(),

                )
                Log.d("TQ1 ", "Almsot be succesful")

                var firebase = Firebase.firestore
                firebase.collection("reviews").add(review)
                    .addOnSuccessListener{ documentReference ->
                        Log.d("TQ2 ", "Should be succesful")

                        showMessage(view,"Review added succesfully")

                    }
                    .addOnFailureListener { e ->
                        showMessage(view, "Review failed to be added")
                    }
            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()


    }

    //get Restaurants Data from firebase
    private fun EventChangeListener() {

        firebase.collection("reviews").addSnapshotListener(object : EventListener,
            com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?,
            ) {

                if (error !=null)   {
                    Log.e("Firestore Error",error.message.toString())
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!)  {
                    Log.d("R1","${dc.document.toObject(Review::class.java)}")
                    if (dc.type == DocumentChange.Type.ADDED)   {
                        Log.d("R2","${dc.document.toObject(Review::class.java)}")
                        if (dc.document.toObject(Review::class.java).res_ID==restaurant.restaurant_name) {
                            var review=dc.document.toObject(Review::class.java)
                            review.review_ID=dc.document.id
                            listOfReviews.add(review)

                        }

                    }

                }
                myadapter.notifyDataSetChanged()
            }

        })
    }

    //  for toolbar
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

    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }
}