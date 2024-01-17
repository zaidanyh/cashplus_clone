package com.pasukanlangit.cashplus.data.api

import com.pasukanlangit.cashplus.model.request_body.*
import com.pasukanlangit.cashplus.model.response.*
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.core.model.BalanceResponseCore
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.nobu.datasource.network.dto.TopUpResponseDto
import okhttp3.ResponseBody
import retrofit2.Response

interface ApiHelper {
    suspend fun downloadFile( url: String, data: String): Response<ResponseBody?>?

    suspend fun downloadFileExport( url: String,export: Boolean, data: String): Response<ResponseBody?>?

    suspend fun login(loginRequest : LoginRequestDto, signature: String): Response<UserModel>

    suspend fun loginByOtp(loginByOtp: LoginByOtpRequestDto): Response<OTPResponseDto>

    suspend fun loginOtpCode(otpCodeRequest: ResetPwCodeRequest): Response<UserModel>

    suspend fun updateFirebaseToken(request: UpdateFirebaseTokenRequest, accessToken: String) : Response<Void>

    suspend fun logout(request : ProfileRequest, token: String) : Response<UserModel>

    suspend fun deleteAccount(request: DeleteAccountRequestDto, token: String): Response<Void>

    suspend fun uploadKYC(request: KYCRequest, token : String) : Response<UserModel>

    suspend fun updateProfile(updateProfilRequest: UpdateProfilRequest, token: String) : Response<UserModel>

    suspend fun updateEmail(updateEmailRequestDto: UpdateEmailRequestDto, accessToken: String): Response<UserModel>

    suspend fun updateLatLong(latLongRequest: UpdateLatLongRequest, token : String) : Response<UserModel>

    suspend fun changeReferral(referralChangeRequest: ReferralChangeRequest, token : String) : Response<UserModel>

    suspend fun register(user : RegisterRequestDto) : Response<OTPResponseDto>

    suspend fun registerOtpCode(resetPwCodeRequest: ResetPwCodeRequest) : Response<UserModel>

    suspend fun changePin(changePinRequest: ChangePinRequest, token : String) : Response<UserModel>

    suspend fun changePinWithoutLastPin(resetPINRequestDto: ResetPINRequestDto, accessToken: String) : Response<OTPResponseDto>

    suspend fun sendOtpChangePIN(changePinOtpCodeRequest: ResetPinOtpCodeRequest,accessToken: String) : Response<UserModel>

    suspend fun getProfile(profileRequest: ProfileRequest, token : String) : Response<ProfileResponse>

    suspend fun getBalance(request: ProfileRequest, token: String) : Response<BalanceResponseCore>

    suspend fun topUpBank(topUpBankRequest: TopUpBankRequest, token: String) : Response<TopUpBankResponse>

    suspend fun registrationNicePay(nicepayRegistrationRequest: NicepayRegistrationRequest,accessToken: String) : Response<NicepayRegistrationResponse>

    suspend fun registrationNicePayEwallet(ewalletRequest: NicePayEwalletRequest, accessToken: String) : Response<NicePayEwalletResponse>

    suspend fun registrationQrisNicePay(qrisRegistrationRequest: QrisRegistrationRequest, accessToken: String): Response<QrisRegistrationResponse>

    suspend fun getBillNicePay(nicepayRegistrationRequest: NicepayRegistrationRequest, accessToken: String) : Response<BillNicePayResponse>

    suspend fun getProductStoreList(request: TokoOnlineListRequest, accessToken: String) : Response<ProductStoreResponse>

    suspend fun getProductList(productRequest: ProductRequest, token: String) : Response<ProductResultModel>

    suspend fun calculateUnitPrice(request: CalculateUnitPriceRequestDto, accessToken: String): Response<CalculateUnitPriceResponseDto>

    suspend fun sendTransactionPrePaid(transactionRequest: TransactionRequest, token: String) : Response<TransactionResponse>

    suspend fun sendTransactionTAG(transactionRequest: TransactionRequest, token: String) : Response<TransactionTAGResponse>

    suspend fun sendTransactionPostPaid(transactionRequest: TransactionRequest, accessToken: String) : Response<TransactionPayResponse>

    suspend fun getTransactionHistory( trxHistoryRequest: TrxHistoryRequest, token: String) : Response<TransactionHistoryResponse>

    suspend fun getTransactionHistoryTopUp( trxHistoryRequest: TrxHistoryRequest, token: String) : Response<HistoryTopUpResponse>

    suspend fun checkTransaction(transactionCheckRequest: TransactionCheckRequest, token: String) : Response<TransactionHistoryResponse>

    suspend fun getProvinces(request : ProfileRequest) : Response<ProvinceResponse>

    suspend fun getDistrcits(districtRequest: DistrictRequest) : Response<DistrictsResponse>

    suspend fun getSubDistrcits(subdistrictRequest: SubdistrictRequest) : Response<SubdistrictsResponse>

    suspend fun getVillages(villageRequest: VillageRequest) : Response<VillageResponse>

    suspend fun changePassword(request: ChangePasswordRequest, token: String) : Response<UserModel>

    suspend fun getProductStore(productStoreRequest: ProductStoreRequest, accessToken: String) : Response<ProductStoreResponse>

    suspend fun productStoreSearch(request: ProductSearchRequest, accessToken: String) : Response<ProductStoreResponse>

    suspend fun categoryProduct(request: ProfileRequest) : Response<CategoryProductResponse>

    suspend fun resetPassword(resetPassRequestDto: ResetPassRequestDto) : Response<OTPResponseDto>

    suspend fun resetPasswordCode(resetPwCodeRequest: ResetPwCodeRequest) : Response<UserModel>

    suspend fun getServerIdGames(oprNameRequest: OprNameRequest, accessToken: String) : Response<ServerIdGamesResponse>

    suspend fun updateSellingPrice(request: UpdateSellingPriceRequestDto, accessToken: String): Response<Void>

    suspend fun getSellingPrice(request: SellingPriceRequest, accessToken: String): Response<SellingPriceResponse>

    suspend fun getMainOrderBook(request: ProfileRequest, accessToken: String) : Response<AddressMainBookResponse>

    suspend fun getListAddressBook(request: ProfileRequest, accessToken: String) : Response<AllAddressBookResponse>

    suspend fun getProvinceShipment(request: ProfileRequest, accessToken: String) : Response<ProvinceShipmentResponse>

    suspend fun getCityShipment(cityShipmentRequest: CityShipmentRequest, accessToken: String) : Response<CityShipmentResponse>

    suspend fun getSubDistrictShipment(subdistrictShipmentRequest: SubdistrictShipmentRequest, accessToken: String) : Response<SubdistrictShipmentResponse>

    suspend fun addAddressShipment(addAddressBookRequest: AddAddressBookRequest,accessToken: String) : Response<UserModel>

    suspend fun updateAddressShipment(addAddressBookRequest: AddAddressBookRequest, accessToken: String) : Response<UserModel>

    suspend fun deleteAddressShipment(addAddressBookRequest: AddAddressBookRequest, accessToken: String) : Response<UserModel>

    suspend fun getPriceShipment(priceRequest: CourierPriceRequest, accessToken: String) : Response<CourierPriceResponse>

    suspend fun orderOnlineStore(orderRequest: OnlineStoreOrderRequest, accessToken: String) : Response<OnlineStoreOrderResponse>

    suspend fun orderCheckTokoOnline(orderCheckTokoRequest: OrderCheckTokoRequest, accessToken: String) : Response<OrderCheckTokoResponse>

    suspend fun setTrxTOReceived(orderCheckTokoRequest: OrderCheckTokoRequest, accessToken: String) : Response<ErrorMessageResponse>

    suspend fun getHotelOrderList(hotelListByCityRequest: HotelListByCityRequest,accessToken: String) : Response<HotelListResponse>

    suspend fun orderTrackingTokoOnline(request: OrderCheckTokoRequest, accessToken: String) : Response<TrackingOrderResponse>

    suspend fun printReciept(request: OrderCheckTokoRequest,accessToken: String) : Response<PrintTrxResponse>

    suspend fun newPrintReceipt(request: PrintReceiptRequestDto, accessToken: String): Response<PrintReceiptResponseDto>

    suspend fun getCurrentVersionApps(request: ProfileRequest) : Response<AppsVersionResponse>

    suspend fun getIsBinded(request: ProfileRequest, accessToken: String): Response<BindedResponse>

    suspend fun getBalanceInquiry(request: ProfileRequest, accessToken: String): Response<BalanceInquiryResponse>

    suspend fun topUpQris(request: TopUpQrisRequest, accessToken: String): Response<TopUpResponseDto>

    suspend fun omniMenu(requets: OmniRequests, accessToken: String): Response<OmniMenuResponseDto>

    suspend fun omniPackageList(request: OmniRequests, accessToken: String): Response<OmniPackageListResponseDto>

    suspend fun omniPackageOrder(request: OmniRequests, accessToken: String): Response<OmniPackageOrderResponseDto>

    suspend fun sendBulk(request: TransactionBulkRequestDto, accessToken: String): Response<ErrorMessageResponse>
}