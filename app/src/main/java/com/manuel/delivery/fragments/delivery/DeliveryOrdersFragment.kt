package com.manuel.delivery.fragments.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.manuel.delivery.R
import com.manuel.delivery.adapters.DeliveryTabsPagerAdapter
import com.manuel.delivery.databinding.FragmentDeliveryOrdersBinding

class DeliveryOrdersFragment : Fragment() {
    private var binding: FragmentDeliveryOrdersBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeliveryOrdersBinding.inflate(inflater, container, false)
        binding?.let { view ->
            val deliveryTabsPagerAdapter = DeliveryTabsPagerAdapter(
                requireContext(),
                requireActivity().supportFragmentManager,
                lifecycle,
                3
            )
            view.vpDeliveryOrders.adapter = deliveryTabsPagerAdapter
            view.vpDeliveryOrders.isUserInputEnabled = true
            TabLayoutMediator(view.tlDeliveryOrders, view.vpDeliveryOrders) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.tag_prepared)
                    1 -> tab.text = getString(R.string.tag_on_the_way)
                    2 -> tab.text = getString(R.string.tag_delivered)
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