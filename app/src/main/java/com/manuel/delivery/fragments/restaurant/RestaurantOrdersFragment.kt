package com.manuel.delivery.fragments.restaurant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.manuel.delivery.R
import com.manuel.delivery.adapters.RestaurantTabsPagerAdapter
import com.manuel.delivery.databinding.FragmentRestaurantOrdersBinding

class RestaurantOrdersFragment : Fragment() {
    private var binding: FragmentRestaurantOrdersBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantOrdersBinding.inflate(inflater, container, false)
        binding?.let { view ->
            val restaurantTabsPagerAdapter = RestaurantTabsPagerAdapter(
                requireContext(),
                requireActivity().supportFragmentManager,
                lifecycle,
                4
            )
            view.vpRestaurantOrders.adapter = restaurantTabsPagerAdapter
            view.vpRestaurantOrders.isUserInputEnabled = true
            TabLayoutMediator(view.tlRestaurantOrders, view.vpRestaurantOrders) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.tag_paid_out)
                    1 -> tab.text = getString(R.string.tag_prepared)
                    2 -> tab.text = getString(R.string.tag_on_the_way)
                    3 -> tab.text = getString(R.string.tag_delivered)
                }
            }.attach()
            return view.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}