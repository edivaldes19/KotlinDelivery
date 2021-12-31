package com.manuel.delivery.routes

import com.manuel.delivery.models.Category
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CategoriesRoutes {
    @GET(Constants.ROUTE_CATEGORIES_GET_ALL)
    fun getAll(@Header(Constants.PROP_AUTHORIZATION) token: String): Call<MutableList<Category>>

    @Multipart
    @POST(Constants.ROUTE_CATEGORIES_CREATE)
    fun create(
        @Part image: MultipartBody.Part,
        @Part(Constants.PROP_CATEGORY) category: RequestBody,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<ResponseHttp>
}