package com.manuel.delivery.fragments.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.manuel.delivery.databinding.FragmentClientOrdersBinding

class ClientOrdersFragment : Fragment() {
    private var binding: FragmentClientOrdersBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClientOrdersBinding.inflate(inflater, container, false)
        binding?.let { view -> return view.root }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}