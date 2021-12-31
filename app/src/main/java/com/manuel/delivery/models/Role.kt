package com.manuel.delivery.models

import com.google.gson.annotations.SerializedName
import com.manuel.delivery.utils.Constants

class Role(
    @SerializedName(Constants.PROP_ID) var id: String,
    @SerializedName(Constants.PROP_NAME) var roleName: String,
    @SerializedName(Constants.PROP_IMAGE) var image: String,
    @SerializedName(Constants.PROP_ROUTE) var route: String
) {
    override fun toString(): String =
        "Rol(id='$id', rolName='$roleName', image='$image', route='$route')"
}