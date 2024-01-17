package com.pasukanlangit.cash_topup.domain.usecase

data class TopUpUseCase(
    val balanceCheck: BalanceCheckUseCase,
    val costNicePay: CostNicePayUseCase,
    val viaBank: ViaBankUseCase,
    val viaVA: ViaVAUseCase,
    val viaMerchant: ViaMerchantUseCase,
    val viaEWallet: ViaEWalletUseCase,
    val viaQRIS: ViaQRISUseCase,
    val flipAcceptList: GetFlipAcceptListUseCase,
    val costFlipAccept: CostFlipAcceptUseCase,
    val createBillFlip: FlipAcceptCreateBillUseCase
)
