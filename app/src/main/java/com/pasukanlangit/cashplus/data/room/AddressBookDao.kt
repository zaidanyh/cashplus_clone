package com.pasukanlangit.cashplus.data.room

import androidx.room.Dao
import androidx.room.Query
import com.pasukanlangit.cashplus.model.response.AddressBookData
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressBookDao {
    @Query("SELECT * FROM address_book WHERE userId = :userId")
    fun getAddressBookMain(userId: String) : Flow<AddressBookData?>

}