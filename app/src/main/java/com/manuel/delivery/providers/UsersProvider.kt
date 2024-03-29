package com.manuel.delivery.providers

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.manuel.delivery.api.ApiRoutes
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.models.User
import com.manuel.delivery.routes.UsersRoutes
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UsersProvider(private val token: String? = null) {
    private var usersRoutes: UsersRoutes? = null
    private var usersRoutesToken: UsersRoutes? = null

    init {
        val api = ApiRoutes()
        usersRoutes = api.getUsersRoutes()
        usersRoutesToken = token?.let { t -> api.getUsersRoutesWithToken(t) }
    }

    fun createToken(user: User, context: Context, view: View) {
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val mySharedPreferences = MySharedPreferences(context)
                val token = task.result
                user.apply { notificationToken = token }
                mySharedPreferences.saveData(Constants.PROP_USER, user)
                updateNotificationToken(user)?.enqueue(object : Callback<ResponseHttp> {
                    override fun onResponse(
                        call: Call<ResponseHttp>,
                        response: Response<ResponseHttp>
                    ) {
                        response.body()?.let { responseHttp ->
                            if (!responseHttp.isSuccess) {
                                Snackbar.make(view, responseHttp.message, Snackbar.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                        Snackbar.make(view, t.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    fun findDeliveryMan(): Call<MutableList<User>>? =
        token?.let { t -> usersRoutesToken?.findDeliveryMan(t) }

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

    private fun updateNotificationToken(user: User): Call<ResponseHttp>? =
        token?.let { t -> usersRoutesToken?.updateNotificationToken(user, t) }
}