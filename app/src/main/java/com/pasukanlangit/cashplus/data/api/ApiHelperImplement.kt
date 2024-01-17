package com.pasukanlangit.cashplus.data.api

import com.pasukanlangit.cashplus.BuildConfig.*
import com.pasukanlangit.cashplus.model.request_body.*
import com.pasukanlangit.cashplus.model.response.*
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.id.core.model.BalanceResponseCore
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.nobu.datasource.network.dto.TopUpResponseDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Url

class ApiHelperImplement(private val apiService: ApiService) : ApiHelper {
    override suspend fun downloadFile(
        @Url url: String,
        data: String
    ): Response<ResponseBody?>? = apiService.downloadFile(url,data)

    override suspend fun downloadFileExport(
        url: String,
        export: Boolean,
        data: String
    ): Response<ResponseBody?>? = apiService.downloadFileExport(url, export,data)

    override suspend fun login(loginRequest: LoginRequestDto, signature: String): Response<UserModel> =
        apiService.login(loginRequest, signature)

    override suspend fun loginByOtp(loginByOtp: LoginByOtpRequestDto): Response<OTPResponseDto> =
        apiService.loginByOtp(loginByOtp)

    override suspend fun loginOtpCode(otpCodeRequest: ResetPwCodeRequest): Response<UserModel> =
        apiService.loginOtpCode(otpCodeRequest)

    override suspend fun updateFirebaseToken(
        request: UpdateFirebaseTokenRequest, accessToken: String
    ): Response<Void> = apiService.updateFirebaseToken(request, accessToken)

    override suspend fun logout(request: ProfileRequest, token: String): Response<UserModel> =
        apiService.logout(request, token)

    override suspend fun deleteAccount(
        request: DeleteAccountRequestDto,
        token: String
    ): Response<Void> = apiService.deleteAccount(request, token)

    override suspend fun uploadKYC(request: KYCRequest, token: String): Response<UserModel> = apiService.uploadKYC(request, token)

    override suspend fun updateProfile(
        updateProfilRequest: UpdateProfilRequest,
        token: String
    ): Response<UserModel> = apiService.updateProfile(updateProfilRequest, token)

    override suspend fun updateEmail(
        updateEmailRequestDto: UpdateEmailRequestDto,
        accessToken: String
    ): Response<UserModel> = apiService.updateEmail(updateEmailRequestDto, accessToken)

    override suspend fun updateLatLong(
        latLongRequest: UpdateLatLongRequest,
        token: String
    ): Response<UserModel> = apiService.updateLatLong(latLongRequest, token)
    override suspend fun changeReferral(
        referralChangeRequest: ReferralChangeRequest,
        token: String
    ): Response<UserModel> = apiService.changeReferral(referralChangeRequest, token)

    override suspend fun register(user: RegisterRequestDto): Response<OTPResponseDto> = apiService.register(user)

    override suspend fun registerOtpCode(resetPwCodeRequest: ResetPwCodeRequest): Response<UserModel> = apiService.registerOtpCode(resetPwCodeRequest)

    override suspend fun changePin(
        changePinRequest: ChangePinRequest,
        token: String
    ): Response<UserModel> = apiService.changePin(changePinRequest, token)

    override suspend fun changePinWithoutLastPin(
        resetPINRequestDto: ResetPINRequestDto,
        accessToken: String
    ): Response<OTPResponseDto> = apiService.changePinWithoutLastPin(resetPINRequestDto,accessToken)

    override suspend fun sendOtpChangePIN(changePinOtpCodeRequest: ResetPinOtpCodeRequest, accessToken: String): Response<UserModel> = apiService.sendOtpChangePIN(changePinOtpCodeRequest, accessToken)

    override suspend fun getProfile(
        profileRequest: ProfileRequest,
        token: String
    ): Response<ProfileResponse> = apiService.getProfile(profileRequest, token)

    override suspend fun getBalance(
        request: ProfileRequest,
        token: String
    ): Response<BalanceResponseCore> = apiService.getBalance(request, token)

    override suspend fun topUpBank(
        topUpBankRequest: TopUpBankRequest,
        token: String
    ): Response<TopUpBankResponse> = apiService.topUpBank(topUpBankRequest, token)

    override suspend fun registrationNicePay(
        nicepayRegistrationRequest: NicepayRegistrationRequest,
        accessToken: String
    ): Response<NicepayRegistrationResponse> = apiService.registrationNicePay(nicepayRegistrationRequest, accessToken)

    override suspend fun registrationNicePayEwallet(
        ewalletRequest: NicePayEwalletRequest,
        accessToken: String
    ): Response<NicePayEwalletResponse> = apiService.registrationNicePayEwallet(ewalletRequest, accessToken)

    override suspend fun registrationQrisNicePay(
        qrisRegistrationRequest: QrisRegistrationRequest,
        accessToken: String
    ): Response<QrisRegistrationResponse> = apiService.registrationQrisNicePay(qrisRegistrationRequest, accessToken)

    override suspend fun getBillNicePay(
        nicepayRegistrationRequest: NicepayRegistrationRequest,
        accessToken: String
    ): Response<BillNicePayResponse> = apiService.getBillNicePay(nicepayRegistrationRequest, accessToken)

    override suspend fun getProductStoreList(
        request: TokoOnlineListRequest,
        accessToken: String
    ): Response<ProductStoreResponse> = apiService.getProductStoreList(request, accessToken)

    override suspend fun getProductList(
        productRequest: ProductRequest,
        token: String
    ): Response<ProductResultModel> = apiService.getProductList(productRequest, token)

    override suspend fun calculateUnitPrice(
        request: CalculateUnitPriceRequestDto,
        accessToken: String
    ): Response<CalculateUnitPriceResponseDto> = apiService.calculateUnitPrice(request, accessToken)

    override suspend fun sendTransactionPrePaid(
        transactionRequest: TransactionRequest,
        token: String
    ): Response<TransactionResponse> = apiService.sendTransactionPrePaid(transactionRequest, token)

    override suspend fun sendTransactionTAG(
        transactionRequest: TransactionRequest,
        token: String
    ): Response<TransactionTAGResponse> = apiService.sendTransactionTAG(transactionRequest, token)

    override suspend fun sendTransactionPostPaid(
        transactionRequest: TransactionRequest,
        accessToken: String
    ): Response<TransactionPayResponse> = apiService.sendTransactionPostPaid(transactionRequest, accessToken)

    override suspend fun getTransactionHistory(
        trxHistoryRequest: TrxHistoryRequest,
        token: String
    ): Response<TransactionHistoryResponse> = apiService.getTransactionHistory(
        "${BASE_URL_HISTORY}trx/history",
        trxHistoryRequest,
        token
    )

    override suspend fun getTransactionHistoryTopUp(
        trxHistoryRequest: TrxHistoryRequest,
        token: String
    ): Response<HistoryTopUpResponse> = apiService.getTransactionHistoryTopUp(
        "${BASE_URL_HISTORY}balance/deposit_history",
        trxHistoryRequest,
        token
    )

    override suspend fun checkTransaction(
        transactionCheckRequest: TransactionCheckRequest,
        token: String
    ): Response<TransactionHistoryResponse> =
        apiService.checkTransaction("${BASE_URL_HISTORY}trx/check", transactionCheckRequest, token)

    override suspend fun getProvinces(request: ProfileRequest): Response<ProvinceResponse> =
        apiService.getProvinces(request)

    override suspend fun getDistrcits(districtRequest: DistrictRequest): Response<DistrictsResponse> =
        apiService.getDistricts(districtRequest)

    override suspend fun getSubDistrcits(subdistrictRequest: SubdistrictRequest): Response<SubdistrictsResponse> =
        apiService.getSubDistricts(subdistrictRequest)

    override suspend fun getVillages(villageRequest: VillageRequest): Response<VillageResponse> =
        apiService.getVillages(villageRequest)

    override suspend fun changePassword(
        request: ChangePasswordRequest,
        token: String
    ): Response<UserModel> = apiService.changePassword(request, token)

    override suspend fun getProductStore(
        productStoreRequest: ProductStoreRequest,
        accessToken: String
    ): Response<ProductStoreResponse> = apiService.getProductStore(productStoreRequest, accessToken)

    override suspend fun productStoreSearch(
        request: ProductSearchRequest,
        accessToken: String
    ): Response<ProductStoreResponse> = apiService.productStoreSearch(request, accessToken)

    override suspend fun categoryProduct(
        request: ProfileRequest): Response<CategoryProductResponse>  = apiService.categoryProduct(request)

    override suspend fun resetPassword(resetPassRequestDto: ResetPassRequestDto): Response<OTPResponseDto> = apiService.resetPassword(resetPassRequestDto)

    override suspend fun resetPasswordCode(resetPwCodeRequest: ResetPwCodeRequest): Response<UserModel> = apiService.resetPasswordCode(resetPwCodeRequest)

    override suspend fun getServerIdGames(
        oprNameRequest: OprNameRequest,
        accessToken: String
    ): Response<ServerIdGamesResponse> = apiService.getServerIdGames(oprNameRequest, accessToken)

    override suspend fun updateSellingPrice(
        request: UpdateSellingPriceRequestDto,
        accessToken: String
    ): Response<Void> = apiService.updateSellingPrice(request, accessToken)

    override suspend fun getSellingPrice(
        request: SellingPriceRequest,
        accessToken: String
    ): Response<SellingPriceResponse> = apiService.getSellingPrice(request, accessToken)

    override suspend fun getMainOrderBook(
        request: ProfileRequest,
        accessToken: String
    ): Response<AddressMainBookResponse> = apiService.getMainOrderBook(request, accessToken)

    override suspend fun getListAddressBook(
        request: ProfileRequest,
        accessToken: String
    ): Response<AllAddressBookResponse> = apiService.getListAddressBook(request, accessToken)

    override suspend fun getProvinceShipment(
        request: ProfileRequest,
        accessToken: String
    ): Response<ProvinceShipmentResponse> = apiService.getProvinceShipment(request, accessToken)

    override suspend fun getCityShipment(
        cityShipmentRequest: CityShipmentRequest,
        accessToken: String
    ): Response<CityShipmentResponse> = apiService.getCityShipment(cityShipmentRequest, accessToken)

    override suspend fun getSubDistrictShipment(
        subdistrictShipmentRequest: SubdistrictShipmentRequest,
        accessToken: String
    ): Response<SubdistrictShipmentResponse> = apiService.getSubDistrictShipment(subdistrictShipmentRequest, accessToken)

    override suspend fun addAddressShipment(
        addAddressBookRequest: AddAddressBookRequest,
        accessToken: String
    ): Response<UserModel> = apiService.addAddressShipment(addAddressBookRequest, accessToken)

    override suspend fun updateAddressShipment(
        addAddressBookRequest: AddAddressBookRequest,
        accessToken: String
    ): Response<UserModel> = apiService.updateAddressShipment(addAddressBookRequest, accessToken)

    override suspend fun deleteAddressShipment(
        addAddressBookRequest: AddAddressBookRequest,
        accessToken: String
    ): Response<UserModel> = apiService.deleteAddressShipment(addAddressBookRequest, accessToken)

    override suspend fun getPriceShipment(
        priceRequest: CourierPriceRequest,
        accessToken: String
    ): Response<CourierPriceResponse> = apiService.getPriceShipment(priceRequest, accessToken)

    override suspend fun orderOnlineStore(
        orderRequest: OnlineStoreOrderRequest,
        accessToken: String
    ): Response<OnlineStoreOrderResponse> = apiService.orderOnlineStore(orderRequest, accessToken)

    override suspend fun orderCheckTokoOnline(
        orderCheckTokoRequest: OrderCheckTokoRequest,
        accessToken: String
    ): Response<OrderCheckTokoResponse> = apiService.orderCheckTokoOnline(orderCheckTokoRequest, accessToken)

    override suspend fun setTrxTOReceived(
        orderCheckTokoRequest: OrderCheckTokoRequest,
        accessToken: String
    ): Response<ErrorMessageResponse> = apiService.setTrxTOReceived(orderCheckTokoRequest, accessToken)

    override suspend fun getHotelOrderList(
        hotelListByCityRequest: HotelListByCityRequest,
        accessToken: String
    ): Response<HotelListResponse> = apiService.getHotelOrderList(hotelListByCityRequest, accessToken)

    override suspend fun orderTrackingTokoOnline(
        request: OrderCheckTokoRequest,
        accessToken: String
    ): Response<TrackingOrderResponse> = apiService.orderTrackingTokoOnline(request, accessToken)

    override suspend fun printReciept(
        request: OrderCheckTokoRequest,
        accessToken: String
    ): Response<PrintTrxResponse> = apiService.printReciept(request, accessToken)

    override suspend fun newPrintReceipt(
        request: PrintReceiptRequestDto,
        accessToken: String
    ): Response<PrintReceiptResponseDto> = apiService.newPrintReceipt(request, accessToken)

    override suspend fun getCurrentVersionApps(request: ProfileRequest): Response<AppsVersionResponse> = apiService.getCurrentVersionApps(request)

    override suspend fun getIsBinded(
        request: ProfileRequest,
        accessToken: String
    ): Response<BindedResponse> = apiService.getIsBinded(request, accessToken)

    override suspend fun getBalanceInquiry(
        request: ProfileRequest,
        accessToken: String
    ): Response<BalanceInquiryResponse> = apiService.getBalanceInquiry(request, accessToken)

    override suspend fun topUpQris(
        request: TopUpQrisRequest,
        accessToken: String
    ): Response<TopUpResponseDto> = apiService.topUpQris(request, accessToken)

    override suspend fun omniMenu(
        requets: OmniRequests,
        accessToken: String
    ): Response<OmniMenuResponseDto> = apiService.omniMenu(requets, accessToken)

    override suspend fun omniPackageList(
        request: OmniRequests,
        accessToken: String
    ): Response<OmniPackageListResponseDto> = apiService.omniPackageList(request, accessToken)

    override suspend fun omniPackageOrder(
        request: OmniRequests,
        accessToken: String
    ): Response<OmniPackageOrderResponseDto> = apiService.omniPackageOrder(request, accessToken)

    override suspend fun sendBulk(
        request: TransactionBulkRequestDto,
        accessToken: String
    ): Response<ErrorMessageResponse> = apiService.sendBulk(request, accessToken)
}