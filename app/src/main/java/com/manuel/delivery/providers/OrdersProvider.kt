package com.manuel.delivery.providers

import com.manuel.delivery.api.ApiRoutes
import com.manuel.delivery.models.Order
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.routes.OrdersRoutes
import retrofit2.Call

class OrdersProvider(private val token: String? = null) {
    private var ordersRoutes: OrdersRoutes? = null

    init {
        val api = ApiRoutes()
        ordersRoutes = token?.let { t -> api.getOrdersRoutesWithToken(t) }
    }

    fun findByStatus(status: String): Call<MutableList<Order>>? =
        token?.let { t -> ordersRoutes?.findByStatus(status, t) }

    fun findByIdClientAndStatus(idClient: String, status: String): Call<MutableList<Order>>? =
        token?.let { t -> ordersRoutes?.findByIdClientAndStatus(idClient, status, t) }

    fun create(order: Order): Call<ResponseHttp>? =
        token?.let { t -> ordersRoutes?.create(order, t) }
}