package com.manuel.delivery.activities.client.address.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.manuel.delivery.R
import com.manuel.delivery.databinding.ActivityClientAddressMapBinding
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.Constants.PERMISSION_ID

class ClientAddressMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityClientAddressMapBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var city = ""
    private var country = ""
    private var address = ""
    private var addressLatLng: LatLng? = null
    private val locationCallback = object : LocationCallback() {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientAddressMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = getString(R.string.reference_point)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorOnPrimary))
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        binding.eFabConfirmAddressMap.setOnClickListener {
            val data = Intent().apply {
                putExtra(Constants.PROP_ADDRESS, address)
                putExtra(Constants.PROP_CITY, city)
                putExtra(Constants.PROP_COUNTRY, country)
                putExtra(Constants.PROP_LATITUDE, addressLatLng?.latitude)
                putExtra(Constants.PROP_LONGITUDE, addressLatLng?.longitude)
            }
            setResult(RESULT_OK, data)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        getLastLocation()
        onCameraMove()
    }

    private fun onCameraMove() {
        mMap.setOnCameraIdleListener {
            try {
                val geocoder = Geocoder(this)
                addressLatLng = mMap.cameraPosition.target
                addressLatLng?.let { latLng ->
                    val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    city = addressList[0].locality
                    country = addressList[0].countryName
                    address = addressList[0].getAddressLine(0)
                    "$address $city $country".also { a -> binding.etAddress.setText(a) }
                }
            } catch (e: Exception) {
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        mMap.moveCamera(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.builder()
                                    .target(LatLng(location.latitude, location.longitude)).zoom(15f)
                                    .build()
                            )
                        )
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
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, l)
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
            PERMISSION_ID
        )
    }
}