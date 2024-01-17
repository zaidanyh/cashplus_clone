package com.pasukanlangit.cashplus.data.room

import androidx.room.*
import com.pasukanlangit.cashplus.model.response.ProductStoreDataItem
import kotlinx.coroutines.flow.Flow


@Dao
interface CartProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductToCart(productStoreDataItem: ProductStoreDataItem)

    @Query("SELECT * FROM product_cart WHERE userId = :userId")
    fun getAllProductCart(userId: String) : Flow<List<ProductStoreDataItem>>

    @Query("SELECT * FROM product_cart WHERE isChecked = 1 AND userId = :userId")
    fun getAllProductChecked(userId: String) : Flow<List<ProductStoreDataItem>>

    @Query("UPDATE product_cart SET qty = :qty, isChecked = :isChecked, note = :note WHERE userId = :userId AND productId = :productId")
    fun updateCart(qty: Int, isChecked: Boolean, note: String, productId: String, userId: String)

    @Query("UPDATE product_cart SET isChecked = :value WHERE userId = :userId")
    fun updateCartsIsChecked(value: Boolean, userId: String)

    @Query("DELETE FROM product_cart WHERE userId = :userId AND productId = :productId")
    fun deleteProductFromCart(userId: String, productId: String)


    @Query("DELETE FROM product_cart WHERE userId = :userId AND isChecked")
    fun deleteCheckedProductFromCart(userId: String)
}