package com.pasukanlangit.id.network

import com.pasukanlangit.id.library_core.datasource.network.dto.CheckKycResponseDto
import com.pasukanlangit.id.library_core.datasource.network.dto.UUIDDtoRequest
import com.pasukanlangit.id.network.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CashGoldService {
    @POST("trx/send")
    suspend fun sendTrx(@Body dtoRequest: TrxRequestDto, @Header("x_access_token") token: String): Response<TrxResponseDto>

    @POST("indogold/user/balance")
    suspend fun getIndoGoldBalance(@Body dtoRequest: UUIDDtoRequest, @Header("x_access_token") token: String): Response<GetBalanceDtoResponse>

    @POST("users/profile")
    suspend fun getUserProfile(@Body dtoRequest: UUIDDtoRequest, @Header("x_access_token") token: String): Response<ProfileResponseDto>

    @POST("indogold/purchase/price")
    suspend fun getPriceGold(@Body dtoRequest: UUIDDtoRequest, @Header("x_access_token") token: String): Response<GetPriceResponseDto>

    @POST("indogold/chart")
    suspend fun getChartGold(@Body dtoRequest: ChartRequestDto, @Header("x_access_token") token: String): Response<ChartResponseDto>

    @POST("indogold/user/check")
    suspend fun checkUser(@Body dtoRequest: UUIDDtoRequest, @Header("x_access_token") token: String): Response<CashGoldError>

    @POST("indogold/withdraw/products")
    suspend fun getWithDrawProduct(@Body dtoRequest: UUIDDtoRequest, @Header("x_access_token") token: String): Response<WithDrawProductResponseDto>

    @POST("indogold/user/register")
    suspend fun register(@Body dtoRequest: RegisterRequestDto, @Header("x_access_token") token: String): Response<CashGoldError>

    @POST("indogold/user/upload/check")
    suspend fun checkKycStatus(@Body dtoRequest: UUIDDtoRequest, @Header("x_access_token") token: String): Response<CheckKycResponseDto>

    @POST("indogold/purchase/lock/gram")
    suspend fun lockGoldByGram(@Body dtoRequest: LockByGramRequestDto, @Header("x_access_token") token: String): Response<LockGoldResponseDto>

    @POST("indogold/purchase/lock/rupiah")
    suspend fun lockGoldByRupiah(@Body dtoRequest: LockByRupiahRequestDto, @Header("x_access_token") token: String): Response<LockGoldResponseDto>

    @POST("indogold/user/address")
    suspend fun addressList(@Body dtoRequest: UUIDDtoRequest, @Header("x_access_token") token: String): Response<CashGoldAddressResponse>

    @POST("indogold/address/provinces")
    suspend fun getProvinces(@Body dtoRequest: UUIDDtoRequest, @Header("x_access_token") token: String): Response<ProvinceDtoResponse>

    @POST("indogold/address/cities")
    suspend fun getCities(@Body dtoRequest: GetCityDtoRequest, @Header("x_access_token") token: String): Response<CityDtoResponse>

    @POST("indogold/address/districts")
    suspend fun getDistricts(@Body dtoRequest: GetDistrictDtoRequest, @Header("x_access_token") token: String): Response<DistrictDtoResponse>

    @POST("indogold/address/villages")
    suspend fun getVillage(@Body dtoRequest: GetVillageDtoRequest, @Header("x_access_token") token: String): Response<VillageDtoResponse>

    @POST("indogold/user/address/add")
    suspend fun addAddress(@Body dtoRequest: AddAddressRequestDto, @Header("x_access_token") token: String): Response<CashGoldError>

    @POST("indogold/user/address/update")
    suspend fun updateAddress(@Body dtoRequest: UpdateAddressRequestDto, @Header("x_access_token") token: String): Response<CashGoldError>

    @POST("indogold/user/address/delete")
    suspend fun deleteAddress(@Body dtoRequest: DeleteAddressRequestDto, @Header("x_access_token") token: String): Response<CashGoldError>

    @POST("indogold/user/update")
    suspend fun updateUserCashGold(@Body dtoCashGoldRequest: UpdateUserCashGoldRequestDto, @Header("x_access_token") token: String): Response<CashGoldError>

    @POST("indogold/user/check")
    suspend fun checkUserCashGold(@Body dtoRequest: UUIDDtoRequest, @Header("x_access_token") token: String): Response<UserCashGoldCheckResponseDto>

    @POST("indogold/withdraw/book")
    suspend fun withDrawBook(@Body dtoRequest: WithDrawBookRequestDto, @Header("x_access_token") token: String): Response<WithDrawBookResponseDto>

}