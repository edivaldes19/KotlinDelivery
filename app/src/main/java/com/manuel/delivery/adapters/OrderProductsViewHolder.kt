package com.manuel.delivery.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.manuel.delivery.R
import com.manuel.delivery.databinding.ItemOrderProductsBinding
import com.manuel.delivery.models.Product

class OrderProductsAdapter(
    private var context: Context,
    private val listOfProducts: MutableList<Product>
) : RecyclerView.Adapter<OrderProductsAdapter.OrderProductsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductsViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_order_products, parent, false)
        return OrderProductsViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderProductsViewHolder, position: Int) {
        val product = listOfProducts[position]
        holder.binding.root.animation =
            AnimationUtils.loadAnimation(context, R.anim.fade_transition)
        holder.binding.tvName.text = product.name
        "${context.getString(R.string.amount)}: ${product.amount}".also { a ->
            holder.binding.tvAmount.text = a
        }
        Glide.with(context).load(product.image1).placeholder(R.drawable.ic_cloud_download)
            .error(R.drawable.ic_broken_image).into(holder.binding.imgProduct)
    }

    override fun getItemCount(): Int = listOfProducts.size
    inner class OrderProductsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemOrderProductsBinding.bind(view)
    }
}