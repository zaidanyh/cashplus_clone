package com.pasukanlangit.id.domain_downline.usecases

import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.*
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RegisterDownLine(
    private val repository: DownLineRepository
) {
    operator fun invoke(
        name: String, owner: String, phoneNumber: String, emailAddress: String, password: String,
        address: String, prov: String, city: String, district: String, village: String, zipCode: String
    ): Flow<DataState<MessageResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(uuid == null || accessToken == null) {
                throw Exception(Constants.invalidAuth)
            }

            val request = RegisterDownLineRequest(
                uuid = uuid, phone = phoneNumber,  fullName = name, ownerName = owner, email = emailAddress,
                password = password, address = address, prov = prov, city = city, district = district,
                village = village, zipcode = zipCode
            )
            val response = repository.registerDownLine(
                request = request,
                accessToken = accessToken
            )
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}