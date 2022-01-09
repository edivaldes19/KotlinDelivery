package com.manuel.delivery.api

import com.manuel.delivery.routes.*
import com.manuel.delivery.utils.Constants

class ApiRoutes {
    private val retrofit = RetrofitClient()
    fun getUsersRoutes(): UsersRoutes =
        retrofit.getClient(Constants.API_URL).create(UsersRoutes::class.java)

    fun getUsersRoutesWithToken(token: String): UsersRoutes =
        retrofit.getClientWithToken(Constants.API_URL, token).create(UsersRoutes::class.java)

    fun getCategoriesRoutesWithToken(token: String): CategoriesRoutes =
        retrofit.getClientWithToken(Constants.API_URL, token).create(CategoriesRoutes::class.java)

    fun getProductsRoutesWithToken(token: String): ProductsRoutes =
        retrofit.getClientWithToken(Constants.API_URL, token).create(ProductsRoutes::class.java)

    fun getAddressRoutesWithToken(token: String): AddressRoutes =
        retrofit.getClientWithToken(Constants.API_URL, token).create(AddressRoutes::class.java)

    fun getOrdersRoutesWithToken(token: String): OrdersRoutes =
        retrofit.getClientWithToken(Constants.API_URL, token).create(OrdersRoutes::class.java)
}