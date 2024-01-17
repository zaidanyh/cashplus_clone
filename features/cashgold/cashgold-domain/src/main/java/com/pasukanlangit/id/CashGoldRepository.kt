package com.pasukanlangit.id

import com.pasukanlangit.id.library_core.domain.model.KycStatus
import com.pasukanlangit.id.library_core.domain.model.UUIDRequest
import com.pasukanlangit.id.model.*

interface CashGoldRepository {
    suspend fun getIndoGoldBalance(request: UUIDRequest, accessToken: String): IndoGoldBalance
    suspend fun getSt24Profile(request: UUIDRequest, accessToken: String): St24Profile
    suspend fun getPriceGold(request: UUIDRequest, accessToken: String): GoldPrice
    fun getUUID(): String?
    fun getAccessToken(): String?
    suspend fun getChart(request: GetChartRequest, accessToken: String): List<ChartResponse>
    suspend fun checkUser(request: UUIDRequest, accessToken: String): Boolean
    suspend fun registerUser(request: RegisterRequest, accessToken: String): Boolean
    suspend fun checkKycStatus(request: UUIDRequest, accessToken: String): KycStatus
    suspend fun lockGoldByGram(request: LockGoldRequest, accessToken: String): LockGoldResponse
    suspend fun lockGoldByRupiah(request: LockGoldRequest, accessToken: String): LockGoldResponse
    suspend fun sendTrxGold(request: TrxRequest, accessToken: String): TrxGoldResponse
    suspend fun getWithDrawProduct(request: UUIDRequest, accessToken: String): WithDrawProductResponse
    suspend fun getAddressList(request: UUIDRequest, accessToken: String): List<Address>
    suspend fun getProvinces(request: UUIDRequest, accessToken: String): List<LocationList>
    suspend fun getCity(request: LocationRequest, accessToken: String): List<LocationList>
    suspend fun getDistrict(request: LocationRequest, accessToken: String): List<LocationList>
    suspend fun getVillage(request: LocationVillageRequest, accessToken: String): List<VillageList>
    suspend fun addAddress(request: AddAddress, accessToken: String): Boolean
    suspend fun updateAddress(request: UpdateAddress, accessToken: String): Boolean
    suspend fun deleteAddress(request: DeleteAddress, accessToken: String): Boolean
    suspend fun updateUserCashGold(request: UpdateUserCashGold, accessToken: String): Boolean
    suspend fun checkIsKtpUserNotEmpty(request: UUIDRequest, accessToken: String): Boolean
    suspend fun getUserCashGold(request: UUIDRequest, accessToken: String): UserCashGold
    suspend fun withDrawBook(request: WithDrawBookRequest, accessToken: String): WithDrawBookResponse
}