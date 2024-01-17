package com.pasukanlangit.id.datasource_downline

import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.datasource_downline.mapper.*
import com.pasukanlangit.id.datasource_downline.network.DownLineService
import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.*
import com.pasukanlangit.id.library_core.datasource.network.dto.UUIDDtoRequest
import com.pasukanlangit.id.library_core.datasource.utils.GlobalErrorParser
import com.pasukanlangit.id.library_core.domain.model.UUIDRequest
import com.pasukanlangit.id.library_core.utils.toUUIDDto

@Suppress("BlockingMethodInNonBlockingContext")
class DownLineRepositoryImpl(
    private val api: DownLineService,
    private val sharedPref: SharedPrefDataSource,
    private val errorParser: GlobalErrorParser
): DownLineRepository{
    override suspend fun getSummaryDownline(
        request: DownLineSummaryRequest,
        accessToken: String
    ): SummaryDownlineResponse {
        val response = api.getSummaryDownLine(request.toDownLineSummaryRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toSummaryDownlineResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getDownLineList(
        request: DownLineRequest,
        accessToken: String
    ): DownLineResponse {
        val response = api.getDownLineList(request.toDownLineRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toDownLineResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun successTrxCount(
        request: DownLineSummaryRequest,
        accessToken: String
    ): DownlineTrxCount {
        val response = api.downlineTrxCount(request.toDownLineSummaryRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toDownLineTrxCountResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getSubDownLineList(
        request: GetSubDownLineRequest,
        accessToken: String
    ): DownLineResponse {
        val response = api.getSubDownLineList(request.toSubDownLineRequestDto(), accessToken)
        if(response.isSuccessful) {
            response.body()?.let { data ->
                return data.toDownLineResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun setAllProductsMarkup(
        request: SetAllProductMarkupRequest,
        accessToken: String
    ): Boolean {
        val response = api.setAllProductMarkup(request.toSetAllProductMarkupDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.rc?.let { rc ->
                return rc == "00"
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun resetMarkup(request: ResetMarkupRequest, accessToken: String): Boolean {
        val response = api.resetMarkup(request.toResetMarkupDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.rc?.let { rc ->
                return rc == "00"
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getProvinces(uuid: String): List<LocationNameResponse> {
        val response = api.getProvinces(UUIDDtoRequest(uuid = uuid))
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.data?.toListProvince() ?: listOf()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getCity(request: CityRequest): List<LocationNameResponse> {
        val response = api.getCity(request.toCityRequestDto())
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.data?.toListCity() ?: listOf()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getDistrict(request: DistrictRequest): List<LocationNameResponse> {
        val response = api.getDistrict(request.toDistrictRequestDto())
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.data?.toDistrictCity() ?: listOf()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getVillage(request: VillageRequest): List<LocationNameResponse> {
        val response = api.getVillage(request.toVillageRequestDto())
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.data?.toVillages() ?: listOf()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun registerDownLine(
        request: RegisterDownLineRequest,
        accessToken: String
    ): MessageResponse {
        val response = api.registerDownLine(request.toRegisterDownLineRequestDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toMessageResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun topUpDownLine(
        request: TopUpDownLine,
        accessToken: String
    ): MessageResponse {
        val response = api.topUpDownLine(request.toTopUpDownLineRequestDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toMessageResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getMutationDownLine(
        transferRequest: DownLineSumDetRequest,
        accessToken: String
    ): List<DownLineMutationResponse> {
        val response = api.balanceMutation(transferRequest.toDownLineMutationRequestDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toDownLineMutationResponseDto()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getMutationSubDownLine(
        transferRequest: DownLineSumDetRequest,
        accessToken: String
    ): List<DownLineMutationResponse> {
        val response = api.subDownLineBalanceMutation(transferRequest.toDownLineMutationRequestDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toDownLineMutationResponseDto()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getDetailTransfer(
        transferRequest: DownLineSumDetRequest,
        accessToken: String
    ): List<DetailTransferDownlineResponse> {
        val response = api.getDetailTransferDownline(transferRequest.toDetailTransferRequestDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toDetailTransferDownline()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getSummaryTransfer(
        transferRequest: DownLineSumDetRequest,
        accessToken: String
    ): List<SummaryTransferResponse> {
        val response = api.getSummaryTransferDownline(transferRequest.toDetailTransferRequestDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toSummaryTransferDownline()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getAgentNearBy(uuid: String, accessToken: String): NearbyAgentResponse {
        val response = api.getNearbyAgent(UUIDDtoRequest(uuid = uuid), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toNearByAgenResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun generateQR(
        request: GenerateQRRequest,
        accessToken: String
    ): GenerateQRResponse {
        val response = api.generateQR(request.toGenerateQRRequestDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                return data.toGenerateQRResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun scanQR(request: ScanQRAgenRequest, accessToken: String): MessageResponse {
        val response = api.scanQR(request.toScanQRAgenRequestDto(), accessToken)
        if(response.isSuccessful){
            response.body()?.let { data ->
                if(data.rc == "00"){
                    return data.toMessageResponse()
                }else{
                    throw Exception(data.msg)
                }
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getProductsList(
        request: ProductRequest,
        accessToken: String
    ): List<ProductResponse> {
        val response = api.getProducts(request.toProductRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toProductResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getListMarkupPlans(
        request: PricePlanRequest,
        accessToken: String
    ): MarkupPlansResponse {
        val response = api.getListMarkupPlan(request.toPricePlanRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toMarkupPlansResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun getDetailMarkupPlan(
        request: DetailMarkupPlanRequest,
        accessToken: String
    ): DetailMarkupPlansResponse {
        val response = api.getDetailMarkupPlan(request.toDetailMarkupPlanRequestDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return data.toDetailMarkupPlanResponse()
            }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun createReplaceMarkupPlan(
        request: CreateReplaceMarkupPlanRequest,
        accessToken: String
    ): Boolean {
        val response = api.createMarkupPlan(request.toCreateReplaceMarkupPlanRequestDto(), accessToken)
        if (response.isSuccessful) return true
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun applyMarkupPlan(
        request: DetailMarkupPlanRequest,
        accessToken: String
    ): Boolean {
        val response = api.applyMarkupPlan(request.toDetailMarkupPlanRequestDto(), accessToken)
        if (response.isSuccessful) return true
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun unApplyMarkupPlan(
        request: DetailMarkupPlanRequest,
        accessToken: String
    ): Boolean {
        val response = api.unApplyMarkupPlan(request.toDetailMarkupPlanRequestDto(), accessToken)
        if (response.isSuccessful) return true
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun deleteMarkupPlan(
        request: DetailMarkupPlanRequest,
        accessToken: String
    ): Boolean {
        val response = api.deleteMarkupPlan(request.toDetailMarkupPlanRequestDto(), accessToken)
        if (response.isSuccessful) return true
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun updateLatLong(
        request: UpdateLatLongRequest,
        accessToken: String
    ): Boolean {
        val response = api.updateLatLong(request.toUpdateLatLongCurrentLocationRequestDto(), accessToken)
        if (response.isSuccessful) return true
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override suspend fun checkBalance(uuid: String, accessToken: String): CheckBalanceResponse {
        val request = UUIDRequest(uuid)
        val response = api.balanceCheck(request.toUUIDDto(), accessToken)
        if (response.isSuccessful) {
            response.body()?.let { data -> return data.toCheckBalanceResponse() }
        }
        val error = errorParser.parse(response.errorBody()?.string())
        throw Exception(error)
    }

    override fun getUUID(): String? = sharedPref.getUUID()

    override fun getAccessToken(): String? = sharedPref.getAccessToken()
}