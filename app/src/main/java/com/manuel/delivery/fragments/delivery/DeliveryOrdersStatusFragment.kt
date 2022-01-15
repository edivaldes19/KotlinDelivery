package com.manuel.delivery.fragments.delivery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.manuel.delivery.adapters.OrdersDeliveryAdapter
import com.manuel.delivery.databinding.FragmentDeliveryOrdersStatusBinding
import com.manuel.delivery.models.Order
import com.manuel.delivery.models.User
import com.manuel.delivery.providers.OrdersProvider
import com.manuel.delivery.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveryOrdersStatusFragment : Fragment() {
    private lateinit var ordersProvider: OrdersProvider
    private lateinit var ordersDeliveryAdapter: OrdersDeliveryAdapter
    private var binding: FragmentDeliveryOrdersStatusBinding? = null
    private var user: User? = null
    private var status: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeliveryOrdersStatusBinding.inflate(inflater, container, false)
        binding?.let { view ->
            user = Constants.getUserInSession(requireContext())
            status = arguments?.getString(Constants.PROP_STATUS)
            return view.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getOrders()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun getOrders() {
        binding?.let { b ->
            user?.let { u ->
                status?.let { s ->
                    u.id?.let { id ->
                        ordersProvider = OrdersProvider(u.sessionToken)
                        ordersProvider.findByIdDeliveryAndStatus(id, s)
                            ?.enqueue(object : Callback<MutableList<Order>> {
                                override fun onResponse(
                                    call: Call<MutableList<Order>>,
                                    response: Response<MutableList<Order>>
                                ) {
                                    response.body()?.let { listOfOrders ->
                                        ordersDeliveryAdapter =
                                            OrdersDeliveryAdapter(requireContext(), listOfOrders)
                                        b.rvDeliveryOrders.apply {
                                            layoutManager = LinearLayoutManager(requireContext())
                                            adapter =
                                                this@DeliveryOrdersStatusFragment.ordersDeliveryAdapter
                                            setHasFixedSize(true)
                                        }
                                    }
                                }

                                override fun onFailure(
                                    call: Call<MutableList<Order>>,
                                    t: Throwable
                                ) {
                                    Snackbar.make(
                                        b.root,
                                        t.message.toString(),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                }
            }
        }
    }
}