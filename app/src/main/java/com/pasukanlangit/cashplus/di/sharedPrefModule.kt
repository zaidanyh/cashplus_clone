package com.pasukanlangit.cashplus.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedPrefModule = module {
    single { provideMasterKey(androidContext()) }
    single { provideSharedPref(androidContext(),get()) }
    single { SharedPrefDataSource(get()) }
}


fun provideMasterKey(context: Context) : MasterKey {
    return MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
}

fun provideSharedPref(context: Context, masterKey: MasterKey) : SharedPreferences{
    return try {
        EncryptedSharedPreferences.create(
            context,
            "mysharedpref",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }catch(e: Exception){
        context.getSharedPreferences("mysharedpref", Context.MODE_PRIVATE)
    }
}