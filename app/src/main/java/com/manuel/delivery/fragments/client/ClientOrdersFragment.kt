package com.manuel.delivery.fragments.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.manuel.delivery.R
import com.manuel.delivery.adapters.ClientPagerAdapter
import com.manuel.delivery.databinding.FragmentClientOrdersBinding

class ClientOrdersFragment : Fragment() {
    private var binding: FragmentClientOrdersBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClientOrdersBinding.inflate(inflater, container, false)
        binding?.let { view ->
            val clientPagerAdapter = ClientPagerAdapter(
                requireContext(),
                requireActivity().supportFragmentManager,
                lifecycle,
                4
            )
            view.vpClientOrders.adapter = clientPagerAdapter
            view.vpClientOrders.isUserInputEnabled = true
            TabLayoutMediator(view.tlClientOrders, view.vpClientOrders) { tab, position ->
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