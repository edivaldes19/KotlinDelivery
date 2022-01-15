package com.manuel.delivery.routes

import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.models.User
import com.manuel.delivery.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UsersRoutes {
    @GET(Constants.ROUTE_USERS_FIND_DELIVERY_MAN)
    fun findDeliveryMan(@Header(Constants.PROP_AUTHORIZATION) token: String): Call<MutableList<User>>

    @POST(Constants.ROUTE_USERS_CREATE)
    fun register(@Body user: User): Call<ResponseHttp>

    @FormUrlEncoded
    @POST(Constants.ROUTE_USERS_LOGIN)
    fun login(
        @Field(Constants.PROP_EMAIL) email: String,
        @Field(Constants.PROP_PASSWORD) password: String
    ): Call<ResponseHttp>

    @Multipart
    @PUT(Constants.ROUTE_USERS_UPDATE)
    fun update(
        @Part image: MultipartBody.Part,
        @Part(Constants.PROP_USER) user: RequestBody,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<ResponseHttp>

    @PUT(Constants.ROUTE_USERS_UPDATE_WITHOUT_IMAGE)
    fun updateWithoutImage(
        @Body user: User,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<ResponseHttp>

    @PUT(Constants.ROUTE_USERS_UPDATE_NOTIFICATION_TOKEN)
    fun updateNotificationToken(
        @Body user: User,
        @Header(Constants.PROP_AUTHORIZATION) token: String
    ): Call<ResponseHttp>
}