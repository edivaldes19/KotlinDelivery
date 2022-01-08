package com.manuel.delivery.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.manuel.delivery.R
import com.manuel.delivery.activities.client.address.list.ClientAddressListActivity
import com.manuel.delivery.databinding.ItemAddressBinding
import com.manuel.delivery.models.Address
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences

class AddressAdapter(
    private var context: Context,
    private val listOfAddress: MutableList<Address>
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    private var previousSelection = 0
    private var positionInSession = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = listOfAddress[position]
        val mySharedPreferences = MySharedPreferences(context)
        holder.binding.root.animation =
            AnimationUtils.loadAnimation(context, R.anim.fade_transition)
        if (!mySharedPreferences.getData(Constants.PROP_ADDRESS).isNullOrEmpty()) {
            val address2 = Gson().fromJson(
                mySharedPreferences.getData(Constants.PROP_ADDRESS),
                Address::class.java
            )
            if (address2.id == address.id) {
                positionInSession = holder.adapterPosition
                holder.binding.imgCheck.visibility = View.VISIBLE
            }
        }
        holder.binding.tvAddress.text = address.address
        holder.binding.tvSuburb.text = address.suburb
        holder.binding.root.setOnClickListener {
            (context as? ClientAddressListActivity)?.resetValue(previousSelection)
            (context as? ClientAddressListActivity)?.resetValue(positionInSession)
            previousSelection = holder.adapterPosition
            holder.binding.imgCheck.visibility = View.VISIBLE
            saveAddress(address.toJson())
        }
    }

    override fun getItemCount(): Int = listOfAddress.size
    private fun saveAddress(data: String) {
        val mySharedPreferences = MySharedPreferences(context)
        val address = Gson().fromJson(data, Address::class.java)
        mySharedPreferences.saveData(Constants.PROP_ADDRESS, address)
    }

    inner class AddressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemAddressBinding.bind(view)
    }
}