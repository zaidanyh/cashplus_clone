package com.pasukanlangit.id.nobu.domain.usecase

data class NobuUseCases(
    val getProfile: GetProfile,
    val getIsBindedUseCase: IsBindedUseCase,
    val getSecurityQuestions: GetSecurityQuestions,
    val getTermCondition: GetTermCondition,
    val nobuAccountCreation: NobuAccountCreation,
    val nobuAccountBinding: GetAccountBinding,
    val verifyOtpUseCase: VerifyOtpUseCase,
    val sendResultScan: SendResultScan,
    val historyNobuUseCase: GetHistoryTrxNobu,
    val unBindAccount: UnBindAccountUseCase
)