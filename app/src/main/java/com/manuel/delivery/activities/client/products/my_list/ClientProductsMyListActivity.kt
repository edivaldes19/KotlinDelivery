package com.manuel.delivery.activities.client.products.my_list

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.manuel.delivery.R
import com.manuel.delivery.activities.client.address.list.ClientAddressListActivity
import com.manuel.delivery.adapters.MyListAdapter
import com.manuel.delivery.databinding.ActivityClientProductsMyListBinding
import com.manuel.delivery.models.Product
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences

class ClientProductsMyListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientProductsMyListBinding
    private lateinit var myListAdapter: MyListAdapter
    private var listOfProducts = mutableListOf<Product>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientProductsMyListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = getString(R.string.my_product_list)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorOnPrimary))
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val mySharedPreferences = MySharedPreferences(this)
        if (!mySharedPreferences.getData(Constants.PROP_ORDER).isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<Product>>() {}.type
            listOfProducts =
                Gson().fromJson(mySharedPreferences.getData(Constants.PROP_ORDER), type)
            myListAdapter = MyListAdapter(this@ClientProductsMyListActivity, listOfProducts)
            binding.rvMyProductsList.apply {
                layoutManager = LinearLayoutManager(this@ClientProductsMyListActivity)
                adapter = this@ClientProductsMyListActivity.myListAdapter
                setHasFixedSize(true)
            }
        }
        binding.eFabContinueProductsMyList.setOnClickListener {
            startActivity(Intent(this, ClientAddressListActivity::class.java))
        }
    }

    fun setTotal(total: Double) {
        "$${getString(R.string.detail_total_price)}: $total MXN".also { t ->
            binding.tvTotalPrice.text = t
        }
    }
}