package com.pasukanlangit.cashplus.ui.pages.others

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityShareReferralBinding
import com.pasukanlangit.cashplus.utils.MyUtils.shareText

class ShareReferralActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShareReferralBinding
    private var btnBenefitClicked = false
    private var btnSnkClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareReferralBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            appBar.setOnBackPressed { finish() }
            val myReferral = intent.getStringExtra(KEY_MY_REFERRAL_CODE)
            tvMyReferralCode.text = intent.getStringExtra(KEY_MY_REFERRAL_CODE)
            btnShareReferral.setOnClickListener {
                shareText(
                    this@ShareReferralActivity,
                    "Ayoo daftar cashplus",
                    "Gunakan kode referral $myReferral untuk mendaftar menjadi downline saya di cashplus. More info: https://cashplus.id/"
                )
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                snkPoint1.text = Html.fromHtml(getString(R.string.snk1), Html.FROM_HTML_MODE_LEGACY)
                snkPoint2.text = Html.fromHtml(getString(R.string.snk2), Html.FROM_HTML_MODE_LEGACY)
                snkPoint3.text = Html.fromHtml(getString(R.string.snk3), Html.FROM_HTML_MODE_LEGACY)
                snkPoint4.text = Html.fromHtml(getString(R.string.snk4), Html.FROM_HTML_MODE_LEGACY)
                snkPoint5.text = Html.fromHtml(getString(R.string.snk5), Html.FROM_HTML_MODE_LEGACY)
            } else {
                snkPoint1.text = Html.fromHtml(getString(R.string.snk1))
                snkPoint2.text = Html.fromHtml(getString(R.string.snk2))
                snkPoint3.text = Html.fromHtml(getString(R.string.snk3))
                snkPoint4.text = Html.fromHtml(getString(R.string.snk4))
                snkPoint5.text = Html.fromHtml(getString(R.string.snk5))
            }

            imgExpand.setImageResource(R.drawable.icon_expand_black)
            ImageViewCompat.setImageTintList(imgExpand, ColorStateList.valueOf(Color.parseColor("#03053D")))
            imgExpandSnk.setImageResource(R.drawable.icon_expand_black)
            ImageViewCompat.setImageTintList(imgExpandSnk, ColorStateList.valueOf(Color.parseColor("#03053D")))

            btnBenefit.setOnClickListener {
                btnBenefitClicked = !btnBenefitClicked
                itemBenefit.isVisible = btnBenefitClicked
                if (btnBenefitClicked) {
                    imgExpand.setImageResource(R.drawable.icon_arrow_up_primary)
                    ImageViewCompat.setImageTintList(imgExpand, ColorStateList.valueOf(Color.parseColor("#03053D")))
                } else {
                    imgExpand.setImageResource(R.drawable.icon_expand_black)
                    ImageViewCompat.setImageTintList(imgExpand, ColorStateList.valueOf(Color.parseColor("#03053D")))
                }
            }
            btnSnk.setOnClickListener {
                btnSnkClicked = !btnSnkClicked
                itemSnk.isVisible = btnSnkClicked
                if (btnSnkClicked) {
                    imgExpandSnk.setImageResource(R.drawable.icon_arrow_up_primary)
                    ImageViewCompat.setImageTintList(imgExpandSnk, ColorStateList.valueOf(Color.parseColor("#03053D")))
                } else {
                    imgExpandSnk.setImageResource(R.drawable.icon_expand_black)
                    ImageViewCompat.setImageTintList(imgExpandSnk, ColorStateList.valueOf(Color.parseColor("#03053D")))
                }
            }
        }
    }

    companion object {
        const val KEY_MY_REFERRAL_CODE = "key_my_referral_code"
    }
}