package com.pasukanlangit.id.datasource_downline.network

import com.pasukanlangit.id.core.model.BalanceResponseCore
import com.pasukanlangit.id.datasource_downline.network.dto.*
import com.pasukanlangit.id.library_core.datasource.network.dto.UUIDDtoRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DownLineService {
    @POST("downline/summary")
    suspend fun getSummaryDownLine(@Body dtoRequest: DownLineSummaryRequestDto, @Header("x_access_token") token: String): Response<SummaryDownlineResponseDto>
    @POST("downline/list")
    suspend fun getDownLineList(@Body dtoRequest: DownLineRequestDto, @Header("x_access_token") token: String): Response<DownlineListResponseDto>
    @POST("downline/subdownline_list")
    suspend fun getSubDownLineList(@Body dtoRequest: GetSubDownLineRequestDto, @Header("x_access_token") token: String): Response<DownlineListResponseDto>
    @POST("downline/markup")
    suspend fun setAllProductMarkup(@Body dtoRequest: SetAllProductMarkupRequestDto, @Header("x_access_token") token: String): Response<MessageResponseDto>
    @POST("downline/markup_reset_all")
    suspend fun resetMarkup(@Body dtoRequest: ResetMarkupRequestDto, @Header("x_access_token") token: String): Response<MessageResponseDto>
    @POST("lookup/provinces")
    suspend fun getProvinces(@Body dtoRequest: UUIDDtoRequest): Response<ProvinceResponseDto>
    @POST("lookup/regencies")
    suspend fun getCity(@Body dtoRequest: CityRequestDto): Response<CityResponseDto>
    @POST("lookup/districts")
    suspend fun getDistrict(@Body dtoRequest: DistrictRequestDto): Response<DistrictResponseDto>
    @POST("lookup/villages")
    suspend fun getVillage(@Body dtoRequest: VillageRequestDto): Response<VillageResponseDto>
    @POST("downline/register")
    suspend fun registerDownLine(@Body dtoRequest: RegisterDownLineRequestDto, @Header("x_access_token") token: String): Response<MessageResponseDto>
    @POST("balance/transfer")
    suspend fun topUpDownLine(@Body dtoRequest: DownlineTopUpRequestDto, @Header("x_access_token") token: String): Response<MessageResponseDto>
    @POST("downline/balance_mutation")
    suspend fun balanceMutation(@Body dtoRequest: DownLineMutationRequestDto, @Header("x_access_token") token: String): Response<DownLineMutationResponseDto>
    @POST("downline/subdownline_balance_mutation")
    suspend fun subDownLineBalanceMutation(@Body dtoRequest: DownLineMutationRequestDto, @Header("x_access_token") token: String): Response<DownLineMutationResponseDto>
    @POST("downline/detail_transfer")
    suspend fun getDetailTransferDownline(@Body dtoRequest: DownlineTransferRequestDto, @Header("x_access_token") token: String): Response<DetailTransferResponseDto>
    @POST("downline/summary_transfer")
    suspend fun getSummaryTransferDownline(@Body dtoRequest: DownlineTransferRequestDto, @Header("x_access_token") token: String): Response<DownlineSummaryTransferResponseDto>
    @POST("users/nearby_agent")
    suspend fun getNearbyAgent(@Body dtoRequest: UUIDDtoRequest, @Header("x_access_token") token: String): Response<NearbyAgenResponseDto>
    @POST("balance/generate_qr")
    suspend fun generateQR(@Body dtoRequest: GenerateQrRequestDto, @Header("x_access_token") token: String): Response<GenerateQrResponseDto>
    @POST("balance/proc_qr")
    suspend fun scanQR(@Body dtoRequest: ScanQRAgenRequestDto, @Header("x_access_token") token: String): Response<MessageResponseDto>
    @POST("product/list")
    suspend fun getProducts(@Body dtoRequest: ProductRequestDto, @Header("x_access_token") token: String): Response<ProductsResponseDto>
    @POST("downline/success_trx_count")
    suspend fun downlineTrxCount(@Body dtoRequest: DownLineSummaryRequestDto, @Header("x_access_token") token: String): Response<DownlineTrxCountResponseDto>

    // Markup Plan
    @POST("downline/markup_plan/list")
    suspend fun getListMarkupPlan(@Body dtoRequest: PricePlanRequestDto, @Header("x_access_token") accessToken: String): Response<MarkupPlansResponseDto>
    @POST("downline/markup_plan/get_detail")
    suspend fun getDetailMarkupPlan(@Body dtoRequest: DetailMarkupPlanRequestDto, @Header("x_access_token") accessToken: String): Response<DetailMarkupPlanResponseDto>
    @POST("downline/markup_plan/create_replace")
    suspend fun createMarkupPlan(@Body dtoRequest: CreateReplaceMarkupPlanRequestDto, @Header("x_access_token") accessToken: String): Response<MarkupPlansResponseDto>
    @POST("downline/markup_plan/apply")
    suspend fun applyMarkupPlan(@Body dtoRequest: DetailMarkupPlanRequestDto, @Header("x_access_token") accessToken: String): Response<MarkupPlansResponseDto>
    @POST("downline/markup_plan/unapply")
    suspend fun unApplyMarkupPlan(@Body dtoRequest: DetailMarkupPlanRequestDto, @Header("x_access_token") accessToken: String): Response<MarkupPlansResponseDto>
    @POST("downline/markup_plan/delete")
    suspend fun deleteMarkupPlan(@Body dtoRequest: DetailMarkupPlanRequestDto, @Header("x_access_token") accessToken: String): Response<MarkupPlansResponseDto>

    @POST("users/lat_long_update")
    suspend fun updateLatLong(@Body dtoRequest: UpdateLatLongCurrentLocationRequestDto, @Header("x_access_token") accessToken: String): Response<MessageResponseDto>

    @POST("balance/check")
    suspend fun balanceCheck(@Body request: UUIDDtoRequest, @Header("x_access_token") accessToken: String): Response<BalanceResponseCore>
}