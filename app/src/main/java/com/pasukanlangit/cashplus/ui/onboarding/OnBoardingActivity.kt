package com.pasukanlangit.cashplus.ui.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityOnBoardingBinding
import com.pasukanlangit.cashplus.ui.login.LoginActivity
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import org.koin.android.ext.android.inject

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding
    private val sharedPrefDataSource: SharedPrefDataSource by inject()

    private lateinit var listBoarding : List<OnBoardingModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listBoarding = listOf(
            OnBoardingModel(
                1,
                R.drawable.splash_1,
                "Top Up Pulsa Cepat dan Aman",
                "Hanya di cashplus beli pulsa mudah dan cepat, buruan beli sekarang"
            ),
            OnBoardingModel(
                2,
                R.drawable.splash_2,
                "Top Up Game Cepat dan Aman",
                "Hanya di cashplus beli pulsa mudah dan cepat, buruan beli sekarang"
            ),
            OnBoardingModel(
                3,
                R.drawable.splash_3,
                "Bayar Tagihan Cepat dan Aman",
                "Hanya di cashplus beli pulsa mudah dan cepat, buruan beli sekarang"
            )
        )

        setUpViewPager(listBoarding)

        with(binding) {
            btnActionOnboarding.setOnClickListener {
                if (viewpagerOnboarding.currentItem < listBoarding.size - 1) {
                    viewpagerOnboarding.currentItem = viewpagerOnboarding.currentItem + 1
                    return@setOnClickListener
                }
                goToLoginActivity()
            }
        }
    }

    private fun goToLoginActivity() {
        finishAffinity()
        sharedPrefDataSource.setBoardingHasBeenShow(true)
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun setUpViewPager(pages: List<OnBoardingModel>) {
        with(binding){
            viewpagerOnboarding.adapter = OnBoardingAdapter(this@OnBoardingActivity,pages)
            viewpagerOnboarding.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                override fun onPageSelected(position: Int) {
                    if(::listBoarding.isInitialized) {
                        if(position == listBoarding.size-1) {
                            btnActionOnboarding.text = getString(R.string.ok)
                            return
                        }
                        btnActionOnboarding.text = getString(R.string.next)
                    }
                }
                override fun onPageScrollStateChanged(state: Int) {}
            })
            indicatorOnboarding.setViewPager(viewpagerOnboarding)
        }
    }
}