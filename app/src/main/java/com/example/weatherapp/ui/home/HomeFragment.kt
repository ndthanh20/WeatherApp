package com.example.weatherapp.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class HomeFragment : Fragment(),LocationListener {

    private lateinit var homeViewModel: HomeViewModel

    val CITY: String = "Vinh,VN"
    val API: String = "fc85a55c0a7c13c02752704e44205c14"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        //val textView: TextView = root.findViewById(R.id.text_home)
       // homeViewModel.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        //})
        val weathertask = weatherTask(root)
        try{
            val arrresult = weathertask.execute().get()
            var result = arrresult[0]
            var result2 = arrresult[1]
            val jsonObj = JSONObject(result)
            val main = jsonObj.getJSONObject("main")
            val sys = jsonObj.getJSONObject("sys")
            val wind = jsonObj.getJSONObject("wind")
            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
            val updatedAt:Long = jsonObj.getLong("dt")
            Log.i(updatedAt.toString(), "123")
            val updatedAlText = "Updated at: "+ SimpleDateFormat(
                "dd/MM/yyyy hh:mm a",
                Locale.ENGLISH
            ).format((Date((updatedAt) * 1000)))
            val temp = main.getDouble("temp").roundToInt().toString() + "°C"
            val tempMin = "Min Temp: "+ main.getString("temp_min") + "°C"
            val tempMax = "Min Temp: "+ main.getString("temp_max") + "°C"
            val pressure = main.getString("pressure")
            val humidity = main.getString("humidity")
            val sunrise:Long = sys.getLong("sunrise")
            val sunset:Long = sys.getLong("sunset")
            val WindSpeed = wind.getString("speed")
            val feel_like = main.getString("feels_like") + "°C"
            val weatherDescription = weather.getString("description")
            val address = jsonObj.getString("name")+ ", "+sys.getString("country")

            val jsonObj2 = JSONObject(result2)


            root.addresses.text = address
            root.updated_at.text = updatedAlText
            root.statuss.text = weatherDescription.capitalize()
            root.temps.text = temp
            root.temp_min.text = tempMin
            root.temp_max.text = tempMax
            root.sunrises.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
            root.sunsets.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))

            root.winds.text = WindSpeed
            root.pressures.text = pressure
            root.humiditys.text = humidity + "%"
            root.feel_likes.text = feel_like


            val list: MutableList<String> = ArrayList()
            var icon : ImageView = root.img_1
            val listIconView : MutableList<ImageView> = ArrayList()

            for(i in 0..root.hour_layout.childCount step 2) {
                val hour_lt = jsonObj2.getJSONArray("list").getJSONObject(i / 2)
                val hour_main = hour_lt.getJSONObject("main")
                val hour_dt:Long = hour_lt.getLong("dt")
                val weaarr = hour_lt.getJSONArray("weather").getJSONObject(0)
                val iconcode = weaarr.getString("icon")
                list.add(iconcode)
                val hour_daily =  SimpleDateFormat("HH:mm", Locale.ENGLISH).format(
                    (Date(
                        hour_dt * 1000
                    ))
                )
                val hour_temp = hour_main.getDouble("temp").roundToInt().toString() + "°C"
                val  h_layout : LinearLayout = root.hour_layout.getChildAt(i) as LinearLayout
                val temp = h_layout.getChildAt(0)  as TextView
                val hour = h_layout.getChildAt(4) as TextView
                val iconTmp = h_layout.getChildAt(2) as ImageView
                listIconView.add(iconTmp)
                temp.text = hour_temp
                hour.text = hour_daily
            }

            val arrIcon : Array<String> = list.toTypedArray()
            val imageTask = imageTask()
            Log.i(Arrays.toString(arrIcon),"image Task")
            val bitmap: Array<Bitmap?> = imageTask.execute(Arrays.toString(arrIcon)).get()
            for (i in 0..listIconView.size - 1) {
                listIconView[i].setImageBitmap(bitmap[i])
            }


            root.loader.visibility = View.GONE
            root.mainContainer.visibility = View.VISIBLE
        }
        catch (e: Exception)
        {
            Log.e("abbc", e.toString())
            root.loader.visibility = View.GONE
            root.errortext.visibility = View.VISIBLE
        }
        return root
    }
    inner class weatherTask(root: View) : AsyncTask<String, Void, Array<String?>>()
    {
        val roots = root
        override fun onPreExecute() {
            super.onPreExecute()
            roots.mainContainer.visibility = View.GONE
            roots.loader.visibility = View.VISIBLE
            roots.errortext.visibility = View.GONE

        }

        override fun doInBackground(vararg params: String?): Array<String?> {
            var response:String?
            var response2:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API&lang=vi")
                        .readText(Charsets.UTF_8)
                response2 = URL("https://pro.openweathermap.org/data/2.5/forecast/hourly?q=$CITY&units=metric&appid=$API&lang=vi")
                    .readText(Charsets.UTF_8)

            }
            catch (e: Exception)
            {
                Log.e("abc", e.toString())
                response = null
                response2 = null
            }
            return  arrayOf(response, response2)
        }

        /*override fun onPostExecute(arrResult: Array<String?>) {
            super.onPostExecute(arrResult)
            var result = arrResult[0]
            var result2 = arrResult[1]
            try{
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAlText = "Updated at: "+ SimpleDateFormat(
                    "dd/MM/yyyy hh:mm a",
                    Locale.ENGLISH
                ).format((Date(updatedAt * 1000)))
                val temp = main.getDouble("temp").roundToInt().toString() + "°C"
                val tempMin = "Min Temp: "+ main.getString("temp_min") + "°C"
                val tempMax = "Min Temp: "+ main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val WindSpeed = wind.getString("speed")
                val feel_like = main.getString("feels_like") + "°C"
                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name")+ ", "+sys.getString("country")

                val jsonObj2 = JSONObject(result2)


                addresses.text = address
                updated_at.text = updatedAlText
                statuss.text = weatherDescription.capitalize()
                temps.text = temp
                temp_min.text = tempMin
                temp_max.text = tempMax
                sunrises.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
                sunsets.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))

                winds.text = WindSpeed
                pressures.text = pressure
                humiditys.text = humidity + "%"
                feel_likes.text = feel_like


                for(i in 0..hour_layout.childCount step 2) {
                    val hour_lt = jsonObj2.getJSONArray("list").getJSONObject(i / 2)
                    val hour_main = hour_lt.getJSONObject("main")
                    val hour_dt:Long = hour_lt.getLong("dt")
                    val weaarr = hour_lt.getJSONArray("weather").getJSONObject(0)
                    val iconcode = weaarr.getString("icon")
                    var iconurl : URL=  URL("http://openweathermap.org/img/w/" + iconcode + ".png")
                    val hour_daily =  SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                        (Date(
                            hour_dt * 1000
                        ))
                    )
                    val hour_temp = hour_main.getDouble("temp").roundToInt().toString() + "°C"
                    val  h_layout : LinearLayout = hour_layout.getChildAt(i) as LinearLayout
                    val temp = h_layout.getChildAt(0)  as TextView
                    val hour = h_layout.getChildAt(4) as TextView
                    val icon = h_layout.getChildAt(2) as ImageView
                    temp.text = hour_temp
                    hour.text = hour_daily
                    val bmp = BitmapFactory.decodeStream(iconurl.openConnection().getInputStream())
                    icon.setImageBitmap(bmp)
                }


                loader.visibility = View.GONE
                mainContainer.visibility = View.VISIBLE
            }
            catch (e: Exception)
            {
                Log.e("abbc", e.toString())
                loader.visibility = View.GONE
                errortext.visibility = View.VISIBLE
            }
        }*/

    }
    inner class imageTask() : AsyncTask<String,Void,Array<Bitmap?>>(){
        override fun doInBackground(vararg params: String): Array<Bitmap?> {
            val arrbitmap : MutableList<Bitmap?> = ArrayList()
            var tmp = params[0].toString()
            tmp = tmp.substring(1,tmp.length-1)
            val arrStr = tmp.toString().split(", ").toTypedArray()
            for(i in arrStr) {
                var iconurl: URL = URL("https://openweathermap.org/img/w/"+ i +".png")
                var bmp: Bitmap?
                try {
                    bmp = BitmapFactory.decodeStream(iconurl.openConnection().getInputStream())
                } catch (e: Exception) {
                    Log.e("imageerror", e.toString())
                    bmp = null
                }
                arrbitmap.add(bmp)
            }
            return arrbitmap.toTypedArray()
        }

    }

    override fun onLocationChanged(location: Location) {
        TODO("Not yet implemented")
    }
}