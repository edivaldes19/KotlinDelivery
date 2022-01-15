package com.manuel.delivery.adapters

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.manuel.delivery.R
import com.manuel.delivery.fragments.delivery.DeliveryOrdersStatusFragment
import com.manuel.delivery.utils.Constants

class DeliveryTabsPagerAdapter(
    private val context: Context,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private var numberOfTabs: Int
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = numberOfTabs
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> setArguments(context.getString(R.string.tag_prepared))
            1 -> setArguments(context.getString(R.string.tag_on_the_way))
            2 -> setArguments(context.getString(R.string.tag_delivered))
            else -> return DeliveryOrdersStatusFragment()
        }
    }

    private fun setArguments(value: String): DeliveryOrdersStatusFragment {
        val bundle = Bundle().apply { putString(Constants.PROP_STATUS, value) }
        return DeliveryOrdersStatusFragment().apply { arguments = bundle }
    }
}