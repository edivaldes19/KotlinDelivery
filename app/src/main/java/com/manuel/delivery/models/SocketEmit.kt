package com.manuel.delivery.models

import com.google.gson.Gson

class SocketEmit(var orderId: String, var latitude: Double, var longitude: Double) {
    override fun toString(): String =
        "SocketEmit(orderId='$orderId', latitude=$latitude, longitude=$longitude)"

    fun toJson(): String = Gson().toJson(this)
}