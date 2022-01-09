package com.manuel.delivery.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.manuel.delivery.R
import com.manuel.delivery.activities.client.products.my_list.ClientProductsMyListActivity
import com.manuel.delivery.databinding.ItemMyProductsListBinding
import com.manuel.delivery.models.Product
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences

class MyListAdapter(
    private var context: Context,
    private val listOfProducts: MutableList<Product>
) : RecyclerView.Adapter<MyListAdapter.MyProductListViewHolder>() {
    init {
        (context as? ClientProductsMyListActivity)?.setTotal(getTotal())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductListViewHolder {
        context = parent.context
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_my_products_list, parent, false)
        return MyProductListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyProductListViewHolder, position: Int) {
        val product = listOfProducts[position]
        holder.binding.root.animation =
            AnimationUtils.loadAnimation(context, R.anim.fade_transition)
        holder.binding.tvName.text = product.name
        holder.binding.tvAmount.text = "${product.amount}"
        holder.binding.tvPrice.text = HtmlCompat.fromHtml(
            context.getString(R.string.price, product.amount * product.price),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        Glide.with(context).load(product.image1).placeholder(R.drawable.ic_cloud_download)
            .error(R.drawable.ic_broken_image).into(holder.binding.imgProduct)
        holder.binding.fabSum.setOnClickListener { addProduct(product, holder) }
        holder.binding.fabSub.setOnClickListener { removeProduct(product, holder) }
        holder.binding.imgDelete.setOnClickListener { deleteItem(position) }
    }

    override fun getItemCount(): Int = listOfProducts.size
    private fun addProduct(product: Product, holder: MyProductListViewHolder) {
        if (product.amount < 5) {
            holder.binding.fabSum.isEnabled = true
            holder.binding.fabSub.isEnabled = true
            val index = product.id?.let { id -> getIndexOf(id) }
            index?.let { i ->
                val mySharedPreferences = MySharedPreferences(context)
                product.amount++
                listOfProducts[i].amount = product.amount
                holder.binding.tvAmount.text = "${product.amount}"
                holder.binding.tvPrice.text = HtmlCompat.fromHtml(
                    context.getString(R.string.price, product.amount * product.price),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                mySharedPreferences.saveData(Constants.PROP_ORDER, listOfProducts)
                (context as? ClientProductsMyListActivity)?.setTotal(getTotal())
            }
        } else if (product.amount == 5) {
            holder.binding.fabSum.isEnabled = false
            holder.binding.fabSub.isEnabled = true
        }
    }

    private fun removeProduct(product: Product, holder: MyProductListViewHolder) {
        if (product.amount > 1) {
            holder.binding.fabSub.isEnabled = true
            holder.binding.fabSum.isEnabled = true
            val index = product.id?.let { id -> getIndexOf(id) }
            index?.let { i ->
                val mySharedPreferences = MySharedPreferences(context)
                product.amount--
                listOfProducts[i].amount = product.amount
                holder.binding.tvAmount.text = "${product.amount}"
                holder.binding.tvPrice.text = HtmlCompat.fromHtml(
                    context.getString(R.string.price, product.amount * product.price),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                mySharedPreferences.saveData(Constants.PROP_ORDER, listOfProducts)
                (context as? ClientProductsMyListActivity)?.setTotal(getTotal())
            }
        } else if (product.amount == 1) {
            holder.binding.fabSub.isEnabled = false
            holder.binding.fabSum.isEnabled = true
        }
    }

    private fun deleteItem(position: Int) {
        val mySharedPreferences = MySharedPreferences(context)
        listOfProducts.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, listOfProducts.size)
        mySharedPreferences.saveData(Constants.PROP_ORDER, listOfProducts)
        (context as? ClientProductsMyListActivity)?.setTotal(getTotal())
    }

    private fun getIndexOf(productId: String): Int {
        var index = 0
        listOfProducts.forEach { p ->
            if (p.id == productId) {
                return index
            }
            index++
        }
        return -1
    }

    private fun getTotal(): Double {
        var total = 0.0
        listOfProducts.forEach { prod -> total += (prod.amount * prod.price) }
        return total
    }

    inner class MyProductListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMyProductsListBinding.bind(view)
    }
}