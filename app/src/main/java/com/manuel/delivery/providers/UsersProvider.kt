package com.manuel.delivery.providers

import com.manuel.delivery.api.ApiRoutes
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.models.User
import com.manuel.delivery.routes.UsersRoutes
import com.manuel.delivery.utils.Constants
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

class UsersProvider(private val token: String? = null) {
    private var usersRoutes: UsersRoutes? = null
    private var usersRoutesToken: UsersRoutes? = null

    init {
        val api = ApiRoutes()
        usersRoutes = api.getUsersRoutes()
        usersRoutesToken = token?.let { t -> api.getUsersRoutesWithToken(t) }
    }

    fun register(user: User): Call<ResponseHttp>? = usersRoutes?.register(user)
    fun login(email: String, password: String): Call<ResponseHttp>? =
        usersRoutes?.login(email, password)

    fun update(file: File, user: User): Call<ResponseHttp>? {
        val body = RequestBody.create(MediaType.parse(Constants.PROP_IMAGE_CREATE), file)
        val image = MultipartBody.Part.createFormData(Constants.PROP_IMAGE, file.name, body)
        val requestBody =
            RequestBody.create(MediaType.parse(Constants.PROP_TEXT_PLAIN), user.toJson())
        return token?.let { t -> usersRoutesToken?.update(image, requestBody, t) }
    }

    fun updateWithoutImage(user: User): Call<ResponseHttp>? =
        token?.let { t -> usersRoutesToken?.updateWithoutImage(user, t) }
}