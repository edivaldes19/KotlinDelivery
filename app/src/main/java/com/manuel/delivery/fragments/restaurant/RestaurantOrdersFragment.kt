package com.manuel.delivery.fragments.restaurant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.manuel.delivery.databinding.FragmentRestaurantOrdersBinding

class RestaurantOrdersFragment : Fragment() {
    private var binding: FragmentRestaurantOrdersBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantOrdersBinding.inflate(inflater, container, false)
        binding?.let { view -> return view.root }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}