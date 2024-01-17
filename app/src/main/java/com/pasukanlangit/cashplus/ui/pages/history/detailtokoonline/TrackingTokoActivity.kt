package com.pasukanlangit.cashplus.ui.pages.history.detailtokoonline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.databinding.ActivityTrackingTokoBinding
import com.pasukanlangit.cashplus.model.response.ManifestItem
import com.pasukanlangit.id.core.utils.parcelableArrayList

class TrackingTokoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackingTokoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingTokoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val manifests = intent.parcelableArrayList<ManifestItem>(KEY_MANIFEST)
        setUpRv(manifests)
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setUpRv(manifests: ArrayList<ManifestItem>?) {
        binding.rvTracking.layoutManager = LinearLayoutManager(this)
        manifests?.let { this_manifests ->
            binding.rvTracking.adapter = TrackingAdapter(this_manifests)
        }
    }

    companion object {
        const val KEY_MANIFEST = "KEY_MANIFEST"
    }
}