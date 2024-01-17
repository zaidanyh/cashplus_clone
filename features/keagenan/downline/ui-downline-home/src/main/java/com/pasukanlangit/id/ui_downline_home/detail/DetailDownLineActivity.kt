package com.pasukanlangit.id.ui_downline_home.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.ui_downline_home.databinding.ActivityDetailDownLineBinding

class DetailDownLineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailDownLineBinding
    private var downline: DownLineDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailDownLineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        downline = intent.parcelable(DOWNLINE_ITEM_KEY)

        with(binding) {
            appBar.setOnBackPressed { finish() }
            tvIsActive.isInvisible = downline?.isActive == false
            tvIsNotActive.isVisible = downline?.isActive == false
            downline?.let { detail ->
                tvName.text = detail.accountName
                tvPhone.text = detail.phoneNumber
                tvAddress.text = detail.address
                tvSaldo.text = detail.balance
                tvOwnerName.text= detail.ownerName
                tvTrxCount.text = detail.trxCount
                tvMarkupCount.text = detail.markup
                tvDownlineCount.text = detail.downLineCount
            }
        }
    }

    companion object {
        const val DOWNLINE_ITEM_KEY = "downline_item_key"
    }
}