package com.pasukanlangit.cashplus.ui.info_promo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.pasukanlangit.cashplus.databinding.ActivityDetailPromoBinding
import com.pasukanlangit.cashplus.ui.ewallet.EWalletActivity
import com.pasukanlangit.cashplus.ui.pages.home.BannerDummy
import com.pasukanlangit.id.core.utils.parcelable

class DetailPromoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPromoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPromoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            btnNext.setOnClickListener {
                startActivity(Intent(this@DetailPromoActivity, EWalletActivity::class.java))
            }

            appBar.setOnBackPressed { finish() }

            intent.parcelable<BannerDummy>(KEY_BANNER)?.let { banner ->
                ivBanner.setImageResource(banner.image)
                tvBannerTitlDetail.text = banner.titleBanner
                tvDescription.text = banner.descriptionLong
                btnNext.isVisible = !banner.uri.isNullOrEmpty()

                banner.uri?.let { uri ->
                    btnNext.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri.replace("#", "%23")))
                        startActivity(intent)
                    }
                }
            }
        }
    }

    companion object {
        const val KEY_BANNER = "KEY_BANNER"
    }
}