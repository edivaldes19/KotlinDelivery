package com.manuel.delivery.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.manuel.delivery.R
import com.manuel.delivery.adapters.RolesAdapter
import com.manuel.delivery.databinding.ActivitySelectRoleBinding
import com.manuel.delivery.models.User
import com.manuel.delivery.utils.Constants
import com.manuel.delivery.utils.MySharedPreferences

class SelectRoleActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectRoleBinding
    private lateinit var rolesAdapter: RolesAdapter
    private var isPressed = false
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
                    adapter = this@SelectRoleActivity.rolesAdapter
                    setHasFixedSize(true)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isPressed) {
            finishAffinity()
        } else {
            isPressed = true
            Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show()
        }
        val runnable = Runnable { isPressed = false }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 2000)
    }
}