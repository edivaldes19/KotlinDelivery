package com.manuel.delivery.activities.client.products.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.manuel.delivery.R
import com.manuel.delivery.adapters.ProductsAdapter
import com.manuel.delivery.databinding.ActivityClientProductsListBinding
import com.manuel.delivery.models.Product
import com.manuel.delivery.models.User
import com.manuel.delivery.providers.ProductsProvider
import com.manuel.delivery.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientProductsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientProductsListBinding
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var productsProvider: ProductsProvider
    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientProductsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = getString(R.string.add_category)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorOnPrimary))
        setSupportActionBar(binding.toolbar)
        intent.getStringExtra(Constants.PROP_ID_CATEGORY)?.let { s ->
            user = Constants.getUserInSession(this)
            user?.let { u ->
                productsProvider = ProductsProvider(u.sessionToken)
                productsProvider.findByCategory(s)?.enqueue(object :
                    Callback<MutableList<Product>> {
                    override fun onResponse(
                        call: Call<MutableList<Product>>,
                        response: Response<MutableList<Product>>
                    ) {
                        response.body()?.let { listOfProducts ->
                            productsAdapter =
                                ProductsAdapter(this@ClientProductsListActivity, listOfProducts)
                            binding.rvProductsList.apply {
                                layoutManager =
                                    GridLayoutManager(this@ClientProductsListActivity, 2)
                                adapter = this@ClientProductsListActivity.productsAdapter
                            }
                        }
                    }

                    override fun onFailure(call: Call<MutableList<Product>>, t: Throwable) {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.failed_to_get_all_products),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }
}