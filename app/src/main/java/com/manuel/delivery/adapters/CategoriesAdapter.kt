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
import com.manuel.delivery.activities.client.products.list.ClientProductsListActivity
import com.manuel.delivery.databinding.ItemCategoryBinding
import com.manuel.delivery.models.Category
import com.manuel.delivery.utils.Constants

class CategoriesAdapter(
    private var context: Context,
    private val listOfCategories: MutableList<Category>
) : RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        return CategoriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val category = listOfCategories[position]
        holder.binding.root.animation =
            AnimationUtils.loadAnimation(context, R.anim.fade_transition)
        holder.binding.tvCategory.text = category.name
        Glide.with(context).load(category.image).placeholder(R.drawable.ic_cloud_download)
            .error(R.drawable.ic_broken_image).into(holder.binding.imgCategory)
        holder.binding.root.setOnClickListener {
            context.startActivity(Intent(context, ClientProductsListActivity::class.java).apply {
                putExtra(Constants.PROP_ID_CATEGORY, category.id)
                putExtra(Constants.PROP_NAME_CATEGORY, category.name)
            })
        }
    }

    override fun getItemCount(): Int = listOfCategories.size
    inner class CategoriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemCategoryBinding.bind(view)
    }
}