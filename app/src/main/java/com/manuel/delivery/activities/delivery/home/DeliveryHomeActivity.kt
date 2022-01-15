package com.manuel.delivery.activities.delivery.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.manuel.delivery.R
import com.manuel.delivery.databinding.ActivityDeliveryHomeBinding
import com.manuel.delivery.fragments.client.ClientProfileFragment
import com.manuel.delivery.fragments.delivery.DeliveryOrdersFragment

class DeliveryHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeliveryHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openFragment(DeliveryOrdersFragment())
        binding.bnDeliveryHome.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_orders_delivery -> {
                    openFragment(DeliveryOrdersFragment())
                    true
                }
                R.id.item_profile_delivery -> {
                    openFragment(ClientProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.clFragmentDelivery, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}