package com.manuel.delivery.providers

import com.manuel.delivery.api.ApiRoutes
import com.manuel.delivery.models.Category
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.routes.CategoriesRoutes
import com.manuel.delivery.utils.Constants
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

class CategoriesProvider(private val token: String? = null) {
    private var categoriesRoutes: CategoriesRoutes? = null

    init {
        val api = ApiRoutes()
        categoriesRoutes = token?.let { t -> api.getCategoriesRoutesWithToken(t) }
    }

    fun create(file: File, category: Category): Call<ResponseHttp>? {
        val body = RequestBody.create(MediaType.parse(Constants.PROP_IMAGE_CREATE), file)
        val image = MultipartBody.Part.createFormData(Constants.PROP_IMAGE, file.name, body)
        val requestBody =
            RequestBody.create(MediaType.parse(Constants.PROP_TEXT_PLAIN), category.toJson())
        return token?.let { t -> categoriesRoutes?.create(image, requestBody, t) }
    }
}