package com.pasukanlangit.id.domain_downline

import com.pasukanlangit.id.domain_downline.model.*

interface DownLineRepository {
    suspend fun getSummaryDownline(request: DownLineSummaryRequest, accessToken: String): SummaryDownlineResponse
    suspend fun getDownLineList(request: DownLineRequest, accessToken: String): DownLineResponse
    suspend fun successTrxCount(request: DownLineSummaryRequest, accessToken: String): DownlineTrxCount
    suspend fun getSubDownLineList(request: GetSubDownLineRequest, accessToken: String): DownLineResponse
    suspend fun setAllProductsMarkup(request: SetAllProductMarkupRequest, accessToken: String): Boolean
    suspend fun resetMarkup(request: ResetMarkupRequest, accessToken: String): Boolean
    suspend fun getProvinces(uuid: String): List<LocationNameResponse>
    suspend fun getCity(request: CityRequest): List<LocationNameResponse>
    suspend fun getDistrict(request: DistrictRequest): List<LocationNameResponse>
    suspend fun getVillage(request: VillageRequest): List<LocationNameResponse>
    suspend fun registerDownLine(request: RegisterDownLineRequest, accessToken: String): MessageResponse
    suspend fun topUpDownLine(request: TopUpDownLine, accessToken: String): MessageResponse
    suspend fun getMutationDownLine(transferRequest: DownLineSumDetRequest, accessToken: String): List<DownLineMutationResponse>
    suspend fun getMutationSubDownLine(transferRequest: DownLineSumDetRequest, accessToken: String): List<DownLineMutationResponse>
    suspend fun getDetailTransfer(transferRequest: DownLineSumDetRequest, accessToken: String): List<DetailTransferDownlineResponse>
    suspend fun getSummaryTransfer(transferRequest: DownLineSumDetRequest, accessToken: String): List<SummaryTransferResponse>
    suspend fun getAgentNearBy(uuid: String, accessToken: String): NearbyAgentResponse
    suspend fun generateQR(request: GenerateQRRequest, accessToken: String): GenerateQRResponse
    suspend fun scanQR(request: ScanQRAgenRequest, accessToken: String): MessageResponse
    suspend fun getProductsList(request: ProductRequest, accessToken: String): List<ProductResponse>

    suspend fun getListMarkupPlans(request: PricePlanRequest, accessToken: String): MarkupPlansResponse
    suspend fun getDetailMarkupPlan(request: DetailMarkupPlanRequest, accessToken: String): DetailMarkupPlansResponse
    suspend fun createReplaceMarkupPlan(request: CreateReplaceMarkupPlanRequest, accessToken: String): Boolean
    suspend fun applyMarkupPlan(request: DetailMarkupPlanRequest, accessToken: String): Boolean
    suspend fun unApplyMarkupPlan(request:DetailMarkupPlanRequest, accessToken: String): Boolean
    suspend fun deleteMarkupPlan(request: DetailMarkupPlanRequest, accessToken: String): Boolean

    suspend fun updateLatLong(request: UpdateLatLongRequest, accessToken: String): Boolean

    suspend fun checkBalance(uuid: String, accessToken: String): CheckBalanceResponse

    fun getUUID(): String?
    fun getAccessToken(): String?
}