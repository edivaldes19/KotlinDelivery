package com.manuel.delivery.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.manuel.delivery.R
import com.manuel.delivery.activities.client.home.ClientHomeActivity
import com.manuel.delivery.databinding.ActivitySaveImageBinding
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.models.User
import com.manuel.delivery.providers.UsersProvider
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SaveImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaveImageBinding
    private lateinit var usersProvider: UsersProvider
    private var user: User? = null
    private var file: File? = null
    private var isPressed = false
    private val imageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val resultCode = activityResult.resultCode
            val data = activityResult.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data
                    file = File(fileUri?.path.toString())
                    binding.imgProfile.setImageURI(fileUri)
                }
                ImagePicker.RESULT_ERROR -> Snackbar.make(
                    binding.root,
                    ImagePicker.getError(data),
                    Snackbar.LENGTH_SHORT
                ).show()
                else -> {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.operation_cancelled),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = Constants.getUserInSession(this)
        binding = ActivitySaveImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        usersProvider = UsersProvider(user?.sessionToken)
        binding.imgProfile.setOnClickListener {
            ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080)
                .createIntent { intent -> imageResult.launch(intent) }
        }
        binding.btnConfirm.setOnClickListener {
            file?.let { f ->
                user?.let { u ->
                    usersProvider.update(f, u)?.enqueue(object : Callback<ResponseHttp> {
                        override fun onResponse(
                            call: Call<ResponseHttp>,
                            response: Response<ResponseHttp>
                        ) {
                            response.body()?.let { responseHttp ->
                                if (responseHttp.isSuccess) {
                                    saveUserSession(responseHttp.data.toString())
                                    goToClientHome()
                                    Toast.makeText(
                                        this@SaveImageActivity,
                                        responseHttp.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.failed_to_update_user_information),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            }
        }
        binding.btnSkip.setOnClickListener { goToClientHome() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isPressed) {
            finishAffinity()
        } else {
            isPressed = true
            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show()
        }
        val runnable = Runnable { isPressed = false }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 2000)
    }

    private fun saveUserSession(data: String) {
        val mySharedPreferences = MySharedPreferences(this)
        val user = Gson().fromJson(data, User::class.java)
        mySharedPreferences.saveData(Constants.PROP_USER, user)
    }

    private fun goToClientHome() {
        startActivity(Intent(this, ClientHomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}