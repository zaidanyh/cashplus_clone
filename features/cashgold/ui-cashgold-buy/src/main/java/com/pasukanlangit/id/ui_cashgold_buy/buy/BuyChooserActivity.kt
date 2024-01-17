package com.pasukanlangit.id.ui_cashgold_buy.buy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pasukanlangit.id.core.utils.putExtra
import com.pasukanlangit.id.model.LockGoldType
import com.pasukanlangit.id.ui_cashgold_buy.databinding.ActivityBuyChooserBinding
import kotlinx.coroutines.FlowPreview

@FlowPreview
class BuyChooserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBuyChooserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyChooserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appbar.setIconBackPressed { finish() }

        binding.buyWithGram.setOnClickListener {
            startActivity(
                Intent(this, BuyCashGoldActivity::class.java)
                    .putExtra(LockGoldType.BY_GRAM)
            )
        }

        binding.buyWithRupiah.setOnClickListener {
            startActivity(
                Intent(this, BuyCashGoldActivity::class.java)
                    .putExtra(LockGoldType.BY_PRICE)
            )
        }

    }
}