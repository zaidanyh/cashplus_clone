 package com.pasukanlangit.cashplus.repository

import com.pasukanlangit.cashplus.data.api.ApiHelper
import com.pasukanlangit.cashplus.data.room.AddressBookDao
import com.pasukanlangit.cashplus.data.room.CartProductDao
import com.pasukanlangit.cashplus.model.request_body.*
import com.pasukanlangit.cashplus.model.response.*
import okhttp3.ResponseBody
import retrofit2.Response

 class MainRepository(
     private val apiHelper: ApiHelper, private val cartProductDao: CartProductDao,
     private val addressBookDao: AddressBookDao
 ) {
     suspend fun downloadFile(url: String, data: String): Response<ResponseBody?>? = apiHelper.downloadFile(url, data)

     suspend fun downloadFileExport(
         url: String,
         export: Boolean,
         data: String
     ): Response<ResponseBody?>? = apiHelper.downloadFileExport(url, export,data)

     suspend fun login(loginRequest: LoginRequestDto, signature: String) = apiHelper.login(loginRequest, signature )

     suspend fun loginByOtp(loginByOtp: LoginByOtpRequestDto) = apiHelper.loginByOtp(loginByOtp)

     suspend fun loginOtpCode(otpCodeRequest: ResetPwCodeRequest) = apiHelper.loginOtpCode(otpCodeRequest)

     suspend fun logout(request: ProfileRequest, token: String) = apiHelper.logout(request, token)

     suspend fun deleteAccount(request: DeleteAccountRequestDto, token: String) = apiHelper.deleteAccount(request, token)

     suspend fun updateProfile(updateProfilRequest: UpdateProfilRequest, token: String) = apiHelper.updateProfile(updateProfilRequest, token)

     suspend fun updateEmail(updateEmail: UpdateEmailRequestDto, accessToken: String) = apiHelper.updateEmail(updateEmail, accessToken)

     suspend fun updateFirebaseToken(
         request: UpdateFirebaseTokenRequest,
         accessToken: String
     ) = apiHelper.updateFirebaseToken(request, accessToken)

     suspend fun updateLatLong(
         latLongRequest: UpdateLatLongRequest,
         token: String
     ) = apiHelper.updateLatLong(latLongRequest, token)

     suspend fun changeReferral(referralChangeRequest: ReferralChangeRequest,token : String) = apiHelper.changeReferral(referralChangeRequest, token)

     suspend fun register(registerUser : RegisterRequestDto) = apiHelper.register(registerUser)

     suspend fun registerOtpCode(resetPwCodeRequest: ResetPwCodeRequest) = apiHelper.registerOtpCode(resetPwCodeRequest)

     suspend fun changePin(changePinRequest: ChangePinRequest, accessToken : String) = apiHelper.changePin(changePinRequest, accessToken)

     suspend fun changePinWithoutLastPin(resetPINRequestDto: ResetPINRequestDto, accessToken: String) = apiHelper.changePinWithoutLastPin(resetPINRequestDto,accessToken)

     suspend fun getProfile(profileRequest: ProfileRequest, accessToken : String) = apiHelper.getProfile(profileRequest, accessToken)

     suspend fun uploadKYC(request: KYCRequest, token: String): Response<UserModel> = apiHelper.uploadKYC(request, token)

     suspend fun getProductList(productRequest: ProductRequest, accessToken : String) = apiHelper.getProductList(productRequest, accessToken)

     suspend fun calculateUnitPrice(request: CalculateUnitPriceRequestDto, accessToken: String) = apiHelper.calculateUnitPrice(request, accessToken)

     suspend fun getProductStoreList(
         request: TokoOnlineListRequest,
         accessToken: String
     ): Response<ProductStoreResponse> = apiHelper.getProductStoreList(request, accessToken)

     suspend fun sendTransactionPrePaid(transactionRequest: TransactionRequest, accessToken : String) = apiHelper.sendTransactionPrePaid(transactionRequest, accessToken)

     suspend fun sendTransactionPostPaid(
         transactionRequest: TransactionRequest,
         accessToken: String
     ) = apiHelper.sendTransactionPostPaid(transactionRequest, accessToken)

     suspend fun sendTransactionTAG(transactionRequest: TransactionRequest, accessToken : String) = apiHelper.sendTransactionTAG(transactionRequest, accessToken)

     suspend fun getTransactionHistory(trxHistoryRequest: TrxHistoryRequest, accessToken : String) = apiHelper.getTransactionHistory(trxHistoryRequest, accessToken)

     suspend fun getTransactionHistoryTopUp(trxHistoryRequest: TrxHistoryRequest, accessToken : String) = apiHelper.getTransactionHistoryTopUp(trxHistoryRequest, accessToken)

     suspend fun checkTransaction(transactionCheckRequest: TransactionCheckRequest, accessToken : String) = apiHelper.checkTransaction(transactionCheckRequest, accessToken)

     suspend fun getBalance(request: ProfileRequest, accessToken : String) = apiHelper.getBalance(request, accessToken)

     suspend fun getProvinces(request: ProfileRequest) = apiHelper.getProvinces(request)

     suspend fun getDistricts(districtRequest: DistrictRequest) = apiHelper.getDistrcits(districtRequest)

     suspend fun getSubdistricts(subdistrictRequest: SubdistrictRequest) = apiHelper.getSubDistrcits(subdistrictRequest)

     suspend fun getVillages(villageRequest: VillageRequest) = apiHelper.getVillages(villageRequest)

     suspend fun changePassword(
         request: ChangePasswordRequest,
         token: String
     ) = apiHelper.changePassword(request, token)

     suspend fun getProductStore(token: String, productStoreRequest: ProductStoreRequest) = apiHelper.getProductStore(productStoreRequest, token)

     suspend fun searchProductStore(token: String, request: ProductSearchRequest) = apiHelper.productStoreSearch(request, token)

     suspend fun categoryProduct(request: ProfileRequest) = apiHelper.categoryProduct(request)

     suspend fun resetPassword(resetPassRequestDto: ResetPassRequestDto) = apiHelper.resetPassword(resetPassRequestDto)

     suspend fun resetPasswordCode(resetPwCodeRequest: ResetPwCodeRequest) = apiHelper.resetPasswordCode(resetPwCodeRequest)

     suspend fun sendOtpChangePIN(changePinOtpCodeRequest: ResetPinOtpCodeRequest, accessToken: String) = apiHelper.sendOtpChangePIN(changePinOtpCodeRequest, accessToken)

     suspend fun getServerIdGames(
         oprNameRequest: OprNameRequest,
         accessToken: String
     ) = apiHelper.getServerIdGames(oprNameRequest, accessToken)

     suspend fun updateSellingPrice(
         request: UpdateSellingPriceRequestDto,
         accessToken: String
     ) = apiHelper.updateSellingPrice(request, accessToken)

     suspend fun getSellingPrice(
         request: SellingPriceRequest,
         accessToken: String
     ) = apiHelper.getSellingPrice(request, accessToken)

     fun getProductCart(userId: String) = cartProductDao.getAllProductCart(userId)

     fun insertProductToCart(productStoreDataItem: ProductStoreDataItem) = cartProductDao.insertProductToCart(productStoreDataItem)

     fun updateCartsIsChecked(value: Boolean, userId: String) = cartProductDao.updateCartsIsChecked(value, userId)

     fun updateCart(productStoreDataItem: ProductStoreDataItem, userId: String) = cartProductDao.updateCart(
         productStoreDataItem.qty,
         productStoreDataItem.isChecked,
         productStoreDataItem.note,
         productStoreDataItem.productId,
         userId
     )

     fun deleteProductFromCart(productStoreDataItem: ProductStoreDataItem, userId: String) = cartProductDao.deleteProductFromCart(userId, productStoreDataItem.productId)

     fun deleteCheckedProductFromCart(userId: String) = cartProductDao.deleteCheckedProductFromCart(userId)

     fun getSavedAddressBook(userId: String) = addressBookDao.getAddressBookMain(userId)

     suspend fun getListAddressBook(
         request: ProfileRequest,
         accessToken: String
     ) = apiHelper.getListAddressBook(request, accessToken)

     suspend fun getProvinceShipment(
         request: ProfileRequest,
         accessToken: String
     ) = apiHelper.getProvinceShipment(request, accessToken)

     suspend fun getCityShipment(
         cityShipmentRequest: CityShipmentRequest,
         accessToken: String
     ) = apiHelper.getCityShipment(cityShipmentRequest, accessToken)

     suspend fun getSubDistrictShipment(
         request: SubdistrictShipmentRequest,
         accessToken: String
     ) = apiHelper.getSubDistrictShipment(request, accessToken)

     suspend fun addAddressShipment(
         addAddressBookRequest: AddAddressBookRequest,
         accessToken: String
     ) = apiHelper.addAddressShipment(addAddressBookRequest, accessToken)

     suspend fun updateAddressShipment(
         addAddressBookRequest: AddAddressBookRequest,
         accessToken: String
     ) = apiHelper.updateAddressShipment(addAddressBookRequest, accessToken)

     suspend fun getMainOrderBook(
         request: ProfileRequest,
         accessToken: String
     ) = apiHelper.getMainOrderBook(request, accessToken)

     suspend fun getPriceShipment(
         priceRequest: CourierPriceRequest,
         accessToken: String
     ) = apiHelper.getPriceShipment(priceRequest, accessToken)

     suspend fun orderOnlineStore(
         orderRequest: OnlineStoreOrderRequest,
         accessToken: String
     ) = apiHelper.orderOnlineStore(orderRequest, accessToken)

     suspend fun deleteAddressShipment(
         addAddressBookRequest: AddAddressBookRequest,
         accessToken: String
     ) = apiHelper.deleteAddressShipment(addAddressBookRequest, accessToken)

     suspend fun orderCheckTokoOnline(
         orderCheckTokoRequest: OrderCheckTokoRequest,
         accessToken: String
     ) = apiHelper.orderCheckTokoOnline(orderCheckTokoRequest, accessToken)

     suspend fun setTrxTOReceived(
         orderCheckTokoRequest: OrderCheckTokoRequest,
         accessToken: String
     ) = apiHelper.setTrxTOReceived(orderCheckTokoRequest, accessToken)

     suspend fun getHotelOrderList(
         hotelListByCityRequest: HotelListByCityRequest,
         accessToken: String
     ) = apiHelper.getHotelOrderList(hotelListByCityRequest, accessToken)

     suspend fun orderTrackingTokoOnline(
         request: OrderCheckTokoRequest,
         accessToken: String
     ) = apiHelper.orderTrackingTokoOnline(request, accessToken)

     suspend fun printReciept(
         request: OrderCheckTokoRequest,
         accessToken: String
     ) = apiHelper.printReciept(request, accessToken)

     suspend fun newPrintReceipt(
         requestDto: PrintReceiptRequestDto, accessToken: String
     ) = apiHelper.newPrintReceipt(requestDto, accessToken)

     suspend fun getCurrentVersionApps(request: ProfileRequest) = apiHelper.getCurrentVersionApps(request)

     suspend fun getIsBinded(request: ProfileRequest, accessToken: String) = apiHelper.getIsBinded(request, accessToken)

     suspend fun getBalanceInquiry(request: ProfileRequest, accessToken: String) = apiHelper.getBalanceInquiry(request, accessToken)

     suspend fun topUpQris(request: TopUpQrisRequest, accessToken: String) = apiHelper.topUpQris(request, accessToken)

     suspend fun omniMenu(request: OmniRequests, accessToken: String) = apiHelper.omniMenu(request, accessToken)

     suspend fun omniPackageList(request: OmniRequests, accessToken: String) = apiHelper.omniPackageList(request, accessToken)

     suspend fun omniPackageOrder(request: OmniRequests, accessToken: String) = apiHelper.omniPackageOrder(request, accessToken)

     suspend fun transactionBulk(request: TransactionBulkRequestDto, accessToken: String) = apiHelper.sendBulk(request, accessToken)
}