package com.pasukanlangit.id.network

import com.pasukanlangit.id.CashGoldRepository
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.library_core.domain.model.KycStatus
import com.pasukanlangit.id.library_core.domain.model.UUIDRequest
import com.pasukanlangit.id.library_core.utils.toKycStatusDomain
import com.pasukanlangit.id.library_core.utils.toUUIDDto
import com.pasukanlangit.id.model.*
import com.pasukanlangit.id.network.utils.CashGoldErrorParser

@Suppress("BlockingMethodInNonBlockingContext")
class CashGoldRepositoryImpl(
    private val api: CashGoldService,
    private val sharedPref: SharedPrefDataSource,
    private val errorParser: CashGoldErrorParser
): CashGoldRepository {
    override suspend fun getIndoGoldBalance(request: UUIDRequest, accessToken: String): IndoGoldBalance{
        val response = api.getIndoGoldBalance(request.toUUIDDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.data.data?.toUserBalance() ?: IndoGoldBalance()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
//        api.getUserBalance(request.toUUIDDto(), accessToken).data.data?.toUserBalance() ?: UserBalance()
    }

    override suspend fun getSt24Profile(request: UUIDRequest, accessToken: String): St24Profile {
        val response = api.getUserProfile(request.toUUIDDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toSt24Balance()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getPriceGold(request: UUIDRequest, accessToken: String): GoldPrice {
        val response = api.getPriceGold(request.toUUIDDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.data?.data?.toGoldPrice() ?: GoldPrice()
            }
        }

        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override fun getUUID(): String? = sharedPref.getUUID()

    override fun getAccessToken(): String? = sharedPref.getAccessToken()

    override suspend fun getChart(request: GetChartRequest, accessToken: String): List<ChartResponse> {
        val response = api.getChartGold(request.toGetChartDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toListChartDomain()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun checkUser(request: UUIDRequest, accessToken: String): Boolean {
        val response = api.checkUser(request.toUUIDDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.rc?.let { rc ->
                return rc == "00"
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun registerUser(request: RegisterRequest, accessToken: String): Boolean {
        val response = api.register(request.toRegisterDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.rc?.let { rc ->
                return rc == "00"
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun checkKycStatus(request: UUIDRequest, accessToken: String): KycStatus {
        val response = api.checkKycStatus(request.toUUIDDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toKycStatusDomain()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun lockGoldByGram(
        request: LockGoldRequest,
        accessToken: String
    ): LockGoldResponse {
        val response = api.lockGoldByGram(request.toLockGoldByGramRequestDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toLockGoldResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun lockGoldByRupiah(
        request: LockGoldRequest,
        accessToken: String
    ): LockGoldResponse {
        val response = api.lockGoldByRupiah(request.toLockGoldByRupiahRequestDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toLockGoldResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun sendTrxGold(request: TrxRequest, accessToken: String): TrxGoldResponse {
        val response = api.sendTrx(request.toTrxRequestDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toTrxGoldResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getWithDrawProduct(
        request: UUIDRequest,
        accessToken: String
    ): WithDrawProductResponse {
        val response = api.getWithDrawProduct(request.toUUIDDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toWithDrawProduct()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getAddressList(request: UUIDRequest, accessToken: String): List<Address> {
        val response = api.addressList(request.toUUIDDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.data.data.address.toAddressList()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getProvinces(
        request: UUIDRequest,
        accessToken: String
    ): List<LocationList> {
        val response = api.getProvinces(request.toUUIDDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.data.provinceData.toListProvince()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getCity(
        request: LocationRequest,
        accessToken: String
    ): List<LocationList> {
        val response = api.getCities(request.toRequestCity(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.data.provinceData.toListCity()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getDistrict(
        request: LocationRequest,
        accessToken: String
    ): List<LocationList> {
        val response = api.getDistricts(request.toRequestDistrict(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.data.provinceData.toListDistrict()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getVillage(
        request: LocationVillageRequest,
        accessToken: String
    ): List<VillageList> {
        val response = api.getVillage(request.toRequestVillage(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.data.provinceData.toListVillage()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun addAddress(request: AddAddress, accessToken: String): Boolean {
        val response = api.addAddress(request.toAddAddressDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { error ->
                if(error.rc == "00") return true
                else throw Exception(errorParser.mapErrorMessage(error))
            }
        }

        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun updateAddress(request: UpdateAddress, accessToken: String): Boolean {
        val response = api.updateAddress(request.toUpdateAddressDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { error ->
                if(error.rc == "00") return true
                else throw Exception(errorParser.mapErrorMessage(error))
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun deleteAddress(request: DeleteAddress, accessToken: String): Boolean {
        val response = api.deleteAddress(request.toDeleteAddressRequestDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { error ->
                if(error.rc == "00") return true
                else throw Exception(errorParser.mapErrorMessage(error))
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun updateUserCashGold(
        request: UpdateUserCashGold,
        accessToken: String
    ): Boolean {
        val response = api.updateUserCashGold(request.toUpdateUserDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { error ->
                if(error.rc == "00") return true
                else throw Exception(errorParser.mapErrorMessage(error))
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun checkIsKtpUserNotEmpty(
        request: UUIDRequest,
        accessToken: String
    ): Boolean {
        val response = api.checkUserCashGold(request.toUUIDDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { resp ->
                if(resp.rc == "00"){
                    return resp.isKtpNotEmpty()
                }
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getUserCashGold(request: UUIDRequest, accessToken: String): UserCashGold {
        val response = api.checkUserCashGold(request.toUUIDDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { resp ->
                if(resp.rc == "00"){
                    return resp.toUserCashGold()
                }
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun withDrawBook(
        request: WithDrawBookRequest,
        accessToken: String
    ): WithDrawBookResponse {
        val response = api.withDrawBook(request.toWithDrawBookRequest(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { resp ->
               if(resp.rc == "00"){
                   return resp.toWithDrawBookResponse()
               }
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

}