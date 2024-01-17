package com.pasukanlangit.cashplus.ui.splashscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityRootedBinding

class RootedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this).asGif().load(R.raw.cashplus_gagal).into(binding.ivError)
    }
}