package com.manuel.delivery.activities.client.orders.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.manuel.delivery.R
import com.manuel.delivery.adapters.OrderProductsAdapter
import com.manuel.delivery.databinding.ActivityClientOrdersDetailBinding
import com.manuel.delivery.models.Order
import com.manuel.delivery.utils.Constants

class ClientOrdersDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientOrdersDetailBinding
    private lateinit var ordersProductsAdapter: OrderProductsAdapter
    private var order: Order? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientOrdersDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.getStringExtra(Constants.PROP_ORDER)?.let { e ->
            order = Gson().fromJson(e, Order::class.java)
            order?.let { o ->
                binding.toolbar.title = getString(R.string.order, o.id)
                binding.toolbar.setTitleTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorOnPrimary
                    )
                )
                setSupportActionBar(binding.toolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                ordersProductsAdapter = OrderProductsAdapter(this, o.listOfProducts)
                with(binding) {
                    rvOrdersDetail.apply {
                        layoutManager = LinearLayoutManager(this@ClientOrdersDetailActivity)
                        adapter = this@ClientOrdersDetailActivity.ordersProductsAdapter
                        setHasFixedSize(true)
                    }
                    tvTotalProducts.text = HtmlCompat.fromHtml(
                        getString(
                            R.string.total_products,
                            ordersProductsAdapter.itemCount
                        ), HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    tvOrderDate.text = HtmlCompat.fromHtml(
                        getString(R.string.order_date, o.timestamp),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    tvClient.text = HtmlCompat.fromHtml(
                        getString(
                            R.string.client,
                            o.client?.name,
                            o.client?.lastname
                        ), HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    tvDeliver.text = HtmlCompat.fromHtml(
                        getString(
                            R.string.deliver,
                            o.address?.address,
                            o.address?.suburb
                        ), HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    tvActualStatus.text = HtmlCompat.fromHtml(
                        getString(R.string.actual_status, o.status.lowercase()),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    var totalPrice = 0.0
                    o.listOfProducts.forEach { p -> totalPrice += (p.amount * p.price) }
                    tvTotalPrice.text = HtmlCompat.fromHtml(
                        getString(R.string.detail_total_price, totalPrice),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                }
            }
        }
    }
}