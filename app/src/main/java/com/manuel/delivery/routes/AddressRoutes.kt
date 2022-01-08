package com.manuel.delivery.routes

import com.manuel.delivery.models.Address
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.utils.Constants
import retrofit2.Call
import retrofit2.http.*

interface AddressRoutes {
    @GET(Constants.ROUTE_ADDRESS_FIND_BY_USER)
    fun findByUser(
        @Path(Constants.PROP_ID_USER) idUser: String,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<MutableList<Address>>

    @POST(Constants.ROUTE_ADDRESS_CREATE)
    fun create(
        @Body address: Address,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<ResponseHttp>
}