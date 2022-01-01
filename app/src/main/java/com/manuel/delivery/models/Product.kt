package com.manuel.delivery.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.manuel.delivery.utils.Constants

class Product(
    @SerializedName(Constants.PROP_ID) var id: String? = null,
    @SerializedName(Constants.PROP_NAME) var name: String,
    @SerializedName(Constants.PROP_DESCRIPTION) var description: String,
    @SerializedName(Constants.PROP_PRICE) var price: Double,
    @SerializedName(Constants.PROP_AMOUNT) var amount: Int = 1,
    @SerializedName("${Constants.PROP_IMAGE}1") var image1: String? = null,
    @SerializedName("${Constants.PROP_IMAGE}2") var image2: String? = null,
    @SerializedName("${Constants.PROP_IMAGE}3") var image3: String? = null,
    @SerializedName(Constants.PROP_ID_CATEGORY) var idCategory: String
) {
    override fun toString(): String =
        "Product(id='$id', name='$name', description='$description', price=$price, amount=$amount, image1='$image1', image2='$image2', image3='$image3', idCategory='$idCategory')"

    fun toJson(): String = Gson().toJson(this)
}