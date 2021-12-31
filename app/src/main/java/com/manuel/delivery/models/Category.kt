package com.manuel.delivery.models

import com.google.gson.Gson

class Category(var id: String? = null, var name: String, var image: String? = null) {
    override fun toString(): String = name
    fun toJson(): String = Gson().toJson(this)
}