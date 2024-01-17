package com.pasukanlangit.id.kyc_domain.domain.usecase

import com.pasukanlangit.id.kyc_domain.domain.KycRepository
import com.pasukanlangit.id.kyc_domain.domain.model.EKycVerifyRequest
import com.pasukanlangit.id.kyc_domain.domain.model.EkycRequest
import com.pasukanlangit.id.kyc_domain.domain.temp.UploadAndVerify
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EKycUploadVerifyUseCase(
    private val repository: KycRepository
) {
    operator fun invoke(
        verificationType: String,
        uploadType: String,
        base64Data: String
    ): Flow<DataState<UploadAndVerify>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val responseUpload = repository.uploadEKycFile(
                EkycRequest(uuid, uploadType, base64Data), accessToken
            )
            val responseVerify = repository.verifyingEKyc(
                EKycVerifyRequest(uuid, verificationType), accessToken
            )

            val response = UploadAndVerify(
                rc = responseVerify.rc, msg = responseVerify.msg, uploadType = responseUpload.uploadType,
                ocrNIK = responseVerify.ocrNIK, ocrFullName = responseVerify.ocrFullName,
                ownerName = responseVerify.ownerName
            )

            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}