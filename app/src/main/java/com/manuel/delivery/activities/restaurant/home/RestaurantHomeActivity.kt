package com.manuel.delivery.activities.restaurant.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.manuel.delivery.R
import com.manuel.delivery.activities.MainActivity
import com.manuel.delivery.databinding.ActivityRestaurantHomeBinding
import com.manuel.delivery.fragments.client.ClientProfileFragment
import com.manuel.delivery.fragments.restaurant.RestaurantCategoriesFragment
import com.manuel.delivery.fragments.restaurant.RestaurantFoodFragment
import com.manuel.delivery.fragments.restaurant.RestaurantOrdersFragment
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences

class RestaurantHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestaurantHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openFragment(RestaurantOrdersFragment())
        binding.bnRestaurantHome.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_orders_restaurant -> {
                    openFragment(RestaurantOrdersFragment())
                    true
                }
                R.id.item_categories_restaurant -> {
                    openFragment(RestaurantCategoriesFragment())
                    true
                }
                R.id.item_food_restaurant -> {
                    openFragment(RestaurantFoodFragment())
                    true
                }
                R.id.item_profile_restaurant -> {
                    openFragment(ClientProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.clFragmentRestaurant, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun logout() {
        val mySharedPreferences = MySharedPreferences(this)
        mySharedPreferences.removeData(Constants.PROP_USER)
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}