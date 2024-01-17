package com.pasukanlangit.cashplus.di

import android.content.Context
import com.google.gson.Gson
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.id.core.utils.AppExecutors
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.library_core.datasource.utils.GlobalErrorParser
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private fun provideAppExecutors(): AppExecutors = AppExecutors()

private fun provideCheckNetwork(appExecutors: AppExecutors, context: Context): NetworkConnectivity =
    NetworkConnectivity(
        appExecutors = appExecutors,
        context = context
    )

private fun provideErrorParser(gson: Gson): GlobalErrorParser = GlobalErrorParser(gson)

val repositoryModule = module {
    single { MainRepository(get(), get(), get()) }
    single { provideAppExecutors() }
    single { provideCheckNetwork(get(), androidContext()) }
    single { provideErrorParser(get()) }
}