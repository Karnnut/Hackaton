package cphack.testkotlin.controller

import cphack.testkotlin.model.Product
import cphack.testkotlin.model.Discount
import cphack.testkotlin.model.Price
import cphack.testkotlin.model.PriceRange
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Callable
import org.json.JSONObject



class FetchProductAPI(productID: String, lang:String) : Callable<Product>  {

    private var productID:String = productID
    private var auth:String = "Basic ZWY4ZTZjMjgzODdlNGVjYTlkM2UxMTU1MDQxMjgyYzE6MEU1NTg0QzUxZTdBNDBEODkzMDUxZGExY2NEQTg2ZTY="
    private var lang:String = lang


    override fun call() : Product {
        println("Calling fetch api Product ${productID} Language ${lang.uppercase()}")

        val url = String.format(
            "https://ppe-api.lotuss.com/proc/product/api/v1/products/details?websiteCode=thailand_hy&sku=%s&storeId=5016",
            this.productID
        )

        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("accept-language", this.lang)
        connection.setRequestProperty("authorization", this.auth)

        if (connection.responseCode == 200) {
            val responseBody = connection.inputStream.use { it.readBytes() }.toString(Charsets.UTF_8)
            val jsonObject: JSONObject = JSONObject(responseBody).getJSONObject("data")
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

    private fun getPriceRange(jsonObject: JSONObject) : PriceRange?{
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
        }catch (e:Exception){
            return null
        }
    }

}