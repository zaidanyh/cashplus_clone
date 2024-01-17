package com.pasukanlangit.cashplus

import android.app.Application
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.pasukanlangit.cashplus.di.*
import com.pasukanlangit.cashplus.di.indigold.cashGoldModule
import com.pasukanlangit.cashplus.di.keagenan.downline.downLineModule
import com.pasukanlangit.cashplus.di.keagenan.rebate.rebateModule
import com.pasukanlangit.cashplus.di.kyc.kycModule
import com.pasukanlangit.cashplus.di.nobu.NobuModule
import com.pasukanlangit.cashplus.di.recapitulation.recapitulationModule
import com.pasukanlangit.cashplus.di.topup.topUpModule
import com.pasukanlangit.cashplus.di.transfer.transferModule
import com.pasukanlangit.cashplus.di.travel.travelModule
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@FlowPreview
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Create global configuration and initialize ImageLoader with this config
        val config = ImageLoaderConfiguration.Builder(this).build()
        ImageLoader.getInstance().init(config)

        startKoin {
            androidContext(applicationContext)
            modules(
                listOf(
                    networkModule, roomModule, repositoryModule, sharedPrefModule,
                    vmModule, topUpModule, cashGoldModule, kycModule, rebateModule,
                    downLineModule, NobuModule, travelModule, transferModule, recapitulationModule
                )
            )
        }
    }
}