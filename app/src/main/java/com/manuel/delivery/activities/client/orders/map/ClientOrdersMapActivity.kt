package com.manuel.delivery.activities.client.orders.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
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
import com.manuel.delivery.databinding.ActivityClientOrdersMapBinding
import com.manuel.delivery.models.Order
import com.manuel.delivery.models.SocketEmit
import com.manuel.delivery.models.User
import com.manuel.delivery.providers.OrdersProvider
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences
import com.manuel.delivery.utils.SocketHandler
import com.maps.route.extensions.drawRouteOnMap

class ClientOrdersMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityClientOrdersMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var ordersProvider: OrdersProvider
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var myLocationLatLng: LatLng? = null
    private var deliveryLatLng: LatLng? = null
    private var markerDelivery: Marker? = null
    private var markerAddress: Marker? = null
    private var order: Order? = null
    private var user: User? = null
    private var socket: Socket? = null
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            myLocationLatLng =
                LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
            myLocationLatLng?.let { latLng ->
                if (binding.switchCenterPosition.isChecked) {
                    mMap.moveCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.builder()
                                .target(LatLng(latLng.latitude, latLng.longitude)).zoom(15f).build()
                        )
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientOrdersMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent.getStringExtra(Constants.PROP_ORDER)?.let { e ->
            order = Gson().fromJson(e, Order::class.java)
            user = Constants.getUserInSession(this)
            order?.let { o ->
                user?.let { u ->
                    setupToolbar()
                    ordersProvider = OrdersProvider(u.sessionToken)
                    val mapFragment =
                        supportFragmentManager.findFragmentById(R.id.delivery_orders_map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                    fusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(this)
                    deliveryLatLng = LatLng(o.latitude, o.longitude)
                    with(binding) {
                        tvAddress.text = HtmlCompat.fromHtml(
                            getString(R.string.address, o.address?.address),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                        tvSuburb.text = HtmlCompat.fromHtml(
                            getString(R.string.suburb, o.address?.suburb),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                        tvDeliveryMan.text = HtmlCompat.fromHtml(
                            getString(
                                R.string.delivery,
                                o.delivery?.name,
                                o.delivery?.lastname
                            ), HtmlCompat.FROM_HTML_MODE_LEGACY
                        )
                        Glide.with(this@ClientOrdersMapActivity).load(o.delivery?.image)
                            .placeholder(R.drawable.ic_cloud_download)
                            .error(R.drawable.ic_broken_image)
                            .into(imgProfile)
                        tvDeliveryMan.setOnClickListener {
                            if (ActivityCompat.checkSelfPermission(
                                    this@ClientOrdersMapActivity,
                                    Manifest.permission.CALL_PHONE
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                ActivityCompat.requestPermissions(
                                    this@ClientOrdersMapActivity,
                                    arrayOf(Manifest.permission.CALL_PHONE),
                                    Constants.REQUEST_PHONE_CALL
                                )
                            } else {
                                callCustomer()
                            }
                        }
                    }
                    with(binding.switchCenterPosition) {
                        val mySharedPreferences =
                            MySharedPreferences(this@ClientOrdersMapActivity)
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
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
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
                    this@ClientOrdersMapActivity,
                    R.color.colorOnPrimary
                )
            )
            inflateMenu(R.menu.menu_delivery_orders_map)
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun connectSocket() {
        order?.let { o ->
            SocketHandler.setSocket()
            socket = SocketHandler.getSocket()
            socket?.let { s ->
                s.connect()
                s.on("${Constants.PROP_POSITION}/${o.id}") { args ->
                    args[0]?.let { a ->
                        runOnUiThread {
                            val data = Gson().fromJson(a.toString(), SocketEmit::class.java)
                            markerDelivery?.remove()
                            addDeliveryMarker(data.latitude, data.longitude)
                        }
                    }
                }
            }
        }
    }

    private fun callCustomer() {
        order?.let { o ->
            if (ActivityCompat.checkSelfPermission(
                    this@ClientOrdersMapActivity,
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
            o.delivery?.let { d ->
                startActivity(Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:${d.phone}")
                })
            }
        }
    }

    private fun addDeliveryMarker(lat: Double, lng: Double) {
        val location = LatLng(lat, lng)
        markerDelivery = mMap.addMarker(
            MarkerOptions().position(location).title(getString(R.string.dealer_position)).snippet(
                "${getString(R.string.latitude, lat)}, ${
                    getString(R.string.longitude, lng)
                }"
            ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
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
                deliveryLatLng?.let { dl ->
                    val addressLocation = LatLng(a.latitude, a.longitude)
                    mMap.drawRouteOnMap(
                        mapsApiKey = getString(R.string.google_maps_key),
                        context = this,
                        source = dl,
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

    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient?.lastLocation?.addOnCompleteListener { task ->
                    val location = task.result
                    location?.let { l ->
                        myLocationLatLng = LatLng(l.latitude, l.longitude)
                        deliveryLatLng?.let { dl ->
                            markerDelivery?.remove()
                            addDeliveryMarker(dl.latitude, dl.longitude)
                            addAddressMarker()
                            drawRoute()
                            if (!binding.switchCenterPosition.isChecked) {
                                mMap.moveCamera(
                                    CameraUpdateFactory.newCameraPosition(
                                        CameraPosition.builder()
                                            .target(LatLng(dl.latitude, dl.longitude))
                                            .zoom(15f).build()
                                    )
                                )
                            }
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