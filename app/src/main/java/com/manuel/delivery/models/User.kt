package com.manuel.delivery.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.manuel.delivery.utils.Constants

class User(
    @SerializedName(Constants.PROP_ID) var id: String? = null,
    @SerializedName(Constants.PROP_EMAIL) var email: String,
    @SerializedName(Constants.PROP_NAME) var name: String,
    @SerializedName(Constants.PROP_LASTNAME) var lastname: String,
    @SerializedName(Constants.PROP_PHONE) var phone: String,
    @SerializedName(Constants.PROP_IMAGE) var image: String? = null,
    @SerializedName(Constants.PROP_PASSWORD) var password: String,
    @SerializedName(Constants.PROP_IS_AVAILABLE) var isAvailable: Boolean? = null,
    @SerializedName(Constants.PROP_SESSION_TOKEN) var sessionToken: String? = null,
    @SerializedName("${Constants.PROP_ROLE}s") var listOfRoles: MutableList<Role>? = null
) {
    override fun toString(): String = "$name $lastname"
    fun toJson(): String = Gson().toJson(this)
}