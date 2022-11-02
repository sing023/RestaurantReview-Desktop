package com.example.restaurantreview

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.util.*


class MyAdapter2(private val imageModelArrayList: ArrayList<Review>) : RecyclerView.Adapter<MyAdapter2.ViewHolder2>() {

   var review_userID:String =""
    var review_ID:String=""
    var no:Int=0
    /*
     * Inflate our views using the layout defined in row_layout.xml
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder2 {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.row_layout2, parent, false)
        return ViewHolder2(v)
    }

    /*
     * Bind the data to the child views of the ViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder2, position: Int) {

        val info = imageModelArrayList[position]
        Log.d("YR","${info.meal_image.toString()}")
        val image_url=info.meal_image

        review_userID=info.user_ID.toString()
        review_ID=info.review_ID.toString()
        no=position
            Picasso
                .get()
                .load(image_url)
                .resize(80,120)
                .placeholder(R.drawable.profile_pic)
                .error(R.drawable.profile_pic)
                .noFade()
                .into(holder.imgView)
            holder.dateEdited.text=info.date_edited.toString()
            holder.profile_name.text=info.user_ID
            holder.review_Description.text=info.review_description


    }

    /*
     * Get the maximum size of the
     */
    override fun getItemCount(): Int {
        return imageModelArrayList.size
    }




    /*
     * The parent class that handles layout inflation and child view use
     */
    inner class ViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        var imgView = itemView.findViewById<View>(R.id.profile_pic) as ImageView
        var review_Description = itemView.findViewById<View>(R.id.firstLine) as TextView
        var profile_name= itemView.findViewById<View>(R.id.profile_name) as TextView
        var dateEdited=itemView.findViewById<View>(R.id.dateEdited) as TextView



        var edit_icon =itemView.findViewById<View>(R.id.edit_review) as ImageView

        var delete_icon = itemView.findViewById<View>(R.id.delete_reviews) as ImageView



        init {


            edit_icon.setOnClickListener(View.OnClickListener {
                if (FirebaseAuth.getInstance().currentUser?.uid !=null) {
                    if (review_userID==FirebaseAuth.getInstance().currentUser?.uid) {
                        clickEditIcon()
                    }
                    else {
                        showMessage(itemView,"You can only edit reviews from youw own your own account")

                    }
                }
                else {
                    showMessage(itemView,"You have to be login")
                }

            })

            delete_icon.setOnClickListener(View.OnClickListener {
                if (FirebaseAuth.getInstance().currentUser?.uid !=null) {
                    if (review_userID==FirebaseAuth.getInstance().currentUser?.uid) {
                        clickDeleteIcon()
                    }
                    else {
                        showMessage(itemView,"You can only delete reviews from youw own your own account")

                    }
                }
                else {
                    showMessage(itemView,"You have to be login")
                }
            })

        }



        private fun clickEditIcon() {
            val builder: AlertDialog.Builder = AlertDialog.Builder(edit_icon.context)
            builder.setTitle("Edit review")

            val input = EditText(edit_icon.context)

            input.inputType =
                InputType.TYPE_CLASS_TEXT
            builder.setView(input)


            builder.setPositiveButton("OK",
                DialogInterface.OnClickListener { dialog, which ->

                    var firebase = Firebase.firestore
                    var query=firebase.collection("reviews")
                        .whereEqualTo("user_ID", FirebaseAuth.getInstance().currentUser?.uid)
                        .addSnapshotListener { value, e ->

                            Log.d("review_ID ", "${review_ID}")
                            if (e != null) {
                                Log.w("Firebase failed", "Listen failed.", e)
                                showMessage(itemView,"You can only edit reviews from youw own your own account")

                            }

                            for (doc in value!!) {
                                if (doc.exists()) {
                                    Log.d("F","${doc.reference.id}")
                                    Log.d("YESSS","${input.text.toString()}")
                                    doc.reference.update("review_description", input.text.toString())
                                    notifyDataSetChanged()
                                   break
                                }
                                else {
                                    showMessage(itemView,"You can only edit reviews from youw own your own account")
                                }

                            }

                        }

                })

            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

            builder.show()
        }

        private fun clickDeleteIcon() {
            val builder: AlertDialog.Builder = AlertDialog.Builder(delete_icon.context)
            builder.setTitle("Delete review")


            builder.setPositiveButton("OK",
                DialogInterface.OnClickListener { dialog, which ->

                    var firebase = Firebase.firestore
                    var query=firebase.collection("reviews")
                        .whereEqualTo("user_ID", FirebaseAuth.getInstance().currentUser?.uid)
                        .addSnapshotListener { value, e ->

                            Log.d("firebase ", "${FirebaseAuth.getInstance().currentUser?.uid}")
                            if (e != null) {
                                Log.w("Firebase failed", "Listen failed.", e)
                                showMessage(itemView,"You can only edit reviews from youw own your own account")

                            }


                            for (doc in value!!) {
                                if (doc.exists()) {
                                    doc.reference.delete()
                                    notifyDataSetChanged()
                                }
                                else {
                                    showMessage(itemView,"You can only edit reviews from youw own your own account")
                                }

                            }

                        }


                })
            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

            builder.show()
        }

        private fun showMessage(view: View, message: String) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
        }

        override fun onClick(view: View?) {

        }

    }



}


