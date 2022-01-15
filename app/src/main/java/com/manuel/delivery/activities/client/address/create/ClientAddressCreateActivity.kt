package com.manuel.delivery.activities.client.address.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.manuel.delivery.R
import com.manuel.delivery.activities.client.address.list.ClientAddressListActivity
import com.manuel.delivery.activities.client.address.map.ClientAddressMapActivity
import com.manuel.delivery.databinding.ActivityClientAddressCreateBinding
import com.manuel.delivery.models.Address
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.models.User
import com.manuel.delivery.providers.AddressProvider
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.TextWatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientAddressCreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientAddressCreateBinding
    private lateinit var addressProvider: AddressProvider
    private var user: User? = null
    private var addressLat = 0.0
    private var addressLng = 0.0
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val address = data?.getStringExtra(Constants.PROP_ADDRESS)
                val city = data?.getStringExtra(Constants.PROP_CITY)
                val country = data?.getStringExtra(Constants.PROP_COUNTRY)
                data?.getDoubleExtra(Constants.PROP_LATITUDE, 0.0)?.let { lat -> addressLat = lat }
                data?.getDoubleExtra(Constants.PROP_LONGITUDE, 0.0)?.let { lng -> addressLng = lng }
                "$address $city $country".also { rp -> binding.etReference.setText(rp) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientAddressCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        user = Constants.getUserInSession(this)
        user?.let { u ->
            addressProvider = AddressProvider(u.sessionToken)
            TextWatchers.validateFieldsAsYouType(
                this,
                binding.eFabContinueAddressCreate,
                binding.etAddress,
                binding.etSuburb
            )
            binding.etReference.setOnClickListener {
                resultLauncher.launch(Intent(this, ClientAddressMapActivity::class.java))
            }
            binding.eFabContinueAddressCreate.setOnClickListener { addAddress() }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.title = getString(R.string.new_address)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorOnPrimary))
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun addAddress() {
        user?.let { u ->
            if (!binding.etReference.text.isNullOrBlank()) {
                if (addressLat != 0.0 && addressLng != 0.0) {
                    val address = Address(
                        idUser = u.id,
                        address = binding.etAddress.text.toString().trim(),
                        suburb = binding.etSuburb.text.toString().trim(),
                        latitude = addressLat,
                        longitude = addressLng
                    )
                    addressProvider.create(address)?.enqueue(object : Callback<ResponseHttp> {
                        override fun onResponse(
                            call: Call<ResponseHttp>,
                            response: Response<ResponseHttp>
                        ) {
                            response.body()?.let { responseHttp ->
                                startActivity(
                                    Intent(
                                        this@ClientAddressCreateActivity,
                                        ClientAddressListActivity::class.java
                                    )
                                )
                                Toast.makeText(
                                    this@ClientAddressCreateActivity,
                                    responseHttp.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {}
                    })
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.error_getting_latitude_and_longitude),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.you_must_select_a_landmark),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }
}