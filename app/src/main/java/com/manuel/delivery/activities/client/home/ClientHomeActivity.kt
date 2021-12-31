package com.manuel.delivery.activities.client.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.manuel.delivery.R
import com.manuel.delivery.databinding.ActivityClientHomeBinding
import com.manuel.delivery.fragments.client.ClientCategoriesFragment
import com.manuel.delivery.fragments.client.ClientOrdersFragment
import com.manuel.delivery.fragments.client.ClientProfileFragment

class ClientHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientHomeBinding
    private var isPressed = false
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
        transaction.replace(R.id.clFragmentClient, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}