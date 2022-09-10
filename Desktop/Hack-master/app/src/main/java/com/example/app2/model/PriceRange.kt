package cphack.testkotlin.model

data class PriceRange(
    val regularPrice : Price ,
    val finalPrice: Price ,
    val discount: Discount
)