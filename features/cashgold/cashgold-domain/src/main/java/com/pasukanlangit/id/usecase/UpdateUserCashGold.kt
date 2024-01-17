package com.pasukanlangit.id.usecase

import com.pasukanlangit.id.CashGoldRepository
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.model.UpdateUserCashGold
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateUserCashGold(
    private val repository: CashGoldRepository
) {
    operator fun invoke(
        profession: String? = null,
        zipCode: String? = null,
        taxIdentityNumber: String?,
        incomePerYear: String? = null,
        address: String? = null,
        gender: String? = null,
        lastEducation: String? = null,
        city: String? = null,
        religion: String? = null,
        birthPlace: String? = null,
        province: String? = null,
        phone: String?,
        identityNumber: String?,
        dayOfBirth: String? = null,
        district: String? = null,
        incomeSource: String? = null,
        village: String? = null,
        user: String? = null,
        email: String?,
        maritalStatus: String? = null
    ): Flow<DataState<Boolean>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(email == null){
                throw Exception(Constants.emailEmpty)
            }

            if(phone == null){
                throw Exception(Constants.phoneEmpty)
            }

            if(identityNumber == null){
                throw Exception(Constants.ktpEmpty)
            }

            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }

            val response = repository.updateUserCashGold(
                UpdateUserCashGold(
                    profession = profession,
                    zipCode = zipCode,
                    taxIdentityNumber = taxIdentityNumber,
                    incomePerYear = incomePerYear,
                    address = address,
                    gender = gender,
                    lastEducation = lastEducation,
                    city = city,
                    uuid = uuid,
                    religion = religion,
                    birthPlace = birthPlace,
                    province = province,
                    phone = phone,
                    identityNumber = identityNumber,
                    dayOfBirth = dayOfBirth,
                    district = district,
                    incomeSource = incomeSource,
                    village = village,
                    user = user,
                    email = email,
                    maritalStatus = maritalStatus
                ), accessToken
            )
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}