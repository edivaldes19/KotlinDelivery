package com.manuel.delivery.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.manuel.delivery.adapters.RolesAdapter
import com.manuel.delivery.databinding.ActivitySelectRoleBinding
import com.manuel.delivery.models.User
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences

class SelectRoleActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectRoleBinding
    private lateinit var rolesAdapter: RolesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mySharedPreferences = MySharedPreferences(this)
        if (!mySharedPreferences.getData(Constants.PROP_USER).isNullOrEmpty()) {
            val user =
                Gson().fromJson(mySharedPreferences.getData(Constants.PROP_USER), User::class.java)
            user.roles?.let { listOfRoles ->
                rolesAdapter = RolesAdapter(this, listOfRoles)
                binding.rvRole.apply {
                    layoutManager = LinearLayoutManager(this@SelectRoleActivity)
                    adapter = rolesAdapter
                    setHasFixedSize(true)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}