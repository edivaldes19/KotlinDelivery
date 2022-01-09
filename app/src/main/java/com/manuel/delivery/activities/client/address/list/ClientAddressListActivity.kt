package com.manuel.delivery.activities.client.address.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.manuel.delivery.R
import com.manuel.delivery.activities.client.address.create.ClientAddressCreateActivity
import com.manuel.delivery.adapters.AddressAdapter
import com.manuel.delivery.databinding.ActivityClientAddressListBinding
import com.manuel.delivery.models.*
import com.manuel.delivery.providers.AddressProvider
import com.manuel.delivery.providers.OrdersProvider
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientAddressListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientAddressListBinding
    private lateinit var ordersProvider: OrdersProvider
    private lateinit var addressProvider: AddressProvider
    private lateinit var addressAdapter: AddressAdapter
    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = Constants.getUserInSession(this)
        user?.let { u ->
            binding.toolbar.title = getString(R.string.my_addresses)
            binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorOnPrimary))
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            ordersProvider = OrdersProvider(u.sessionToken)
            addressProvider = AddressProvider(u.sessionToken)
            addressProvider.findByUser(u.id)?.enqueue(object : Callback<MutableList<Address>> {
                override fun onResponse(
                    call: Call<MutableList<Address>>,
                    response: Response<MutableList<Address>>
                ) {
                    response.body()?.let { listOfAddress ->
                        addressAdapter =
                            AddressAdapter(this@ClientAddressListActivity, listOfAddress)
                        binding.rvAddress.apply {
                            layoutManager = LinearLayoutManager(this@ClientAddressListActivity)
                            adapter = this@ClientAddressListActivity.addressAdapter
                            setHasFixedSize(true)
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<Address>>, t: Throwable) {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.failed_to_get_all_address),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            })
            binding.eFabContinueAddressCreate.setOnClickListener {
                val mySharedPreferences = MySharedPreferences(this)
                if (!mySharedPreferences.getData(Constants.PROP_ADDRESS).isNullOrEmpty()) {
                    if (!mySharedPreferences.getData(Constants.PROP_ORDER).isNullOrEmpty()) {
                        val address = Gson().fromJson(
                            mySharedPreferences.getData(Constants.PROP_ADDRESS),
                            Address::class.java
                        )
                        val type = object : TypeToken<MutableList<Product>>() {}.type
                        val listOfProducts: MutableList<Product> =
                            Gson().fromJson(mySharedPreferences.getData(Constants.PROP_ORDER), type)
                        u.id?.let { idU ->
                            address.id?.let { idA ->
                                val order = Order(
                                    idClient = idU,
                                    idAddress = idA,
                                    listOfProducts = listOfProducts
                                )
                                ordersProvider.create(order)
                                    ?.enqueue(object : Callback<ResponseHttp> {
                                        override fun onResponse(
                                            call: Call<ResponseHttp>,
                                            response: Response<ResponseHttp>
                                        ) {
                                            response.body()?.let { responseHttp ->
                                                Toast.makeText(
                                                    this@ClientAddressListActivity,
                                                    responseHttp.message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<ResponseHttp>,
                                            t: Throwable
                                        ) {
                                            Snackbar.make(
                                                binding.root,
                                                getString(R.string.error_creating_order),
                                                Snackbar.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            }
                        }
                    }
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.you_must_select_an_address),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            binding.fabAddAddress.setOnClickListener {
                startActivity(Intent(this, ClientAddressCreateActivity::class.java))
            }
        }
    }

    fun resetValue(position: Int) {
        val viewHolder = binding.rvAddress.findViewHolderForAdapterPosition(position)
        val view = viewHolder?.itemView
        val imgCheck = view?.findViewById<ShapeableImageView>(R.id.imgCheck)
        imgCheck?.visibility = View.GONE
    }
}