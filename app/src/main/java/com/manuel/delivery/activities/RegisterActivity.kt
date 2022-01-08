package com.manuel.delivery.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.manuel.delivery.R
import com.manuel.delivery.databinding.ActivityRegisterBinding
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.models.User
import com.manuel.delivery.providers.UsersProvider
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences
import com.manuel.delivery.utils.TextWatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var usersProvider = UsersProvider()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        TextWatchers.validateFieldsAsYouType(
            this,
            binding.eFabRegister,
            binding.etName,
            binding.etSurnames,
            binding.etPhone,
            binding.etEmail,
            binding.etPassword,
            binding.etConfirmPassword
        )
        binding.tvReturnToLog.setOnClickListener { returnToLog() }
        binding.eFabRegister.setOnClickListener {
            if (TextWatchers.isEmailValid(binding.etEmail.text.toString().trim())) {
                val password = binding.etPassword.text.toString().trim()
                val confirmPassword = binding.etConfirmPassword.text.toString().trim()
                if (password == confirmPassword) {
                    val user = User(
                        email = binding.etEmail.text.toString().trim(),
                        name = binding.etName.text.toString().trim(),
                        lastname = binding.etSurnames.text.toString().trim(),
                        phone = binding.etPhone.text.toString().trim(),
                        password = password
                    )
                    usersProvider.register(user)?.enqueue(object : Callback<ResponseHttp> {
                        override fun onResponse(
                            call: Call<ResponseHttp>,
                            response: Response<ResponseHttp>
                        ) {
                            response.body()?.let { responseHttp ->
                                if (responseHttp.isSuccess) {
                                    saveUserInSession(responseHttp.data.toString())
                                    startActivity(
                                        Intent(
                                            this@RegisterActivity,
                                            SaveImageActivity::class.java
                                        ).apply {
                                            flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        })
                                    Toast.makeText(
                                        this@RegisterActivity,
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
                            Snackbar.make(
                                binding.root,
                                getString(R.string.failed_to_register_user),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    })
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.passwords_do_not_match),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.invalid_email),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        returnToLog()
    }

    private fun returnToLog() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    private fun saveUserInSession(data: String) {
        val mySharedPreferences = MySharedPreferences(this)
        val user = Gson().fromJson(data, User::class.java)
        mySharedPreferences.saveData(Constants.PROP_USER, user)
    }
}