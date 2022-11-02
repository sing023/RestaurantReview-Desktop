package com.example.restaurantreview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.Serializable
import java.util.*

class MainAdapter(var restaurantList: ArrayList<Restaurants>) : RecyclerView.Adapter<MainAdapter.CustomViewHolder>(), Serializable, Filterable{

    var restaurantFilterList = ArrayList<Restaurants>()

    var resList=arrayListOf<Restaurants>()



    private var context: Context? = null
    var selectedPos =0
    var address_for_location:String=""

    init {
        this.restaurantFilterList = restaurantList
    }

    // method for filtering our recyclerview items.
    fun filterList(filterllist: ArrayList<Restaurants>):ArrayList<Restaurants> {
        Log.d("Empty3","Empty3")

        // below line is to add our filtered
        // list in our course array list.
        restaurantFilterList = filterllist
        // below line is to notify our adapter
        // as change in recycler view data.
        Log.d("RR","${restaurantFilterList[0].restaurant_name}")
        Log.d("RRR","${restaurantFilterList.size}") //Correct ADy

        notifyDataSetChanged()
        return restaurantFilterList
    }


    /*
     * Inflate our views using the layout defined in row_layout.xml
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.row_layout, parent, false)

        return CustomViewHolder(v)
    }



    /*
     * Bind the data to the child views of the ViewHolder
     */
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {


        holder.itemView.setOnClickListener(View.OnClickListener() {

            val clicked_postion=position
            Log.d("position","$clicked_postion")


            val restaurantModel: Restaurants = restaurantFilterList[clicked_postion]
            Log.d("restaurantModel","$restaurantModel")
            passData(restaurantModel,holder.itemView)

        })


        val info = restaurantFilterList[position]


        val image_url="https://maps.googleapis.com/maps/api/place/photo?maxwidth=10&maxheight=10&photo_reference=${info.restaurant_link}"
        Log.d("EA", "${image_url}")



        print(image_url)
        Picasso
            .get()
            .load(image_url)
            .resize(80,120)
            .placeholder(R.drawable.profile_pic)
            .error(R.drawable.profile_pic)
            .noFade()
            .into(holder.imgView)

        holder.txtMsg.text = info.restaurant_name
        val a= arrayOf("This is the description for each restaurant ","Please rate:","Address for each restaurant","Overall Ratings:        / 5.0")
        holder.restaurantDescription.text=info.features
        holder.s1.setText("Please rate:")
        holder.address.text=info.address
        holder.ratings.text="Overall Ratings:    "+"${info.avg_rating.toString()}" +"  / 5.0"
    }

    /*
     * Get the maximum size of the
     */
    override fun getItemCount(): Int {
        return restaurantFilterList.size
    }


    private fun passData(res: Restaurants,view:View) {
        val intent = Intent(view.context, Activity2::class.java)
        intent.putExtra("restaurantModel", res)
        view.context!!.startActivity(intent)
    }

    /*
     * The parent class that handles layout inflation and child view use
     */
    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        Serializable {

        var row_linearlayout: LinearLayout? = null
        var imgView = itemView.findViewById<View>(R.id.imgView) as ImageView
        var txtMsg = itemView.findViewById<View>(R.id.firstLine) as TextView
        var restaurantDescription=itemView.findViewById<View>(R.id.description) as TextView
        var s1=itemView.findViewById<View>(R.id.please_rate) as TextView
        var address=itemView.findViewById<View>(R.id.address) as TextView
        var ratings=itemView.findViewById<View>(R.id.given_ratings) as TextView
        var map=itemView.findViewById<View>(R.id.location_icon) as ImageView

        init {
            map.setOnClickListener(View.OnClickListener {
                val address_for_location =restaurantFilterList[position].address.toString()
                Log.d("AdDDREZS", "${address_for_location}")
                val uri: String =
                    java.lang.String.format(Locale.ENGLISH, "geo:0,0?q=$address_for_location")
                Log.d("URI", "${uri}")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                itemView.context!!.startActivity(intent)

            })
        }


    }


    suspend fun fetchJson2()= withContext(Dispatchers.IO) {

        val url = "https://mocki.io/v1/c9384354-11f1-45f6-9621-f874d47ad883"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(response: Response?) {
                val body = response?.body()?.string()

                val gson = GsonBuilder().create()
                val restaurantList:Array<Restaurants> = gson.fromJson(body, Array<Restaurants>::class.java)
                Log.d("EC", "${restaurantList.size}")
                val restaurantList2:ArrayList<Restaurants> = restaurantList.toCollection(ArrayList())

                resList=restaurantList2


            }

            override fun onFailure(request: Request?, e: IOException?) {
                print("Failed to execute request")
            }

        })

    }


    override  fun getFilter(): Filter {

        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {


                val filterResults = FilterResults()

                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    Log.d("CHARSEARCH ERMPTY","HERERERERERERE")
                    restaurantFilterList = restaurantList as ArrayList<Restaurants>
                } else {
                    Log.d("CHARSEARCH NOT ERMPTY","HERERERERERERE")
                    val resultList = ArrayList<Restaurants>()
                    runBlocking {
                        val deferredDoc = async {
                            fetchJson2()
                        }
                        val doc = deferredDoc.await()


                    }
                    if (resList == null)  {
                        Log.d("RESLIST IS NULL", "IS EMpty")
                    }
                    Log.d("RESLIST NOT  NULL", "IS EMpty")
                    Log.d("RESLIST SIZE","${resList.size}")
                    for (row in resList) {
                        if (row.restaurant_name?.lowercase()?.contains(constraint.toString().lowercase())==true) {
                            resultList.add(row)
                        }
                    }
                    restaurantFilterList = resultList
                }

                filterResults.values = restaurantFilterList
                Log.d("S1","HERERERERERERE")
                Log.d("RFL","${restaurantFilterList[0].restaurant_name}")

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                var restaurantFilterList2 = results?.values as ArrayList<Restaurants>
                Log.d("S2","HERERERERERERE")
                restaurantFilterList.clear()
                restaurantFilterList.addAll(restaurantFilterList2)

                notifyDataSetChanged()

            }

        }
    }

}


