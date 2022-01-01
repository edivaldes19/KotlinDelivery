package com.manuel.delivery.fragments.restaurant

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.manuel.delivery.R
import com.manuel.delivery.databinding.FragmentRestaurantCategoriesBinding
import com.manuel.delivery.models.Category
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.models.User
import com.manuel.delivery.providers.CategoriesProvider
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.TextWatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RestaurantCategoriesFragment : Fragment() {
    private lateinit var categoriesProvider: CategoriesProvider
    private var binding: FragmentRestaurantCategoriesBinding? = null
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
                    binding?.imgCategory?.setImageURI(fileUri)
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
        binding = FragmentRestaurantCategoriesBinding.inflate(inflater, container, false)
        binding?.let { view ->
            user = Constants.getUserInSession(requireContext())
            TextWatchers.validateFieldsAsYouType(requireContext(), view.btnAddCategory, view.etName)
            view.toolbar.title = getString(R.string.add_category)
            view.toolbar.setTitleTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorOnPrimary
                )
            )
            (activity as? AppCompatActivity)?.setSupportActionBar(view.toolbar)
            return view.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.let { b ->
            user?.let { u ->
                categoriesProvider = CategoriesProvider(u.sessionToken)
                b.imgCategory.setOnClickListener {
                    ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080)
                        .createIntent { intent -> resultLauncher.launch(intent) }
                }
                b.btnAddCategory.setOnClickListener {
                    b.btnAddCategory.isEnabled = false
                    val category = Category(name = b.etName.text.toString().trim())
                    file?.let { f ->
                        categoriesProvider.create(f, category)
                            ?.enqueue(object : Callback<ResponseHttp> {
                                override fun onResponse(
                                    call: Call<ResponseHttp>,
                                    response: Response<ResponseHttp>
                                ) {
                                    response.body()?.let { responseHttp ->
                                        if (responseHttp.isSuccess) {
                                            file = null
                                            with(b) {
                                                TextWatchers.clearAllTextFields(etName)
                                                b.imgCategory.setImageResource(R.drawable.ic_image_search)
                                                btnAddCategory.isEnabled = true
                                            }
                                            Toast.makeText(
                                                requireContext(),
                                                responseHttp.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                                    b.btnAddCategory.isEnabled = true
                                    Snackbar.make(
                                        b.root,
                                        getString(R.string.error_adding_category),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                    if (file == null) {
                        b.btnAddCategory.isEnabled = true
                        Snackbar.make(
                            b.root,
                            getString(R.string.you_must_capture_or_select_an_image),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}