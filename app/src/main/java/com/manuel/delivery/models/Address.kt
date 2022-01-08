package com.manuel.delivery.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.manuel.delivery.utils.Constants

class Address(
    @SerializedName(Constants.PROP_ID) var id: String? = null,
    @SerializedName(Constants.PROP_ID_USER) var idUser: String? = null,
    @SerializedName(Constants.PROP_ADDRESS) var address: String,
    @SerializedName(Constants.PROP_SUBURB) var suburb: String,
    @SerializedName(Constants.PROP_LATITUDE) var latitude: Double,
    @SerializedName(Constants.PROP_LONGITUDE) var longitude: Double
) {
    override fun toString(): String =
        "Address(id=$id, idUser=$idUser, address='$address', suburb='$suburb', latitude=$latitude, longitude=$longitude)"

    fun toJson(): String = Gson().toJson(this)
}