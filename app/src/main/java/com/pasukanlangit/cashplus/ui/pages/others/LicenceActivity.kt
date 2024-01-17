package com.pasukanlangit.cashplus.ui.pages.others

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pasukanlangit.cashplus.databinding.ActivityLicenceBinding

class LicenceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLicenceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLicenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            appBar.setOnBackPressed { finish() }
        }
    }
}