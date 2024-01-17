package com.pasukanlangit.id.kyc_domain.domain.usecase

import com.pasukanlangit.id.kyc_domain.domain.KycRepository
import com.pasukanlangit.id.kyc_domain.domain.model.EKycVerifyRequest
import com.pasukanlangit.id.kyc_domain.domain.temp.VerifyOnly
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EKycVerifyUseCase(
    private val repository: KycRepository
) {
    operator fun invoke(verificationType: String): Flow<DataState<VerifyOnly>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = EKycVerifyRequest(uuid, verificationType)
            val response = repository.verifyingEKyc(request, accessToken)
            val parseResponse = VerifyOnly(
                rc = response.rc, msg = response.msg, ocrNIK = response.ocrNIK,
                ocrFullName = response.ocrFullName, ownerName = response.ownerName,
                verificationType = verificationType
            )
            emit(DataState.data(data = parseResponse))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}