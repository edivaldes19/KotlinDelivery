package com.manuel.delivery.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.manuel.delivery.utils.Constants

class Order(
    @SerializedName(Constants.PROP_ID) var id: String? = null,
    @SerializedName(Constants.PROP_ID_CLIENT) var idClient: String,
    @SerializedName(Constants.PROP_ID_DELIVERY) var idDelivery: String? = null,
    @SerializedName(Constants.PROP_ID_ADDRESS) var idAddress: String,
    @SerializedName(Constants.PROP_STATUS) var status: String? = null,
    @SerializedName(Constants.PROP_TIMESTAMP) var timestamp: String? = null,
    @SerializedName("${Constants.PROP_PRODUCT}s") var listOfProducts: MutableList<Product>,
    @SerializedName(Constants.PROP_LATITUDE) var latitude: Double = 0.0,
    @SerializedName(Constants.PROP_LONGITUDE) var longitude: Double = 0.0,
    @SerializedName(Constants.PROP_CLIENT) var client: User? = null,
    @SerializedName(Constants.PROP_DELIVERY) var delivery: User? = null,
    @SerializedName(Constants.PROP_ADDRESS) var address: Address? = null
) {
    override fun toString(): String =
        "Order(id=$id, idClient='$idClient', idDelivery=$idDelivery, idAddress='$idAddress', status=$status, timestamp=$timestamp, listOfProducts=$listOfProducts, client=$client, delivery=$delivery, address=$address)"

    fun toJson(): String = Gson().toJson(this)
}