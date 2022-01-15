package com.manuel.delivery.activities.delivery.orders.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.manuel.delivery.R
import com.manuel.delivery.activities.delivery.orders.map.DeliveryOrdersMapActivity
import com.manuel.delivery.adapters.OrderProductsAdapter
import com.manuel.delivery.databinding.ActivityDeliveryOrdersDetailBinding
import com.manuel.delivery.models.Order
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.models.User
import com.manuel.delivery.providers.OrdersProvider
import com.manuel.delivery.providers.UsersProvider
import com.manuel.delivery.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveryOrdersDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeliveryOrdersDetailBinding
    private lateinit var ordersProductsAdapter: OrderProductsAdapter
    private lateinit var ordersProvider: OrdersProvider
    private lateinit var usersProvider: UsersProvider
    private var order: Order? = null
    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryOrdersDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.getStringExtra(Constants.PROP_ORDER)?.let { e ->
            order = Gson().fromJson(e, Order::class.java)
            user = Constants.getUserInSession(this)
            order?.let { o ->
                user?.let { u ->
                    with(binding) {
                        usersProvider = UsersProvider(u.sessionToken)
                        ordersProvider = OrdersProvider(u.sessionToken)
                        if (o.status == getString(R.string.tag_on_the_way)) {
                            eFabTrackOrder.text = getString(R.string.back_to_map)
                            eFabTrackOrder.setIconResource(R.drawable.ic_keyboard_arrow_left)
                        }
                        setupToolbar()
                        setupRecyclerView()
                        setInformationFromModel()
                        setTotal()
                        eFabTrackOrder.setOnClickListener {
                            when (eFabTrackOrder.text) {
                                getString(R.string.start_delivery) -> startDelivery()
                                getString(R.string.back_to_map) -> goToDeliveryOrdersMap()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        with(binding) {
            order?.let { o ->
                toolbar.title = getString(R.string.order, o.id)
                toolbar.setTitleTextColor(
                    ContextCompat.getColor(
                        this@DeliveryOrdersDetailActivity,
                        R.color.colorOnPrimary
                    )
                )
                setSupportActionBar(toolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    private fun setupRecyclerView() {
        order?.let { o ->
            ordersProductsAdapter =
                OrderProductsAdapter(this@DeliveryOrdersDetailActivity, o.listOfProducts)
            binding.rvOrdersDetail.apply {
                layoutManager = LinearLayoutManager(this@DeliveryOrdersDetailActivity)
                adapter = this@DeliveryOrdersDetailActivity.ordersProductsAdapter
                setHasFixedSize(true)
            }
        }
    }

    private fun setInformationFromModel() {
        with(binding) {
            order?.let { o ->
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
                    getString(R.string.actual_status, o.status?.lowercase()),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
        }
    }

    private fun setTotal() {
        order?.let { o ->
            var totalPrice = 0.0
            o.listOfProducts.forEach { p -> totalPrice += (p.amount * p.price) }
            binding.tvTotalPrice.text = HtmlCompat.fromHtml(
                getString(R.string.detail_total_price, totalPrice),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    }

    private fun startDelivery() {
        order?.let { o ->
            ordersProvider.upgradeToOnTheWay(o)?.enqueue(object : Callback<ResponseHttp> {
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    response.body()?.let { responseHttp ->
                        if (responseHttp.isSuccess) {
                            goToDeliveryOrdersMap()
                            Toast.makeText(
                                this@DeliveryOrdersDetailActivity,
                                responseHttp.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Snackbar.make(binding.root, responseHttp.message, Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Snackbar.make(binding.root, t.message.toString(), Snackbar.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun goToDeliveryOrdersMap() {
        order?.let { o ->
            startActivity(Intent(this, DeliveryOrdersMapActivity::class.java).apply {
                putExtra(Constants.PROP_ORDER, o.toJson())
            })
        }
    }
}