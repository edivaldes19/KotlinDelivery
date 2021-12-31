package com.manuel.delivery.models

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.manuel.delivery.utils.Constants

class ResponseHttp(
    @SerializedName(Constants.PROP_MESSAGE) var message: String,
    @SerializedName(Constants.PROP_SUCCESS) var isSuccess: Boolean,
    @SerializedName(Constants.PROP_DATA) var data: JsonObject,
    @SerializedName(Constants.PROP_ERROR) var error: String
) {
    override fun toString(): String =
        "ResponseHttp(message='$message', isSuccess=$isSuccess, data=$data, error='$error')"
}