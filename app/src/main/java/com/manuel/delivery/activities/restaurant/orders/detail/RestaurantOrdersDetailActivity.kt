package com.manuel.delivery.activities.restaurant.orders.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.manuel.delivery.R
import com.manuel.delivery.activities.restaurant.home.RestaurantHomeActivity
import com.manuel.delivery.adapters.OrderProductsAdapter
import com.manuel.delivery.databinding.ActivityRestaurantOrdersDetailBinding
import com.manuel.delivery.models.Order
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.models.User
import com.manuel.delivery.providers.OrdersProvider
import com.manuel.delivery.providers.UsersProvider
import com.manuel.delivery.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantOrdersDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestaurantOrdersDetailBinding
    private lateinit var ordersProductsAdapter: OrderProductsAdapter
    private lateinit var ordersProvider: OrdersProvider
    private lateinit var usersProvider: UsersProvider
    private var order: Order? = null
    private var user: User? = null
    private var deliveryId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantOrdersDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.getStringExtra(Constants.PROP_ORDER)?.let { e ->
            order = Gson().fromJson(e, Order::class.java)
            user = Constants.getUserInSession(this)
            order?.let { o ->
                user?.let { u ->
                    with(binding) {
                        if (o.status == getString(R.string.tag_paid_out)) {
                            tilDeliveryMan.visibility = View.VISIBLE
                            eFabTrackOrder.visibility = View.VISIBLE
                        } else {
                            tvDeliveryMan.visibility = View.VISIBLE
                        }
                        toolbar.title = getString(R.string.order, o.id)
                        toolbar.setTitleTextColor(
                            ContextCompat.getColor(
                                this@RestaurantOrdersDetailActivity,
                                R.color.colorOnPrimary
                            )
                        )
                        setSupportActionBar(toolbar)
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        usersProvider = UsersProvider(u.sessionToken)
                        ordersProvider = OrdersProvider(u.sessionToken)
                        ordersProductsAdapter = OrderProductsAdapter(
                            this@RestaurantOrdersDetailActivity,
                            o.listOfProducts
                        )
                        rvOrdersDetail.apply {
                            layoutManager = LinearLayoutManager(this@RestaurantOrdersDetailActivity)
                            adapter = this@RestaurantOrdersDetailActivity.ordersProductsAdapter
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
                            getString(R.string.actual_status, o.status?.lowercase()),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                        tvDeliveryMan.text = HtmlCompat.fromHtml(
                            getString(
                                R.string.assigned_delivery_man,
                                o.delivery?.name,
                                o.delivery?.lastname
                            ), HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                        var totalPrice = 0.0
                        o.listOfProducts.forEach { p -> totalPrice += (p.amount * p.price) }
                        tvTotalPrice.text = HtmlCompat.fromHtml(
                            getString(R.string.detail_total_price, totalPrice),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                        usersProvider.findDeliveryMan()
                            ?.enqueue(object : Callback<MutableList<User>> {
                                override fun onResponse(
                                    call: Call<MutableList<User>>,
                                    response: Response<MutableList<User>>
                                ) {
                                    response.body()?.let { delivererList ->
                                        val arrayAdapter = ArrayAdapter(
                                            this@RestaurantOrdersDetailActivity,
                                            android.R.layout.simple_dropdown_item_1line,
                                            delivererList
                                        )
                                        atvDeliveryMan.setAdapter(arrayAdapter)
                                        atvDeliveryMan.onItemClickListener =
                                            AdapterView.OnItemClickListener { _, _, position, _ ->
                                                delivererList[position].id?.let { id ->
                                                    deliveryId = id
                                                }
                                            }
                                    }
                                }

                                override fun onFailure(
                                    call: Call<MutableList<User>>,
                                    t: Throwable
                                ) {
                                    Snackbar.make(
                                        root,
                                        getString(R.string.failed_to_get_all_categories),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        eFabTrackOrder.setOnClickListener {
                            o.apply { idDelivery = deliveryId }
                            ordersProvider.upgradeToReady(o)
                                ?.enqueue(object : Callback<ResponseHttp> {
                                    override fun onResponse(
                                        call: Call<ResponseHttp>,
                                        response: Response<ResponseHttp>
                                    ) {
                                        response.body()?.let { responseHttp ->
                                            if (responseHttp.isSuccess) {
                                                startActivity(
                                                    Intent(
                                                        this@RestaurantOrdersDetailActivity,
                                                        RestaurantHomeActivity::class.java
                                                    ).apply {
                                                        flags =
                                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                    })
                                                Toast.makeText(
                                                    this@RestaurantOrdersDetailActivity,
                                                    responseHttp.message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }

                                    override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                                        Snackbar.make(
                                            root,
                                            getString(R.string.failed_to_assign_dealer),
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        }
                    }
                }
            }
        }
    }
}