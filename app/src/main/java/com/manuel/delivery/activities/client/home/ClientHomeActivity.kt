package com.manuel.delivery.activities.client.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.manuel.delivery.R
import com.manuel.delivery.databinding.ActivityClientHomeBinding
import com.manuel.delivery.fragments.client.ClientCategoriesFragment
import com.manuel.delivery.fragments.client.ClientOrdersFragment
import com.manuel.delivery.fragments.client.ClientProfileFragment

class ClientHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openFragment(ClientCategoriesFragment())
        binding.bnClientHome.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_categories_client -> {
                    openFragment(ClientCategoriesFragment())
                    true
                }
                R.id.item_orders_client -> {
                    openFragment(ClientOrdersFragment())
                    true
                }
                R.id.item_profile_client -> {
                    openFragment(ClientProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.clFragmentClient, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}