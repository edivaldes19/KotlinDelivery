package com.manuel.delivery.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.manuel.delivery.R
import com.manuel.delivery.activities.client.home.ClientHomeActivity
import com.manuel.delivery.activities.delivery.home.DeliveryHomeActivity
import com.manuel.delivery.activities.restaurant.home.RestaurantHomeActivity
import com.manuel.delivery.databinding.ActivityMainBinding
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.models.User
import com.manuel.delivery.providers.UsersProvider
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences
import com.manuel.delivery.utils.TextWatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var usersProvider = UsersProvider()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Delivery)
        getUserInSession()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TextWatchers.validateFieldsAsYouType(
            this,
            binding.eFabLogin,
            binding.etEmail,
            binding.etPassword
        )
        binding.tvRegisterHere.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
        binding.eFabLogin.setOnClickListener { login() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun login() {
        if (TextWatchers.isEmailValid(binding.etEmail.text.toString().trim())) {
            usersProvider.login(
                binding.etEmail.text.toString().trim(),
                binding.etPassword.text.toString().trim()
            )?.enqueue(object : Callback<ResponseHttp> {
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    response.body()?.let { responseHttp ->
                        if (responseHttp.isSuccess) {
                            saveUserSession(responseHttp.data.toString())
                            Toast.makeText(
                                this@MainActivity,
                                responseHttp.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Snackbar.make(
                                binding.root,
                                responseHttp.message,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Snackbar.make(binding.root, t.message.toString(), Snackbar.LENGTH_SHORT)
                        .show()
                }
            })
        } else {
            Snackbar.make(
                binding.root,
                getString(R.string.invalid_email),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun getUserInSession() {
        val mySharedPreferences = MySharedPreferences(this)
        if (!mySharedPreferences.getData(Constants.PROP_USER).isNullOrBlank()) {
            if (!mySharedPreferences.getData(Constants.PROP_ROLE).isNullOrBlank()) {
                when (mySharedPreferences.getData(Constants.PROP_ROLE).toString()
                    .replace("\"", "")) {
                    getString(R.string.tag_client) -> {
                        startActivity(Intent(this, ClientHomeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                    }
                    getString(R.string.tag_delivery) -> {
                        startActivity(Intent(this, DeliveryHomeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                    }
                    getString(R.string.tag_restaurant) -> {
                        startActivity(Intent(this, RestaurantHomeActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                    }
                }
            } else {
                startActivity(Intent(this, ClientHomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
        }
    }

    private fun saveUserSession(data: String) {
        val mySharedPreferences = MySharedPreferences(this)
        val user = Gson().fromJson(data, User::class.java)
        mySharedPreferences.saveData(Constants.PROP_USER, user)
        user.listOfRoles?.let { listOfRoles ->
            if (listOfRoles.size > 1) {
                startActivity(Intent(this, SelectRoleActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            } else {
                startActivity(Intent(this, ClientHomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
        }
    }
}