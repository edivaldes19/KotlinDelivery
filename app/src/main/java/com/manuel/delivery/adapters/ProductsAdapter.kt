package com.manuel.delivery.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.manuel.delivery.R
import com.manuel.delivery.activities.client.products.detail.ClientProductsDetailActivity
import com.manuel.delivery.databinding.ItemProductBinding
import com.manuel.delivery.models.Product
import com.manuel.delivery.utils.Constants

class ProductsAdapter(
    private var context: Context,
    private val listOfProducts: MutableList<Product>
) : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        return ProductsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = listOfProducts[position]
        holder.binding.root.animation =
            AnimationUtils.loadAnimation(context, R.anim.fade_transition)
        "$${product.price} MXN".also { price -> holder.binding.tvPrice.text = price }
        holder.binding.tvName.text = product.name
        Glide.with(context).load(product.image1).placeholder(R.drawable.ic_cloud_download)
            .error(R.drawable.ic_broken_image).into(holder.binding.imgProduct)
        holder.binding.root.setOnClickListener {
            context.startActivity(Intent(context, ClientProductsDetailActivity::class.java).apply {
                putExtra(Constants.PROP_PRODUCT, product.toJson())
            })
        }
    }

    override fun getItemCount(): Int = listOfProducts.size
    inner class ProductsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemProductBinding.bind(view)
    }
}