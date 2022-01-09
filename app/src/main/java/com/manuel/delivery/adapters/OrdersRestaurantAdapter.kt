package com.manuel.delivery.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.manuel.delivery.R
import com.manuel.delivery.activities.restaurant.orders.detail.RestaurantOrdersDetailActivity
import com.manuel.delivery.databinding.ItemRestaurantOrderBinding
import com.manuel.delivery.models.Order
import com.manuel.delivery.utils.Constants

class OrdersRestaurantAdapter(
    private var context: Context,
    private val listOfOrder: MutableList<Order>
) : RecyclerView.Adapter<OrdersRestaurantAdapter.OrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        context = parent.context
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_restaurant_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = listOfOrder[position]
        holder.binding.root.animation =
            AnimationUtils.loadAnimation(context, R.anim.fade_transition)
        holder.binding.tvNumberOfOrder.text = HtmlCompat.fromHtml(
            context.getString(R.string.order, order.id),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        holder.binding.tvClient.text = HtmlCompat.fromHtml(
            context.getString(R.string.client, order.client?.name, order.client?.lastname),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        holder.binding.tvOrderDate.text = HtmlCompat.fromHtml(
            context.getString(R.string.order_date, order.timestamp),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        holder.binding.tvDeliver.text = HtmlCompat.fromHtml(
            context.getString(R.string.deliver, order.address?.address, order.address?.suburb),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        holder.binding.root.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    RestaurantOrdersDetailActivity::class.java
                ).apply {
                    putExtra(Constants.PROP_ORDER, order.toJson())
                })
        }
    }

    override fun getItemCount(): Int = listOfOrder.size
    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemRestaurantOrderBinding.bind(view)
    }
}