package com.manuel.delivery.activities.delivery.orders.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.github.nkzawa.socketio.client.Socket
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.manuel.delivery.R
import com.manuel.delivery.activities.delivery.home.DeliveryHomeActivity
import com.manuel.delivery.databinding.ActivityDeliveryOrdersMapBinding
import com.manuel.delivery.models.Order
import com.manuel.delivery.models.ResponseHttp
import com.manuel.delivery.models.SocketEmit
import com.manuel.delivery.models.User
import com.manuel.delivery.providers.OrdersProvider
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences
import com.manuel.delivery.utils.SocketHandler
import com.maps.route.extensions.drawRouteOnMap
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveryOrdersMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityDeliveryOrdersMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var ordersProvider: OrdersProvider
    private var locationProviderClient: FusedLocationProviderClient? = null
    private var myLocationLatLng: LatLng? = null
    private var addressLatLng: LatLng? = null
    private var markerDelivery: Marker? = null
    private var markerAddress: Marker? = null
    private var order: Order? = null
    private var user: User? = null
    private var socket: Socket? = null
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val lastLocation = locationResult.lastLocation
            myLocationLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
            myLocationLatLng?.let { latLng ->
                emitPosition()
                if (binding.switchCenterPosition.isChecked) {
                    mMap.moveCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.builder()
                                .target(LatLng(latLng.latitude, latLng.longitude)).zoom(15f).build()
                        )
                    )
                }
                addressLatLng?.let { al ->
                    binding.eFabDeliverOrder.isEnabled = calculateDistance(latLng, al) <= 50
                }
                markerDelivery?.remove()
                addDeliveryMarker()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryOrdersMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.getStringExtra(Constants.PROP_ORDER)?.let { e ->
            setupToolbar()
            order = Gson().fromJson(e, Order::class.java)
            user = Constants.getUserInSession(this)
            order?.let { o ->
                user?.let { u ->
                    ordersProvider = OrdersProvider(u.sessionToken)
                    val mapFragment =
                        supportFragmentManager.findFragmentById(R.id.delivery_orders_map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                    locationProviderClient =
                        LocationServices.getFusedLocationProviderClient(this)
                    o.address?.let { a -> addressLatLng = LatLng(a.latitude, a.longitude) }
                    setInformationFromModel()
                    saveSelectionInSwitch()
                    binding.tvClient.setOnClickListener { validateCallPermission() }
                    binding.eFabDeliverOrder.setOnClickListener { updateOrder() }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationProviderClient?.removeLocationUpdates(locationCallback)
        socket?.disconnect()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        getLastLocation()
        connectSocket()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delivery_orders_map, menu)
        val mySharedPreferences = MySharedPreferences(this)
        mySharedPreferences.getSelection(Constants.PROP_RADIO_KEY)?.let { s ->
            if (s) {
                menu?.findItem(R.id.map_view)?.isChecked = true
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            } else {
                menu?.findItem(R.id.satellite_view)?.isChecked = true
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.map_view, R.id.satellite_view -> {
                item.isChecked = !item.isChecked
                val mySharedPreferences = MySharedPreferences(this)
                when (item.itemId) {
                    R.id.map_view -> {
                        mySharedPreferences.saveSelection(Constants.PROP_RADIO_KEY, true)
                        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                    }
                    R.id.satellite_view -> {
                        mySharedPreferences.saveSelection(Constants.PROP_RADIO_KEY, false)
                        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
        if (requestCode == Constants.REQUEST_PHONE_CALL) {
            callCustomer()
        }
    }

    private fun setupToolbar() {
        with(binding.toolbar) {
            title = getString(R.string.delivery_route)
            setTitleTextColor(
                ContextCompat.getColor(
                    this@DeliveryOrdersMapActivity,
                    R.color.colorOnPrimary
                )
            )
            inflateMenu(R.menu.menu_delivery_orders_map)
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun emitPosition() {
        order?.let { o ->
            o.id?.let { id ->
                myLocationLatLng?.let { latLng ->
                    val data = SocketEmit(
                        orderId = id,
                        latitude = latLng.latitude,
                        longitude = latLng.longitude
                    )
                    socket?.emit(Constants.PROP_POSITION, data.toJson())
                }
            }
        }
    }

    private fun connectSocket() {
        SocketHandler.setSocket()
        socket = SocketHandler.getSocket()
        socket?.connect()
    }

    private fun validateCallPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                Constants.REQUEST_PHONE_CALL
            )
        } else {
            callCustomer()
        }
    }

    private fun saveSelectionInSwitch() {
        with(binding.switchCenterPosition) {
            val mySharedPreferences =
                MySharedPreferences(this@DeliveryOrdersMapActivity)
            mySharedPreferences.getSelection(Constants.PROP_SWITCH_KEY)
                ?.let { s -> isChecked = s }
            setOnCheckedChangeListener { _, b ->
                if (b) {
                    mySharedPreferences.saveSelection(Constants.PROP_SWITCH_KEY, true)
                } else {
                    mySharedPreferences.saveSelection(Constants.PROP_SWITCH_KEY, false)
                }
            }
        }
    }

    private fun setInformationFromModel() {
        order?.let { o ->
            with(binding) {
                tvAddress.text = HtmlCompat.fromHtml(
                    getString(R.string.address, o.address?.address),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                tvSuburb.text = HtmlCompat.fromHtml(
                    getString(R.string.suburb, o.address?.suburb),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                tvClient.text = HtmlCompat.fromHtml(
                    getString(
                        R.string.client,
                        o.client?.name,
                        o.client?.lastname
                    ), HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                Glide.with(this@DeliveryOrdersMapActivity).load(o.client?.image)
                    .placeholder(R.drawable.ic_cloud_download).error(R.drawable.ic_broken_image)
                    .into(imgProfile)
            }
        }
    }

    private fun callCustomer() {
        if (ActivityCompat.checkSelfPermission(
                this@DeliveryOrdersMapActivity,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Snackbar.make(
                binding.root,
                getString(R.string.the_telephone_permission_is_disabled),
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }
        startActivity(Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:${order?.client?.phone}")
        })
    }

    private fun calculateDistance(fromLatLng: LatLng, toLatLng: LatLng): Float {
        val from = Location("")
        val to = Location("")
        from.latitude = fromLatLng.latitude
        from.longitude = fromLatLng.longitude
        to.latitude = toLatLng.latitude
        to.longitude = toLatLng.longitude
        return from.distanceTo(to)
    }

    private fun updateOrder() {
        order?.let { o ->
            ordersProvider.upgradeToDelivered(o)?.enqueue(object : Callback<ResponseHttp> {
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    response.body()?.let { responseHttp ->
                        if (responseHttp.isSuccess) {
                            startActivity(
                                Intent(
                                    this@DeliveryOrdersMapActivity,
                                    DeliveryHomeActivity::class.java
                                ).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                })
                            Toast.makeText(
                                this@DeliveryOrdersMapActivity,
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

    private fun addDeliveryMarker() {
        myLocationLatLng?.let { latLng ->
            markerDelivery = mMap.addMarker(
                MarkerOptions().position(latLng).title(getString(R.string.my_position)).snippet(
                    "${
                        getString(
                            R.string.latitude,
                            latLng.latitude
                        )
                    }, ${getString(R.string.longitude, latLng.longitude)}"
                ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
        }
    }

    private fun addAddressMarker() {
        order?.let { o ->
            o.address?.let { a ->
                val addressLocation = LatLng(a.latitude, a.longitude)
                markerAddress = mMap.addMarker(
                    MarkerOptions().position(addressLocation)
                        .title(getString(R.string.deliver_here)).snippet(
                            "${
                                getString(
                                    R.string.latitude,
                                    addressLocation.latitude
                                )
                            }, ${getString(R.string.longitude, addressLocation.longitude)}"
                        )
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                )
            }
        }
    }

    private fun drawRoute() {
        order?.let { o ->
            o.address?.let { a ->
                myLocationLatLng?.let { latLng ->
                    val addressLocation = LatLng(a.latitude, a.longitude)
                    mMap.drawRouteOnMap(
                        mapsApiKey = getString(R.string.google_maps_key),
                        context = this,
                        source = latLng,
                        destination = addressLocation,
                        color = Color.RED,
                        markers = false,
                        boundMarkers = false,
                        polygonWidth = 10
                    )
                }
            }
        }
    }

    private fun updateLatitudeAndLongitude(lat: Double, lng: Double) {
        order?.let { o ->
            o.apply {
                latitude = lat
                longitude = lng
            }
            ordersProvider.updateLatitudeAndLongitude(o)?.enqueue(object : Callback<ResponseHttp> {
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    response.body()?.let { responseHttp ->
                        if (!responseHttp.isSuccess) {
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

    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                requestNewLocationData()
                locationProviderClient?.lastLocation?.addOnCompleteListener { task ->
                    val location = task.result
                    location?.let { l ->
                        myLocationLatLng = LatLng(l.latitude, l.longitude)
                        updateLatitudeAndLongitude(l.latitude, l.longitude)
                        markerDelivery?.remove()
                        addDeliveryMarker()
                        addAddressMarker()
                        drawRoute()
                        if (!binding.switchCenterPosition.isChecked) {
                            mMap.moveCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.builder().target(LatLng(l.latitude, l.longitude))
                                        .zoom(15f).build()
                                )
                            )
                        }
                    }
                }
            } else {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                Toast.makeText(
                    this,
                    getString(R.string.localization_is_disabled),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            requestPermissions()
        }
    }

    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Looper.myLooper()?.let { l ->
            locationProviderClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                l
            )
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) or locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            Constants.PERMISSION_ID
        )
    }
}