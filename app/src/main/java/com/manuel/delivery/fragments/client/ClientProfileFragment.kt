package com.manuel.delivery.fragments.client

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.manuel.delivery.R
import com.manuel.delivery.activities.MainActivity
import com.manuel.delivery.activities.SelectRoleActivity
import com.manuel.delivery.databinding.FragmentClientProfileBinding
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.models.User
import com.manuel.delivery.providers.UsersProvider
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences
import com.manuel.delivery.utils.TextWatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ClientProfileFragment : Fragment() {
    private lateinit var usersProvider: UsersProvider
    private var binding: FragmentClientProfileBinding? = null
    private var user: User? = null
    private var file: File? = null
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val resultCode = activityResult.resultCode
            val data = activityResult.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data
                    file = File(fileUri?.path.toString())
                    binding?.imgProfile?.setImageURI(fileUri)
                }
                ImagePicker.RESULT_ERROR -> binding?.let { view ->
                    Snackbar.make(
                        view.root,
                        ImagePicker.getError(data),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    binding?.let { view ->
                        Snackbar.make(
                            view.root,
                            getString(R.string.operation_cancelled),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClientProfileBinding.inflate(inflater, container, false)
        binding?.let { view ->
            getUserInSession()
            TextWatchers.validateFieldsAsYouType(
                requireContext(),
                view.btnEditProfile,
                view.etEmail,
                view.etName,
                view.etSurnames,
                view.etPhone
            )
            return view.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.let { b ->
            user?.let { u ->
                usersProvider = UsersProvider(u.sessionToken)
                Glide.with(requireContext()).load(u.image).placeholder(R.drawable.ic_cloud_download)
                    .error(R.drawable.ic_person).into(b.imgProfile)
                b.etEmail.setText(u.email.trim())
                b.etName.setText(u.name.trim())
                b.etSurnames.setText(u.lastname.trim())
                b.etPhone.setText(u.phone.trim())
                b.btnLogout.setOnClickListener {
                    val mySharedPreferences = MySharedPreferences(requireContext())
                    mySharedPreferences.removeData(Constants.PROP_USER)
                    startActivity(Intent(requireContext(), MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
                b.imgProfile.setOnClickListener {
                    ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080)
                        .createIntent { intent -> resultLauncher.launch(intent) }
                }
                b.btnChangeRole.setOnClickListener {
                    startActivity(
                        Intent(
                            requireContext(),
                            SelectRoleActivity::class.java
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                }
                b.btnEditProfile.setOnClickListener {
                    u.apply {
                        name = b.etName.text.toString().trim()
                        lastname = b.etSurnames.text.toString().trim()
                        phone = b.etPhone.text.toString().trim()
                    }
                    file?.let { f ->
                        usersProvider.update(f, u)
                            ?.enqueue(object : Callback<ResponseHttp> {
                                override fun onResponse(
                                    call: Call<ResponseHttp>,
                                    response: Response<ResponseHttp>
                                ) {
                                    response.body()?.let { responseHttp ->
                                        if (responseHttp.isSuccess) {
                                            saveUserSession(responseHttp.data.toString())
                                            Toast.makeText(
                                                requireContext(),
                                                responseHttp.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                                    Snackbar.make(
                                        b.root,
                                        getString(R.string.failed_to_update_user_information),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                    usersProvider.updateWithoutImage(u)?.enqueue(object : Callback<ResponseHttp> {
                        override fun onResponse(
                            call: Call<ResponseHttp>,
                            response: Response<ResponseHttp>
                        ) {
                            response.body()?.let { responseHttp ->
                                if (responseHttp.isSuccess) {
                                    saveUserSession(responseHttp.data.toString())
                                    Toast.makeText(
                                        requireContext(),
                                        responseHttp.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                            Snackbar.make(
                                b.root,
                                getString(R.string.failed_to_update_user_information),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun saveUserSession(data: String) {
        val mySharedPreferences = MySharedPreferences(requireContext())
        val user = Gson().fromJson(data, User::class.java)
        mySharedPreferences.saveData(Constants.PROP_USER, user)
    }

    private fun getUserInSession() {
        val mySharedPreferences = MySharedPreferences(requireContext())
        if (!mySharedPreferences.getData(Constants.PROP_USER).isNullOrEmpty()) {
            user =
                Gson().fromJson(mySharedPreferences.getData(Constants.PROP_USER), User::class.java)
        }
    }
}