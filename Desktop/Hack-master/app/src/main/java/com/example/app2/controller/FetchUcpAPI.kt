package cphack.testkotlin.controller

import cphack.testkotlin.model.Discount
import cphack.testkotlin.model.Price
import cphack.testkotlin.model.PriceRange
import cphack.testkotlin.model.Product
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Callable

// Fetch product from ucp id
class FetchUcpAPI(ucp:String, lang:String) : Callable<Product>{
    private val ucp = ucp
    private val lang = lang
    private val baseUrl : String = "10.0.2.2:5050"
    private val apiKey: String = "9146d632-30b9-11ed-a261-0242ac120002"

    override fun call(): Product {
        val url : String = String.format("http://%s/product/%s/%s",baseUrl , lang , ucp )
        println(url)
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("x-api-key", this.apiKey)
        connection.connectTimeout = 10000

        if (connection.responseCode == 200) {
            val responseBody = connection.inputStream.use { it.readBytes() }.toString(Charsets.UTF_8)
            val jsonObject: JSONObject = JSONObject(responseBody).getJSONObject("data")
            println(jsonObject)
            return parsingJsonObject(jsonObject)
        } else {
            println("Error fetching api http status ${connection.responseCode}")
        }

        return Product("-1","-1")
    }

    //Brute force way
    private fun parsingJsonObject(jsonObject: JSONObject) : Product {

        return Product(
            jsonObject.get("id").toString(),
            jsonObject.get("name").toString(),
            jsonObject.get("sku").toString(),
            if(!jsonObject.isNull("urlKey")) jsonObject.get("urlKey").toString() else "" ,
            if(!jsonObject.isNull("stockStatus") ) jsonObject.get("stockStatus").toString() else "",
            if(!jsonObject.isNull("stockOnHand") ) jsonObject.get("stockOnHand").toString().toInt() else 0,
            if(!jsonObject.isNull("sellingType") ) jsonObject.get("sellingType") as String else "",
            if(!jsonObject.isNull("unitOfQuantity")) jsonObject.get("unitOfQuantity").toString() else "",
            if(!jsonObject.isNull("unitOfWeight")) jsonObject.get("unitOfWeight").toString() else "",
            if(!jsonObject.isNull("weightPerPiece")) jsonObject.get("weightPerPiece").toString().toDouble() else 0.0,
            if(!jsonObject.isNull("finalPricePerUOW")) jsonObject.get("finalPricePerUOW").toString().toDouble() else 0.0,
            if(!jsonObject.isNull("regularPricePerUOW")) jsonObject.get("regularPricePerUOW").toString().toDouble() else 0.0,
            if(!jsonObject.isNull("disableLoyaltyPoints")) jsonObject.get("disableLoyaltyPoints").toString().toBoolean() else false,
            if(!jsonObject.isNull("loyaltyPoints")) jsonObject.get("loyaltyPoints").toString().toInt() else 0,
            if(!jsonObject.isNull("maxQuantityOfProduct")) jsonObject.get("maxQuantityOfProduct").toString().toInt() else 0,
            if(!jsonObject.isNull("minQuantityOfProduct")) jsonObject.get("minQuantityOfProduct").toString().toInt() else 0,
            if(!jsonObject.isNull("quantityIncrement")) jsonObject.get("quantityIncrement").toString().toInt() else 0,
            getImageList(jsonObject),
            getPriceRange(jsonObject)
        )

    }

    private fun getImageList(jsonObject: JSONObject) : ArrayList<String> {
        try{
            val mediaList : JSONArray = jsonObject.get("mediaGallery") as JSONArray
            var imgList : ArrayList<String> = ArrayList()
            for(i in 0 until mediaList.length()){
                val mediaObj: JSONObject = mediaList.get(i) as JSONObject
                imgList.add(mediaObj.get("url").toString())
            }
            return imgList
        }catch (e:Exception) {
            return  ArrayList<String>()
        }

    }

    private fun getPriceRange(jsonObject: JSONObject) : PriceRange? {
        try {
            val itemPriceRange: JSONObject =
                (jsonObject.get("priceRange") as JSONObject).get("minimumPrice") as JSONObject

            val regularPriceObj = itemPriceRange.get("regularPrice") as JSONObject
            val regularPrice: Price = Price(
                regularPriceObj.get("value").toString().toDouble(),
                regularPriceObj.get("currency").toString()
            )

            val finalPriceObj = itemPriceRange.get("finalPrice") as JSONObject
            val finalPrice = Price(
                finalPriceObj.get("value").toString().toDouble(),
                finalPriceObj.get("currency").toString()
            )

            val discountObj = itemPriceRange.get("discount") as JSONObject
            val discount = Discount(
                discountObj.get("amountOff").toString().toDouble(),
                discountObj.get("percentOff").toString().toDouble()
            )

            return PriceRange(regularPrice, finalPrice, discount)
        } catch (e: Exception) {
            return null
        }
    }

}