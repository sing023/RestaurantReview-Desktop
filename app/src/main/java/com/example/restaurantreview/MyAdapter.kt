package com.example.restaurantreview
/*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.io.Serializable
import java.util.*


class MyAdapter (private val imageModelArrayList: MutableList<Restaurants>) : RecyclerView.Adapter<MyAdapter.ViewHolder>(),Serializable {
    private var context: Context? = null
    var selectedPos =0
    var address_for_location:String=""


    fun MyAdapter(context: Context?) {
        this.context = context
    }
    /*
     * Inflate our views using the layout defined in row_layout.xml
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.row_layout, parent, false)

        return ViewHolder(v)
    }

    /*
     * Bind the data to the child views of the ViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val info = imageModelArrayList[position]
        address_for_location=imageModelArrayList[position].address.toString()
        val image_url=info.image


        print(image_url)
        Picasso
            .get()
            .load(image_url)
            .resize(80,120)
            .placeholder(R.drawable.profile_pic)
            .error(R.drawable.profile_pic)
            .noFade()
            .into(holder.imgView)


        //holder.imgView.setImageResource(info.getImages())
        //holder.imgView=info.getImages()
        holder.txtMsg.text = info.name
        val a= arrayOf("This is the description for each restaurant ","Please rate:","Address for each restaurant","Overall Ratings:        / 5.0")
        holder.restaurantDescription.text=info.description
        holder.s1.setText("Please rate:")
        holder.address.text=info.address
        holder.ratings.text="Overall Ratings:    "+"${info.overall_ratings.toString()}" +"  / 5.0"
    }

    /*
     * Get the maximum size of the
     */
    override fun getItemCount(): Int {
        return imageModelArrayList.size
    }

    private fun passData(res: Restaurants) {
        val intent = Intent(context, Activity2::class.java)
        intent.putExtra("restaurantModel", res)
        context!!.startActivity(intent)
    }

    /*
     * The parent class that handles layout inflation and child view use
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener, Serializable{

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
                val uri: String =
                    java.lang.String.format(Locale.ENGLISH, "geo:0,0?q=$address_for_location")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                context!!.startActivity(intent)

            })

            //itemView.setOnClickListener(this)
            context = itemView.getContext();
            itemView.setOnClickListener(View.OnClickListener() {

                onClick(itemView)
            })

            itemView.setOnLongClickListener(View.OnLongClickListener(){

                onLongClick(itemView)
            })

        }

        override fun onClick(v: View) {
            val clicked_postion=adapterPosition
            Log.d("position","$clicked_postion")


            val restaurantModel: Restaurants = imageModelArrayList[clicked_postion]
            passData(restaurantModel)
        }


        override fun onLongClick(v: View): Boolean {
            val msg = txtMsg.text
            val snackbar = Snackbar.make(v, "$msg" + R.string.msg, Snackbar.LENGTH_LONG)
            snackbar.show()

            notifyItemChanged( selectedPos)
            selectedPos = layoutPosition
            notifyItemChanged( selectedPos)
            return true
        }


    }
}



*/

