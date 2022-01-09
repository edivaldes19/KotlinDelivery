package com.manuel.delivery.activities.client.products.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.manuel.delivery.R
import com.manuel.delivery.databinding.ActivityClientProductsDetailBinding
import com.manuel.delivery.models.Product
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences

class ClientProductsDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientProductsDetailBinding
    private var listOfProducts = mutableListOf<Product>()
    private var product: Product? = null
    private var totalPrice = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientProductsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.getStringExtra(Constants.PROP_PRODUCT)?.let { p ->
            binding.toolbar.title = getString(R.string.product_detail)
            binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorOnPrimary))
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            product = Gson().fromJson(p, Product::class.java)
            product?.let { prod ->
                val mySharedPreferences = MySharedPreferences(this)
                if (!mySharedPreferences.getData(Constants.PROP_ORDER).isNullOrEmpty()) {
                    val type = object : TypeToken<MutableList<Product>>() {}.type
                    listOfProducts =
                        Gson().fromJson(mySharedPreferences.getData(Constants.PROP_ORDER), type)
                    val index = prod.id?.let { id -> getIndexOf(id) }
                    index?.let { i ->
                        if (i != -1) {
                            with(binding) {
                                prod.amount = listOfProducts[i].amount
                                tvAmount.text = "${prod.amount}"
                                totalPrice = prod.amount * prod.price
                                tvTotalPrice.text = HtmlCompat.fromHtml(
                                    getString(R.string.detail_total_price, totalPrice),
                                    HtmlCompat.FROM_HTML_MODE_LEGACY
                                )
                                eFabAddToTheList.text = getString(R.string.update_quantity)
                            }
                        }
                    }
                }
                val listOfImages = mutableListOf(
                    SlideModel(prod.image1, ScaleTypes.CENTER_CROP),
                    SlideModel(prod.image2, ScaleTypes.CENTER_CROP),
                    SlideModel(prod.image3, ScaleTypes.CENTER_CROP)
                )
                with(binding) {
                    isProduct.setImageList(listOfImages)
                    tvName.text = prod.name
                    tvDescription.text = HtmlCompat.fromHtml(
                        getString(R.string.description, prod.description),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    tvPrice.text = HtmlCompat.fromHtml(
                        getString(R.string.price, prod.price),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                }
                binding.fabSum.setOnClickListener { addProduct() }
                binding.fabSub.setOnClickListener { removeProduct() }
                binding.eFabAddToTheList.setOnClickListener {
                    val index = prod.id?.let { id -> getIndexOf(id) }
                    index?.let { i ->
                        if (i == -1) {
                            if (prod.amount == 0) {
                                prod.amount = 1
                            }
                            listOfProducts.add(prod)
                        } else {
                            listOfProducts[i].amount = prod.amount
                        }
                        mySharedPreferences.saveData(Constants.PROP_ORDER, listOfProducts)
                        Toast.makeText(
                            this@ClientProductsDetailActivity,
                            getString(R.string.product_added_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun addProduct() {
        product?.let { prod ->
            with(binding) {
                if (prod.amount < 5) {
                    fabSum.isEnabled = true
                    fabSub.isEnabled = true
                    prod.amount++
                    totalPrice = prod.price * prod.amount
                    tvAmount.text = "${prod.amount}"
                    tvTotalPrice.text = HtmlCompat.fromHtml(
                        getString(R.string.detail_total_price, totalPrice),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                }
                if (prod.amount == 5) {
                    fabSum.isEnabled = false
                    fabSub.isEnabled = true
                }
            }
        }
    }

    private fun removeProduct() {
        product?.let { prod ->
            with(binding) {
                if (prod.amount > 1) {
                    fabSub.isEnabled = true
                    fabSum.isEnabled = true
                    prod.amount--
                    totalPrice = prod.price * prod.amount
                    tvAmount.text = "${prod.amount}"
                    tvTotalPrice.text = HtmlCompat.fromHtml(
                        getString(R.string.detail_total_price, totalPrice),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                }
                if (prod.amount == 1) {
                    fabSub.isEnabled = false
                    fabSum.isEnabled = true
                }
            }
        }
    }

    private fun getIndexOf(productId: String): Int {
        var index = 0
        listOfProducts.forEach { p ->
            if (p.id == productId) {
                return index
            }
            index++
        }
        return -1
    }
}