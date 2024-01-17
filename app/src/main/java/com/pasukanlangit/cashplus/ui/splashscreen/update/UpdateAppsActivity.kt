package com.pasukanlangit.cashplus.ui.splashscreen.update

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.MainActivityNavComp
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityUpdateAppsBinding
import com.pasukanlangit.cashplus.ui.login.LoginActivity
import com.pasukanlangit.cashplus.ui.onboarding.OnBoardingActivity
import com.pasukanlangit.cashplus.utils.model.AppDataVersion
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.utils.parcelable
import org.koin.android.ext.android.inject

class UpdateAppsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateAppsBinding
    private val sharedPrefDataSource : SharedPrefDataSource by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateAppsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isBoardingHasBeenShow = sharedPrefDataSource.getBoardingHasBeenShow()
        val token = sharedPrefDataSource.getAccessToken()
        val uuid = sharedPrefDataSource.getUUID()
        val appNewsInfo = intent.parcelable<AppDataVersion>(KEY_VERSION_APP)

        with(binding){
            appNewsInfo?.let { info ->
                tvNewVersion.text = info.appVersion
                tvListUpdateVersion.text = getString(R.string.list_update_app_title, info.appVersion)
                layoutNewFeatures.isVisible = !info.newFeatures.isNullOrEmpty()
                if (!info.newFeatures.isNullOrEmpty()) {
                    with(rvNewFeatures) {
                        layoutManager = LinearLayoutManager(this@UpdateAppsActivity)
                        adapter = UpdateListAdapter(info.newFeatures)
                    }
                }

                layoutImprovement.isVisible = !info.improvements.isNullOrEmpty()
                if (!info.improvements.isNullOrEmpty()) {
                    with(rvImprovement) {
                        layoutManager = LinearLayoutManager(this@UpdateAppsActivity)
                        adapter = UpdateListAdapter(info.improvements)
                    }
                }

                layoutFixBug.isVisible = !info.bugFixings.isNullOrEmpty()
                if (!info.bugFixings.isNullOrEmpty()) {
                    with(rvFixBug) {
                        layoutManager = LinearLayoutManager(this@UpdateAppsActivity)
                        adapter = UpdateListAdapter(info.bugFixings)
                    }
                }
            }
            btnUpdate.setOnClickListener {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                    )
                }
            }

            btnSkip.setOnClickListener {
                finishAffinity()
                if (!isBoardingHasBeenShow) {
                    startActivity(Intent(this@UpdateAppsActivity, OnBoardingActivity::class.java))
                    return@setOnClickListener
                }

                if (uuid.isNullOrEmpty() && token.isNullOrEmpty()) {
                    startActivity(Intent(this@UpdateAppsActivity, LoginActivity::class.java))
                } else {
                    startActivity(Intent(this@UpdateAppsActivity, MainActivityNavComp::class.java))
                }
            }
        }

    }

    companion object {
        const val KEY_VERSION_APP = "KEY_VERSION_APP"
    }
}