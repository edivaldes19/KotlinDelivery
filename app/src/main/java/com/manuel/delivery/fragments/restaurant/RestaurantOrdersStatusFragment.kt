package com.manuel.delivery.fragments.restaurant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.manuel.delivery.R
import com.manuel.delivery.adapters.OrdersRestaurantAdapter
import com.manuel.delivery.databinding.FragmentRestaurantOrdersStatusBinding
import com.manuel.delivery.models.Order
import com.manuel.delivery.models.User
import com.manuel.delivery.providers.OrdersProvider
import com.manuel.delivery.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantOrdersStatusFragment : Fragment() {
    private lateinit var ordersProvider: OrdersProvider
    private lateinit var ordersRestaurantAdapter: OrdersRestaurantAdapter
    private var binding: FragmentRestaurantOrdersStatusBinding? = null
    private var user: User? = null
    private var status: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantOrdersStatusBinding.inflate(inflater, container, false)
        binding?.let { view ->
            user = Constants.getUserInSession(requireContext())
            status = arguments?.getString(Constants.PROP_STATUS)
            return view.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.let { b ->
            user?.let { u ->
                status?.let { s ->
                    ordersProvider = OrdersProvider(u.sessionToken)
                    ordersProvider.findByStatus(s)?.enqueue(object : Callback<MutableList<Order>> {
                        override fun onResponse(
                            call: Call<MutableList<Order>>,
                            response: Response<MutableList<Order>>
                        ) {
                            response.body()?.let { listOfOrders ->
                                ordersRestaurantAdapter =
                                    OrdersRestaurantAdapter(requireContext(), listOfOrders)
                                b.rvRestaurantOrders.apply {
                                    layoutManager = LinearLayoutManager(requireContext())
                                    adapter =
                                        this@RestaurantOrdersStatusFragment.ordersRestaurantAdapter
                                    setHasFixedSize(true)
                                }
                            }
                        }

                        override fun onFailure(call: Call<MutableList<Order>>, t: Throwable) {
                            Snackbar.make(
                                b.root,
                                getString(R.string.failed_to_get_all_orders),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}