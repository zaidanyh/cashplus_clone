package com.pasukanlangit.cashplus.ui.info_promo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.adapter.BannerAdapterList
import com.pasukanlangit.cashplus.databinding.ActivityInfoPromoListBinding
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel

class InfoPromoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoPromoListBinding
    private val infoPromoListViewModel : InfoPromoListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoPromoListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        with(binding){
            appBar.setOnBackPressed { finish() }

            infoPromoListViewModel.getBanner()
            rvBannerList.layoutManager = LinearLayoutManager(this@InfoPromoListActivity)
            rvBannerList.addItemDecoration(CashplusItemDecoration(24))

            infoPromoListViewModel.banner.observe(this@InfoPromoListActivity){
                when (it.status) {
                    Status.SUCCESS -> {
                        rvBannerList.isVisible = true
                        rvBannerListShimmer.isVisible = false
                        rvBannerListShimmer.stopShimmer()

                        it.data?.let { banner ->
                            rvBannerList.adapter = BannerAdapterList(banner){
                                startActivity(
                                    Intent(this@InfoPromoListActivity, DetailPromoActivity::class.java).apply {
                                        putExtra(DetailPromoActivity.KEY_BANNER, it)
                                    }
                                )
                            }
                        }
                    }
                    Status.LOADING -> {
                        rvBannerList.isVisible = false
                        rvBannerListShimmer.isVisible = true
                        rvBannerListShimmer.startShimmer()
                    }
                    Status.ERROR -> {
                        rvBannerList.isVisible = false
                        rvBannerListShimmer.isVisible = true
                        rvBannerListShimmer.startShimmer()
                    }
                }
            }
        }
    }
}