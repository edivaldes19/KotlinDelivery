package com.manuel.delivery.fragments.restaurant

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.manuel.delivery.R
import com.manuel.delivery.databinding.FragmentRestaurantProductBinding
import com.manuel.delivery.models.Category
import com.manuel.delivery.models.Product
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.models.User
import com.manuel.delivery.providers.CategoriesProvider
import com.manuel.delivery.providers.ProductsProvider
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.TextWatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RestaurantProductFragment : Fragment() {
    private lateinit var categoriesProvider: CategoriesProvider
    private lateinit var productsProvider: ProductsProvider
    private var binding: FragmentRestaurantProductBinding? = null
    private var user: User? = null
    private var file1: File? = null
    private var file2: File? = null
    private var file3: File? = null
    private var categoryId = ""
    private val resultLauncher1 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val resultCode = activityResult.resultCode
            val data = activityResult.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data
                    file1 = File(fileUri?.path.toString())
                    binding?.imgProduct1?.setImageURI(fileUri)
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
    private val resultLauncher2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val resultCode = activityResult.resultCode
            val data = activityResult.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data
                    file2 = File(fileUri?.path.toString())
                    binding?.imgProduct2?.setImageURI(fileUri)
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
    private val resultLauncher3 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val resultCode = activityResult.resultCode
            val data = activityResult.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data
                    file3 = File(fileUri?.path.toString())
                    binding?.imgProduct3?.setImageURI(fileUri)
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
        binding = FragmentRestaurantProductBinding.inflate(inflater, container, false)
        binding?.let { view ->
            user = Constants.getUserInSession(requireContext())
            TextWatchers.validateFieldsAsYouType(
                requireContext(),
                view.btnAddProduct,
                view.etName,
                view.etDescription,
                view.etPrice
            )
            view.toolbar.title = getString(R.string.add_product)
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
                productsProvider = ProductsProvider(u.sessionToken)
                categoriesProvider.getAll()?.enqueue(object : Callback<MutableList<Category>> {
                    override fun onResponse(
                        call: Call<MutableList<Category>>,
                        response: Response<MutableList<Category>>
                    ) {
                        response.body()?.let { listOfCategories ->
                            val arrayAdapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                listOfCategories
                            )
                            b.atvCategory.setAdapter(arrayAdapter)
                            b.atvCategory.onItemClickListener =
                                AdapterView.OnItemClickListener { _, _, position, _ ->
                                    listOfCategories[position].id?.let { s ->
                                        categoryId = s
                                    }
                                }
                        }
                    }

                    override fun onFailure(call: Call<MutableList<Category>>, t: Throwable) {
                        Snackbar.make(
                            b.root,
                            getString(R.string.failed_to_get_all_categories),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                })
                b.imgProduct1.setOnClickListener {
                    ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080)
                        .createIntent { intent -> resultLauncher1.launch(intent) }
                }
                b.imgProduct2.setOnClickListener {
                    ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080)
                        .createIntent { intent -> resultLauncher2.launch(intent) }
                }
                b.imgProduct3.setOnClickListener {
                    ImagePicker.with(this).crop().compress(1024).maxResultSize(1080, 1080)
                        .createIntent { intent -> resultLauncher3.launch(intent) }
                }
                b.btnAddProduct.setOnClickListener {
                    b.btnAddProduct.isEnabled = false
                    b.pbRestaurantProduct.visibility = View.VISIBLE
                    val product = Product(
                        name = b.etName.text.toString().trim(),
                        description = b.etDescription.text.toString().trim(),
                        price = b.etPrice.text.toString().trim().toDouble(),
                        idCategory = categoryId
                    )
                    file1?.let { f1 ->
                        file2?.let { f2 ->
                            file3?.let { f3 ->
                                val files = mutableListOf(f1, f2, f3)
                                productsProvider.create(files, product)
                                    ?.enqueue(object : Callback<ResponseHttp> {
                                        override fun onResponse(
                                            call: Call<ResponseHttp>,
                                            response: Response<ResponseHttp>
                                        ) {
                                            response.body()?.let { responseHttp ->
                                                if (responseHttp.isSuccess) {
                                                    file1 = null
                                                    file2 = null
                                                    file3 = null
                                                    with(b) {
                                                        TextWatchers.clearAllTextFields(
                                                            etName,
                                                            etDescription,
                                                            etPrice
                                                        )
                                                        imgProduct1.setImageResource(R.drawable.ic_image_search)
                                                        imgProduct2.setImageResource(R.drawable.ic_image_search)
                                                        imgProduct3.setImageResource(R.drawable.ic_image_search)
                                                        pbRestaurantProduct.visibility = View.GONE
                                                    }
                                                    Toast.makeText(
                                                        requireContext(),
                                                        responseHttp.message,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<ResponseHttp>,
                                            t: Throwable
                                        ) {
                                            b.pbRestaurantProduct.visibility = View.GONE
                                            Snackbar.make(
                                                b.root,
                                                getString(R.string.error_adding_product),
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            }
                        }
                    }
                    if (file1 == null || file2 == null || file3 == null) {
                        b.pbRestaurantProduct.visibility = View.GONE
                        Snackbar.make(
                            b.root,
                            getString(R.string.you_must_capture_or_select_an_image),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    if (categoryId.isEmpty()) {
                        Snackbar.make(
                            b.root,
                            getString(R.string.you_must_select_a_category),
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