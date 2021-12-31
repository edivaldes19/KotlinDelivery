package com.manuel.delivery.routes

import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CategoriesRoutes {
    @Multipart
    @POST(Constants.ROUTE_CATEGORIES_CREATE)
    fun create(
        @Part image: MultipartBody.Part,
        @Part(Constants.PROP_CATEGORY) category: RequestBody,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<ResponseHttp>
}