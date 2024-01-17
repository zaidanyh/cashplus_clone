package com.pasukanlangit.id.cash_transfer.domain.utils

import com.pasukanlangit.id.cash_transfer.domain.model.FetchBankByCountryResponse
import com.pasukanlangit.id.cash_transfer.domain.model.FetchNationResponse
import com.pasukanlangit.id.cash_transfer.domain.model.FetchRelationshipsResponse
import com.pasukanlangit.id.cash_transfer.domain.model.GlobalBankSavedResponse

data class BeneficiaryAccountResponse(
    val banks: List<FetchBankByCountryResponse>,
    val nations: List<FetchNationResponse>,
    val relationship: List<FetchRelationshipsResponse>,
    val banksSaved: List<GlobalBankSavedResponse>
)
