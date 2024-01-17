package com.pasukanlangit.id.kyc_domain.domain.usecase

import com.pasukanlangit.id.kyc_domain.domain.KycRepository
import com.pasukanlangit.id.kyc_domain.domain.model.EKycProfileUser
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.domain.model.UUIDRequest
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EKycProfile(
    private val repository: KycRepository
) {
    operator fun invoke(): Flow<DataState<EKycProfileUser>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = UUIDRequest(uuid)
            val response = repository.profileUser(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}