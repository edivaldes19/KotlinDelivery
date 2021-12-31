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
import com.manuel.delivery.activities.client.home.ClientHomeActivity
import com.manuel.delivery.activities.delivery.home.DeliveryHomeActivity
import com.manuel.delivery.activities.restaurant.home.RestaurantHomeActivity
import com.manuel.delivery.databinding.ItemRoleBinding
import com.manuel.delivery.models.Role
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences

class RolesAdapter(private var context: Context, private val listOfRoles: MutableList<Role>) :
    RecyclerView.Adapter<RolesAdapter.RolesViewHolder>() {
    private val mySharedPreferences = MySharedPreferences(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RolesViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_role, parent, false)
        return RolesViewHolder(view)
    }

    override fun onBindViewHolder(holder: RolesViewHolder, position: Int) {
        val role = listOfRoles[position]
        holder.binding.root.animation = AnimationUtils.loadAnimation(context, R.anim.slide)
        holder.binding.tvRole.text = role.roleName
        Glide.with(context).load(role.image).placeholder(R.drawable.ic_cloud_download)
            .error(R.drawable.ic_broken_image).into(holder.binding.imgRole)
        holder.binding.root.setOnClickListener {
            when (role.roleName) {
                Constants.PARAM_CLIENT -> {
                    mySharedPreferences.saveData(Constants.PROP_ROLE, Constants.PARAM_CLIENT)
                    context.startActivity(Intent(context, ClientHomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
                Constants.PARAM_DELIVERY -> {
                    mySharedPreferences.saveData(Constants.PROP_ROLE, Constants.PARAM_DELIVERY)
                    context.startActivity(Intent(context, DeliveryHomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
                Constants.PARAM_RESTAURANT -> {
                    mySharedPreferences.saveData(Constants.PROP_ROLE, Constants.PARAM_RESTAURANT)
                    context.startActivity(
                        Intent(
                            context,
                            RestaurantHomeActivity::class.java
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                }
            }
        }
    }

    override fun getItemCount(): Int = listOfRoles.size
    inner class RolesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemRoleBinding.bind(view)
    }
}