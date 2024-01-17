package com.pasukanlangit.cashplus.data.api

import com.pasukanlangit.cashplus.model.request_body.*
import com.pasukanlangit.cashplus.model.response.*
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.core.model.BalanceResponseCore
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.nobu.datasource.network.dto.TopUpResponseDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Streaming
import retrofit2.http.GET

interface ApiService {
    @GET
    @Streaming
    suspend fun downloadFile(@Url url: String, @Query("data") data: String): Response<ResponseBody?>?

    @GET
    @Streaming
    suspend fun downloadFileExport(@Url url: String,@Query("ex") export: Boolean, @Query("data") data: String): Response<ResponseBody?>?

    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequestDto, @Header("signatureapps") signatureapps : String) : Response<UserModel>

    @POST("users/login_by_otp")
    suspend fun loginByOtp(@Body loginByOtp: LoginByOtpRequestDto): Response<OTPResponseDto>

    @POST("users/login_entry_otp")
    suspend fun loginOtpCode(@Body otpCodeRequest: ResetPwCodeRequest): Response<UserModel>

    @POST("users/update_firebase_token")
    suspend fun updateFirebaseToken(@Body request: UpdateFirebaseTokenRequest, @Header("x_access_token") accessToken : String) : Response<Void>

    @POST("users/logout")
    suspend fun logout(@Body request: ProfileRequest, @Header("x_access_token") token : String) : Response<UserModel>

    @POST("users/delete")
    suspend fun deleteAccount(@Body request: DeleteAccountRequestDto, @Header("x_access_token") token: String): Response<Void>

    @POST("users/upload_kyc")
    suspend fun uploadKYC(@Body request: KYCRequest, @Header("x_access_token") token : String) : Response<UserModel>

    @POST("users/profile_update")
    suspend fun updateProfile(@Body updateProfilRequest: UpdateProfilRequest, @Header("x_access_token") token : String) : Response<UserModel>

    @POST("users/email_update")
    suspend fun updateEmail(@Body updateEmailRequestDto: UpdateEmailRequestDto, @Header("x_access_token") token: String): Response<UserModel>

    @POST("users/lat_long_update")
    suspend fun updateLatLong(@Body latLongRequest: UpdateLatLongRequest, @Header("x_access_token") token : String) : Response<UserModel>

    @POST("users/change_referral")
    suspend fun changeReferral(@Body referralChangeRequest: ReferralChangeRequest, @Header("x_access_token") token : String) : Response<UserModel>

    @POST("users/register")
    suspend fun register(@Body user: RegisterRequestDto) : Response<OTPResponseDto>

    @POST("users/register_otp")
    suspend fun registerOtpCode(@Body resetPwCodeRequest: ResetPwCodeRequest) : Response<UserModel>

    @POST("users/change_pin")
    suspend fun changePin(@Body changePinRequest: ChangePinRequest, @Header("x_access_token") accessToken: String) : Response<UserModel>

    @POST("users/reset_pin")
    suspend fun changePinWithoutLastPin(@Body resetPINRequestDto: ResetPINRequestDto, @Header("x_access_token") accessToken: String) : Response<OTPResponseDto>

    @POST("users/reset_pin_otp")
    suspend fun sendOtpChangePIN(@Body changePinOtpCodeRequest: ResetPinOtpCodeRequest, @Header("x_access_token") accessToken: String) : Response<UserModel>

    @POST("users/profile")
    suspend fun getProfile(@Body request: ProfileRequest, @Header("x_access_token") accessToken: String) : Response<ProfileResponse>

    @POST("product/list")
    suspend fun getProductList(@Body productRequest: ProductRequest, @Header("x_access_token") accessToken: String) : Response<ProductResultModel>

    @POST("product/calc_unit_price")
    suspend fun calculateUnitPrice(@Body request: CalculateUnitPriceRequestDto, @Header("x_access_token") accessToken: String): Response<CalculateUnitPriceResponseDto>

    @POST("trx/send")
    suspend fun sendTransactionPrePaid(@Body transactionRequest: TransactionRequest, @Header("x_access_token") accessToken: String) : Response<TransactionResponse>

    @POST("trx/send")
    suspend fun sendTransactionPostPaid(@Body transactionRequest: TransactionRequest, @Header("x_access_token") accessToken: String) : Response<TransactionPayResponse>

    @POST("balance/topup")
    suspend fun topUpBank(@Body topUpBankRequest: TopUpBankRequest, @Header("x_access_token") accessToken: String) : Response<TopUpBankResponse>

    @POST("nicepay/va/registration")
    suspend fun registrationNicePay(@Body nicepayRegistrationRequest: NicepayRegistrationRequest, @Header("x_access_token") accessToken: String) : Response<NicepayRegistrationResponse>

    @POST("nicepay/cvs_ewallet/registration")
    suspend fun registrationNicePayEwallet(@Body ewalletRequest: NicePayEwalletRequest, @Header("x_access_token") accessToken: String) : Response<NicePayEwalletResponse>

    @POST("nicepay/qris/registration")
    suspend fun registrationQrisNicePay(@Body qrisRequest: QrisRegistrationRequest, @Header("x_access_token") accessToken: String): Response<QrisRegistrationResponse>

    @POST("nicepay/get_biaya")
    suspend fun getBillNicePay(@Body nicepayRegistrationRequest: NicepayRegistrationRequest, @Header("x_access_token") accessToken: String) : Response<BillNicePayResponse>

    @POST("trx/send")
    suspend fun sendTransactionTAG(@Body transactionRequest: TransactionRequest, @Header("x_access_token") accessToken: String) : Response<TransactionTAGResponse>

    @POST
    suspend fun getTransactionHistory(@Url baseUrlHistory : String, @Body transactionRequest: TrxHistoryRequest, @Header("x_access_token") accessToken: String) : Response<TransactionHistoryResponse>

    @POST
    suspend fun getTransactionHistoryTopUp(@Url baseUrlHistory : String, @Body transactionRequest: TrxHistoryRequest, @Header("x_access_token") accessToken: String) : Response<HistoryTopUpResponse>

    @POST
    suspend fun checkTransaction(@Url baseUrlHistory: String, @Body transactionCheckRequest: TransactionCheckRequest, @Header("x_access_token") accessToken: String) : Response<TransactionHistoryResponse>

    @POST("balance/check")
    suspend fun getBalance(@Body request: ProfileRequest, @Header("x_access_token") token : String) : Response<BalanceResponseCore>

    @POST("lookup/provinces")
    suspend fun getProvinces(@Body request: ProfileRequest) : Response<ProvinceResponse>

    @POST("lookup/regencies")
    suspend fun getDistricts(@Body districtRequest: DistrictRequest) : Response<DistrictsResponse>

    @POST("lookup/districts")
    suspend fun getSubDistricts(@Body subdistrictRequest: SubdistrictRequest) : Response<SubdistrictsResponse>

    @POST("lookup/villages")
    suspend fun getVillages(@Body villageRequest: VillageRequest) : Response<VillageResponse>

    @POST("users/change_pass")
    suspend fun changePassword(@Body request: ChangePasswordRequest,@Header("x_access_token") token: String) : Response<UserModel>

    @POST("onlinestore/public/product_news")
    suspend fun getProductStore(@Body productStoreRequest: ProductStoreRequest, @Header("x_access_token") accessToken: String) : Response<ProductStoreResponse>

    @POST("onlinestore/public/product_list")
    suspend fun getProductStoreList(@Body request: TokoOnlineListRequest, @Header("x_access_token") accessToken: String) : Response<ProductStoreResponse>

    @POST("onlinestore/public/product_search")
    suspend fun productStoreSearch(@Body request: ProductSearchRequest, @Header("x_access_token") accessToken: String) : Response<ProductStoreResponse>

    @POST("onlinestore/public/product_categories")
    suspend fun categoryProduct(@Body request: ProfileRequest) : Response<CategoryProductResponse>

    @POST("users/reset_pass")
    suspend fun resetPassword(@Body resetPassRequestDto: ResetPassRequestDto) : Response<OTPResponseDto>

    @POST("users/reset_pass_otp")
    suspend fun resetPasswordCode(@Body resetPwCodeRequest: ResetPwCodeRequest) : Response<UserModel>

    @POST("product/serverid_games_list")
    suspend fun getServerIdGames(@Body oprNameRequest: OprNameRequest, @Header("x_access_token") accessToken: String) : Response<ServerIdGamesResponse>

    @POST("product/update_selling_price")
    suspend fun updateSellingPrice(@Body request: UpdateSellingPriceRequestDto, @Header("x_access_token") accessToken: String): Response<Void>

    @POST("product/get_selling_price")
    suspend fun getSellingPrice(@Body request: SellingPriceRequest, @Header("x_access_token") accessToken: String): Response<SellingPriceResponse>

    @POST("onlinestore/addressbook/get_main")
    suspend fun getMainOrderBook(@Body request: ProfileRequest, @Header("x_access_token") accessToken: String) : Response<AddressMainBookResponse>

    @POST("onlinestore/addressbook/get_all")
    suspend fun getListAddressBook(@Body request: ProfileRequest, @Header("x_access_token") accessToken: String) : Response<AllAddressBookResponse>

    @POST("onlinestore/shipment/get_province")
    suspend fun getProvinceShipment(@Body request: ProfileRequest, @Header("x_access_token") accessToken: String) : Response<ProvinceShipmentResponse>

    @POST("onlinestore/shipment/get_city")
    suspend fun getCityShipment(@Body cityShipmentRequest: CityShipmentRequest, @Header("x_access_token") accessToken: String) : Response<CityShipmentResponse>

    @POST("onlinestore/shipment/get_subdistrict")
    suspend fun getSubDistrictShipment(@Body subdistrictShipmentRequest: SubdistrictShipmentRequest, @Header("x_access_token") accessToken: String) : Response<SubdistrictShipmentResponse>

    @POST("onlinestore/addressbook/create")
    suspend fun addAddressShipment(@Body addAddressBookRequest: AddAddressBookRequest, @Header("x_access_token") accessToken: String) : Response<UserModel>

    @POST("onlinestore/addressbook/update")
    suspend fun updateAddressShipment(@Body addAddressBookRequest: AddAddressBookRequest, @Header("x_access_token") accessToken: String) : Response<UserModel>

    @POST("onlinestore/addressbook/delete")
    suspend fun deleteAddressShipment(@Body addAddressBookRequest: AddAddressBookRequest, @Header("x_access_token") accessToken: String) : Response<UserModel>

    @POST("onlinestore/shipment/get_cost")
    suspend fun getPriceShipment(@Body priceRequest: CourierPriceRequest, @Header("x_access_token") accessToken: String) : Response<CourierPriceResponse>

    @POST("onlinestore/trx/order")
    suspend fun orderOnlineStore(@Body orderRequest: OnlineStoreOrderRequest, @Header("x_access_token") accessToken: String) : Response<OnlineStoreOrderResponse>

    @POST("onlinestore/trx/order_check")
    suspend fun orderCheckTokoOnline(@Body orderCheckTokoRequest: OrderCheckTokoRequest, @Header("x_access_token") accessToken: String) : Response<OrderCheckTokoResponse>

    @POST("onlinestore/trx/order_received")
    suspend fun setTrxTOReceived(@Body orderCheckTokoRequest: OrderCheckTokoRequest, @Header("x_access_token") accessToken: String) : Response<ErrorMessageResponse>

    @POST("mmbc/hotel/list")
    suspend fun getHotelOrderList(@Body hotelListByCityRequest: HotelListByCityRequest, @Header("x_access_token") accessToken: String) : Response<HotelListResponse>

    @POST("onlinestore/trx/order_track")
    suspend fun orderTrackingTokoOnline(@Body request: OrderCheckTokoRequest, @Header("x_access_token") accessToken: String) : Response<TrackingOrderResponse>

    @POST("trx/print_receipt")
    suspend fun printReciept(@Body request: OrderCheckTokoRequest, @Header("x_access_token") accessToken: String) : Response<PrintTrxResponse>

    @POST("trx/print_receipt_edit")
    suspend fun newPrintReceipt(@Body request: PrintReceiptRequestDto, @Header("x_access_token") accessToken: String): Response<PrintReceiptResponseDto>

    @POST("param/get")
    suspend fun getCurrentVersionApps(@Body request: ProfileRequest) : Response<AppsVersionResponse>

    @POST("nobu/cobrand/is_binded")
    suspend fun getIsBinded(@Body request: ProfileRequest, @Header("x_access_token") accessToken: String): Response<BindedResponse>

    @POST("nobu/cobrand/balance_inquiry")
    suspend fun getBalanceInquiry(@Body request: ProfileRequest, @Header("x_access_token") accessToken: String): Response<BalanceInquiryResponse>

    @POST("nobu/cobrand/topup_emoney")
    suspend fun topUpQris(@Body request: TopUpQrisRequest, @Header("x_access_token") accessToken: String): Response<TopUpResponseDto>

    @POST("omni/menu")
    suspend fun omniMenu(@Body request: OmniRequests, @Header("x_access_token") accessToken: String): Response<OmniMenuResponseDto>

    @POST("omni/package_list")
    suspend fun omniPackageList(@Body request: OmniRequests, @Header("x_access_token") accessToken: String): Response<OmniPackageListResponseDto>

    @POST("omni/package_order")
    suspend fun omniPackageOrder(@Body request: OmniRequests, @Header("x_access_token") accessToken: String): Response<OmniPackageOrderResponseDto>

    @POST("trx/send_bulk")
    suspend fun sendBulk(@Body request: TransactionBulkRequestDto, @Header("x_access_token") accessToken: String): Response<ErrorMessageResponse>
}