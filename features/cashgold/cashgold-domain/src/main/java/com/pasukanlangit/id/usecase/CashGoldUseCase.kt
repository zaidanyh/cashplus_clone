package com.pasukanlangit.id.usecase

data class CashGoldUseCase(
    val getIndoGoldBalance: GetIndoGoldBalance,
    val getGoldPrice: GetGoldPrice,
    val getChartUseCase: GetChartUseCase,
    val checkStatusRegister: CheckStatusRegister,
    val registerUseCase: RegisterUseCase,
    val getSt24Profile: GetSt24Profile,
    val kycStatus: CheckKycStatus,
    val lockGold: LockGold,
    val sendTrx: SendTrx,
    val getProductWithDraw: GetProductWithDraw,
    val mainAddress: MainAddress,
    val addressList: AddressList,
    val getProvinces: GetProvinces,
    val getCity: GetCity,
    val getDistrict: GetDistrict,
    val getVillage: GetVillage,
    val addAddress: AddAddress,
    val updateAddress: UpdateAddress,
    val deleteAddress: DeleteAddress,
    val updateUserCashGold: UpdateUserCashGold,
    val checkKtpNotEmpty: CheckKtpNotEmpty,
    val withDrawBook: WithDrawBook,
    val getProfileCashGold: GetUserCashGold
)
