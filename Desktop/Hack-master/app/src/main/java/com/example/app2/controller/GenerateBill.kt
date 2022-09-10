package cphack.testkotlin.controller

import cphack.testkotlin.model.Product

class GenerateBill(prodctList: ArrayList<Product> ) {
    val productList = prodctList

    var billSummary:String = ""
    var billSumWithDiscount : Double = 0.0;
    var billSumFullPrice : Double = 0.0 ;
    init{

        val billMap = java.util.HashMap<Product, Int>()
        productList.forEach {
            if( billMap.containsKey(it)){
                billMap.put(it , billMap.get(it) as Int + 1 )
            }else{
                billMap.put(it , 1)
            }
        }

        val productBill  = HashSet(productList).toArray()

        for(element in productBill) {
            val tmpProduct: Product = element as Product
            val amount = billMap.get(tmpProduct)
            val sum = tmpProduct.priceRange?.finalPrice?.value as Double * amount as Int
            val fullSum = tmpProduct.priceRange?.regularPrice?.value as Double * amount as Int
            val tmpString : String = String.format("%s x%s\t%.2f\n", tmpProduct.name, amount , sum  )
            billSumWithDiscount += sum
            billSumFullPrice += fullSum
            this.billSummary += tmpString ;
        }
    }


}