package com.manuel.delivery.activities.client.payments.form

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.manuel.delivery.databinding.ActivityClientPaymentsFormBinding

class ClientPaymentsFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClientPaymentsFormBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientPaymentsFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}