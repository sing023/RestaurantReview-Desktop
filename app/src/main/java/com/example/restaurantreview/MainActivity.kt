package com.example.restaurantreview



import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import com.squareup.okhttp.*
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    // Initialize FireStore
    var firebase = Firebase.firestore
    private lateinit var listOfRestaurants: ArrayList<Restaurants> // for firestore

    var resList=arrayListOf<Restaurants>()
    var fullList=arrayListOf<Restaurants>()
    var myadapter:MainAdapter? =MainAdapter(resList)





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_main)
        val recyclerView =
            findViewById<View>(R.id.my_recycler_view) as RecyclerView // Bind to the recyclerview in the layout

        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) // Get the layout manager
        recyclerView.layoutManager = layoutManager

        fetchJson()



    }


     suspend fun fetchJson2()= withContext(Dispatchers.IO) {

        val url = "https://mocki.io/v1/c9384354-11f1-45f6-9621-f874d47ad883"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            val recyclerView =
                findViewById<View>(R.id.my_recycler_view) as RecyclerView // Bind to the recyclerview in the layout
            override fun onResponse(response: Response?) {
                val body = response?.body()?.string()

                val gson = GsonBuilder().create()
                val restaurantList:Array<Restaurants> = gson.fromJson(body, Array<Restaurants>::class.java)
                Log.d("EC", "${restaurantList.size}")
                val restaurantList2:ArrayList<Restaurants> = restaurantList.toCollection(ArrayList())

                resList=restaurantList2
                fullList=restaurantList2


            }

            override fun onFailure(request: Request?, e: IOException?) {
                print("Failed to execute request")
            }

        })

    }




     fun fetchJson() {
        val url = "https://mocki.io/v1/c9384354-11f1-45f6-9621-f874d47ad883"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            val recyclerView =
                findViewById<View>(R.id.my_recycler_view) as RecyclerView // Bind to the recyclerview in the layout
            override fun onResponse(response: Response?) {
                val body = response?.body()?.string()

                val gson = GsonBuilder().create()
                val restaurantList:Array<Restaurants> = gson.fromJson(body, Array<Restaurants>::class.java)
                Log.d("EC", "${restaurantList.size}")
                val restaurantList2:ArrayList<Restaurants> = restaurantList.toCollection(ArrayList())

                runOnUiThread {
                    recyclerView.adapter = MainAdapter(restaurantList2)
                    runBlocking { fetchJson2() }
                }


            }

            override fun onFailure(request: Request?, e: IOException?) {
                print("Failed to execute request")
            }

        })
    }




    //get Restaurants Data from firebase
    private fun EventChangeListener() {
        firebase = FirebaseFirestore.getInstance()
        firebase.collection("restaurants").addSnapshotListener(object : EventListener,
            com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?,
            ) {

                if (error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {

                    if (dc.type == DocumentChange.Type.ADDED) {
                        Log.d("FKK", "${dc.document.toObject(Restaurants::class.java)}")
                        listOfRestaurants.add(dc.document.toObject(Restaurants::class.java))
                    }

                }
                myadapter?.notifyDataSetChanged()
            }

        })

    }


    //  for toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate((R.menu.toolbar_layout), menu)

        var menuItem = menu!!.findItem(R.id.action_search)
        var searchView = menuItem?.actionView as SearchView?

        searchView?.maxWidth = Int.MAX_VALUE
        if (searchView ==null ) {
            Log.d("SEARCGVIEW IS NULL", "FKKKK")
        }

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                runOnUiThread(java.lang.Runnable {
                    val temp=filter2(query!!)

                    Log.d("BB","${temp[0].restaurant_name}")
                    Log.d("BBB","${temp.size}") //Correct ADy
                    resList.clear()
                    resList.addAll(temp)
                    Log.d("BB2","${resList[0].restaurant_name}")
                    Log.d("BBB2","${resList.size}") //Correct ADy
                    MainAdapter(resList).notifyItemInserted(0)
                    MainAdapter(resList).notifyDataSetChanged()
                    val recyclerView =
                        findViewById<View>(R.id.my_recycler_view) as RecyclerView
                    recyclerView.adapter= MainAdapter(fullList) // Very Importamt
                    finishActivity(200)

                })
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {

                Log.d("onQueryTextChange", "query: " + query)

                return true
            }

        })


       return super.onCreateOptionsMenu(menu)
        //return true;
    }

    fun filter2(text: String):ArrayList<Restaurants> {

        // creating a new array list to filter our data.
        var forSaved: ArrayList<Restaurants> = ArrayList()
        val filteredlist: ArrayList<Restaurants> = ArrayList()
        runBlocking { fetchJson2() }
        if (resList.isEmpty()) {

            Log.d("RESLIST IS EMPTY","Empty")
        }

        // running a for loop to compare elements.
        for (item in resList!!) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.restaurant_name?.lowercase()!!?.contains(text.lowercase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Log.d("FILTEREDLIST IS EMPTY","Empty")
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            Log.d("FILTEREDLISTIS NOTEMPTY","Empty1")
            forSaved=MainAdapter(filteredlist)?.filterList(filteredlist)

            Log.d("Empty2","Empty2")

        }
        Log.d("MainRES","${resList[0].restaurant_name}")
        return forSaved
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // val myView = findViewById<View>(R.id.main_toolbar)
        Log.d("InHERE", "not fk")

        when (item.itemId){

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

            R.id.back_to_main -> {
                runOnUiThread(java.lang.Runnable {
                    val recyclerView =
                        findViewById<View>(R.id.my_recycler_view) as RecyclerView
                    recyclerView.adapter= MainAdapter(resList) // Very Importamt
                    finishActivity(200)

                })
            }
        }
        return super.onOptionsItemSelected(item)
    }



    fun openActivity2(view: View) {
        val intent = Intent(this, Activity2::class.java)
        startForResult.launch(intent)
    }

    public val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val msgExtra = intent?.extras
                val msgText = msgExtra?.getString("returnkey1")


            }
        }

}

class RestaurantList(val imageModelArrayList:List<Restaurants>)

/*
public fun getRestaurants(testList:ArrayList<String>) {

    Ion.with(this).load("GET","https://mocki.io/v1/172505ba-64a3-4de3-9c00-0e539aa72921")
        .asString().setCallback { ex, result ->
            val result_Object2 = JSONObject(result)

            val result_Object:JSONArray =JSONArray(result_Object2)

            for (i in 0..4) {
                val jasonobject = result_Object2.toJSONArray(result_Object).getJSONObject(i).getString("restaurant_name")
                Log.d("QW", "$jasonobject")
                testList.add(jasonobject.toString())
            }

        }

}*/