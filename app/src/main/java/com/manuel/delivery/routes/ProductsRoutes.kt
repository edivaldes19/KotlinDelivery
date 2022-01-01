package com.manuel.delivery.routes

import com.manuel.delivery.models.Product
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ProductsRoutes {
    @GET(Constants.ROUTE_PRODUCTS_FIND_BY_CATEGORY)
    fun findByCategory(
        @Path(Constants.PROP_ID_CATEGORY) idCategory: String,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<MutableList<Product>>

    @Multipart
    @POST(Constants.ROUTE_PRODUCTS_CREATE)
    fun create(
        @Part images: Array<MultipartBody.Part?>,
        @Part(Constants.PROP_PRODUCT) product: RequestBody,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<ResponseHttp>
}