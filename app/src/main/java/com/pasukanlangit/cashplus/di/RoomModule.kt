package com.pasukanlangit.cashplus.di

import android.content.Context
import androidx.room.Room
import com.pasukanlangit.cashplus.data.room.AddressBookDao
import com.pasukanlangit.cashplus.data.room.CartProductDao
import com.pasukanlangit.cashplus.data.room.CashPlusDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val roomModule = module {
    single { provideDatabase(androidContext()) }
    single { provideCartProductDao(get()) }
    single { provideAddressBookDao(get()) }
}

fun provideDatabase(context: Context) : CashPlusDatabase =
    Room.databaseBuilder(context,CashPlusDatabase::class.java, "cashplus_database")
        .build()

fun provideCartProductDao(db: CashPlusDatabase): CartProductDao = db.cartProductDao()

fun provideAddressBookDao(db: CashPlusDatabase): AddressBookDao = db.addressBookDao()



