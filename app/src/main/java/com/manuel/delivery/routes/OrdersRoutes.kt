package com.manuel.delivery.routes

import com.manuel.delivery.models.Order
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.utils.Constants
import retrofit2.Call
import retrofit2.http.*

interface OrdersRoutes {
    @GET(Constants.ROUTE_ORDERS_FIND_BY_STATUS)
    fun findByStatus(
        @Path(Constants.PROP_STATUS) status: String,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<MutableList<Order>>

    @GET(Constants.ROUTE_ORDERS_FIND_BY_ID_CLIENT_AND_STATUS)
    fun findByIdClientAndStatus(
        @Path(Constants.PROP_ID_CLIENT) idClient: String,
        @Path(Constants.PROP_STATUS) status: String,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<MutableList<Order>>

    @GET(Constants.ROUTE_ORDERS_FIND_BY_ID_DELIVERY_AND_STATUS)
    fun findByIdDeliveryAndStatus(
        @Path(Constants.PROP_ID_DELIVERY) idDelivery: String,
        @Path(Constants.PROP_STATUS) status: String,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<MutableList<Order>>

    @POST(Constants.ROUTE_ORDERS_CREATE)
    fun create(
        @Body order: Order,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<ResponseHttp>

    @PUT(Constants.ROUTE_ORDERS_UPGRADE_TO_READY)
    fun upgradeToReady(
        @Body order: Order,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<ResponseHttp>

    @PUT(Constants.ROUTE_ORDERS_UPGRADE_TO_ON_THE_WAY)
    fun upgradeToOnTheWay(
        @Body order: Order,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<ResponseHttp>

    @PUT(Constants.ROUTE_ORDERS_UPGRADE_TO_DELIVERED)
    fun upgradeToDelivered(
        @Body order: Order,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<ResponseHttp>

    @PUT(Constants.ROUTE_ORDERS_UPDATE_LATITUDE_AND_LONGITUDE)
    fun updateLatitudeAndLongitude(
        @Body order: Order,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<ResponseHttp>
}