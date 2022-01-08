package com.manuel.delivery.providers

import com.manuel.delivery.api.ApiRoutes
import com.manuel.delivery.models.Address
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.routes.AddressRoutes
import retrofit2.Call

class AddressProvider(private val token: String? = null) {
    private var addressRoutes: AddressRoutes? = null

    init {
        val api = ApiRoutes()
        addressRoutes = token?.let { t -> api.getAddressRoutesWithToken(t) }
    }

    fun findByUser(idUser: String?): Call<MutableList<Address>>? =
        token?.let { t -> idUser?.let { id -> addressRoutes?.findByUser(id, t) } }

    fun create(address: Address): Call<ResponseHttp>? =
        token?.let { t -> addressRoutes?.create(address, t) }
}