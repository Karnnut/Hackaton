package cphack.testkotlin.controller

import cphack.testkotlin.model.ProductHistory
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Callable
import kotlin.collections.ArrayList


class FetchCustomerAPI( search:String ,customerId : String) : Callable<ArrayList<String>>  {
    private val search = search
    private val customerId: String = customerId
    private val baseUrl : String = "10.0.2.2:5050"

    override fun call() : ArrayList<String>  {
        val suggestProducts = ArrayList<String>()
        try {
            val url = String.format("http://%s/%s/%s",baseUrl,this.search,this.customerId)
            println("Send request for ${search} to ${baseUrl} with customer id = ${customerId}")
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.setRequestProperty("x-api-key","9146d632-30b9-11ed-a261-0242ac120002")
            connection.connectTimeout = 5000
            if (connection.responseCode == 200) {
                val responseBody = connection.inputStream.use { it.readBytes() }.toString(Charsets.UTF_8)
                val data: JSONArray = JSONObject(responseBody).getJSONObject("data").getJSONArray("product_list")
                for (i in 0 until data.length()) {
                    suggestProducts.add(data[i].toString())
                }

            }
        }catch (e:Exception) {
            println("Error fetching customer history" )
            e.printStackTrace()
        }
        return suggestProducts

    }

}