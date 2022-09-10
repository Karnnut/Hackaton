package cphack.testkotlin.model

data class Product(
    val id: String,
    val name: String,
    val sku: String? = "",
    val urlKey: String? ="",
    val stockStatus: String? ="",
    val stockOnHand: Int? = 0,
    val sellingType: String?  = null,
    val unitOfQuantity: String?  = null,
    val unitOfWeight: String?  = null,
    val weightPerPiece: Double?  = null,
    val finalPricePerUOW: Double?  = null,
    val regularPricePerUOW: Double?  = null,
    val disableLoyaltyPoints: Boolean? = null,
    val loyaltyPoints: Int?  = null,
    val maxQuantityOfProduct: Int?  = null,
    val minQuantityOfProduct: Int?  = null,
    val quantityIncrement: Int?  = null,

    val imageList: ArrayList<String>? = null,
    val priceRange: PriceRange?  = null,
)