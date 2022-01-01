package com.manuel.delivery.providers

import com.manuel.delivery.api.ApiRoutes
import com.manuel.delivery.models.Product
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.routes.ProductsRoutes
import com.manuel.delivery.utils.Constants
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

class ProductsProvider(private val token: String? = null) {
    private var productsRoutes: ProductsRoutes? = null

    init {
        val api = ApiRoutes()
        productsRoutes = token?.let { t -> api.getProductsRoutesWithToken(t) }
    }

    fun findByCategory(idCategory: String): Call<MutableList<Product>>? =
        token?.let { t -> productsRoutes?.findByCategory(idCategory, t) }

    fun create(files: MutableList<File>, product: Product): Call<ResponseHttp>? {
        val images = arrayOfNulls<MultipartBody.Part>(files.size)
        files.forEachIndexed { i, f ->
            val body = RequestBody.create(MediaType.parse(Constants.PROP_IMAGE_CREATE), f)
            images[i] = MultipartBody.Part.createFormData(Constants.PROP_IMAGE, f.name, body)
        }
        val requestBody =
            RequestBody.create(MediaType.parse(Constants.PROP_TEXT_PLAIN), product.toJson())
        return token?.let { t -> productsRoutes?.create(images, requestBody, t) }
    }
}