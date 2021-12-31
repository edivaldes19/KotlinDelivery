package com.manuel.delivery.fragments.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.manuel.delivery.databinding.FragmentDeliveryOrdersBinding

class DeliveryOrdersFragment : Fragment() {
    private var binding: FragmentDeliveryOrdersBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeliveryOrdersBinding.inflate(inflater, container, false)
        binding?.let { view -> return view.root }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}