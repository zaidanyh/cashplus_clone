package com.pasukanlangit.id.usecase

import com.pasukanlangit.id.CashGoldRepository
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.model.UpdateAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateAddress(
    private val repository: CashGoldRepository
) {
    operator fun invoke(
        zipCode: String,
        address: String,
        province: String,
        city: String,
        district: String,
        village: String,
        isMain: Boolean?,
        id: Int?
    ): Flow<DataState<Boolean>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }

            if(id == null){
                throw Exception(Constants.addressIdEmpty)
            }
            if(isMain == null){
                throw Exception(Constants.addressIsDefaultEmpty)
            }
            val response = repository.updateAddress(
                UpdateAddress(
                    zipCode = zipCode,
                    address = address,
                    province = province,
                    city = city,
                    district = district,
                    village = village,
                    uuid = uuid,
                    isMain = isMain,
                    id = id
                )
                , accessToken
            )
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}