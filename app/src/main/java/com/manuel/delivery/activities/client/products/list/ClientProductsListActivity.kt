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
        intent.getStringExtra(Constants.PROP_ID_CATEGORY)?.let { ID_CATEGORY ->
            intent.getStringExtra(Constants.PROP_NAME_CATEGORY)?.let { NAME_CATEGORY ->
                setupToolbar(NAME_CATEGORY)
                user = Constants.getUserInSession(this)
                user?.let { u -> getProducts(u.sessionToken, ID_CATEGORY) }
            }
        }
    }

    private fun setupToolbar(title: String) {
        binding.toolbar.title = title
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorOnPrimary))
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getProducts(sessionToken: String?, idCategory: String) {
        productsProvider = ProductsProvider(sessionToken)
        productsProvider.findByCategory(idCategory)
            ?.enqueue(object : Callback<MutableList<Product>> {
                override fun onResponse(
                    call: Call<MutableList<Product>>,
                    response: Response<MutableList<Product>>
                ) {
                    response.body()?.let { listOfProducts ->
                        productsAdapter =
                            ProductsAdapter(this@ClientProductsListActivity, listOfProducts)
                        binding.rvProductsList.apply {
                            layoutManager = GridLayoutManager(this@ClientProductsListActivity, 2)
                            adapter = this@ClientProductsListActivity.productsAdapter
                            setHasFixedSize(true)
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<Product>>, t: Throwable) {
                    Snackbar.make(binding.root, t.message.toString(), Snackbar.LENGTH_SHORT).show()
                }
            })
    }
}