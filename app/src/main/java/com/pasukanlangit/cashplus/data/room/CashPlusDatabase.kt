package com.pasukanlangit.cashplus.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pasukanlangit.cashplus.model.response.AddressBookData
import com.pasukanlangit.cashplus.model.response.ProductStoreDataItem

@Database(entities = [ProductStoreDataItem::class, AddressBookData::class], version = 1)
abstract class CashPlusDatabase : RoomDatabase() {
    abstract fun cartProductDao() : CartProductDao
    abstract fun addressBookDao() : AddressBookDao
}