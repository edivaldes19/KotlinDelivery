package com.manuel.delivery.adapters

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.manuel.delivery.R
import com.manuel.delivery.fragments.restaurant.RestaurantOrdersStatusFragment
import com.manuel.delivery.utils.Constants

class RestaurantTabsPagerAdapter(
    private val context: Context,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private var numberOfTabs: Int
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = numberOfTabs
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> setArguments(context.getString(R.string.tag_paid_out))
            1 -> setArguments(context.getString(R.string.tag_prepared))
            2 -> setArguments(context.getString(R.string.tag_on_the_way))
            3 -> setArguments(context.getString(R.string.tag_delivered))
            else -> return RestaurantOrdersStatusFragment()
        }
    }

    private fun setArguments(value: String): RestaurantOrdersStatusFragment {
        val bundle = Bundle().apply { putString(Constants.PROP_STATUS, value) }
        return RestaurantOrdersStatusFragment().apply { arguments = bundle }
    }
}