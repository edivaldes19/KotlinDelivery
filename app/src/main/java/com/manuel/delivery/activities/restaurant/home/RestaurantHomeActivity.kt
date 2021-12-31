package com.manuel.delivery.activities.restaurant.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.manuel.delivery.R
import com.manuel.delivery.databinding.ActivityRestaurantHomeBinding
import com.manuel.delivery.fragments.client.ClientProfileFragment
import com.manuel.delivery.fragments.restaurant.RestaurantCategoriesFragment
import com.manuel.delivery.fragments.restaurant.RestaurantOrdersFragment
import com.manuel.delivery.fragments.restaurant.RestaurantProductFragment

class RestaurantHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestaurantHomeBinding
    private var isPressed = false
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
                    openFragment(RestaurantProductFragment())
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

    override fun onBackPressed() {
        super.onBackPressed()
        if (isPressed) {
            finishAffinity()
        } else {
            isPressed = true
            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show()
        }
        val runnable = Runnable { isPressed = false }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 2000)
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.clFragmentRestaurant, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}