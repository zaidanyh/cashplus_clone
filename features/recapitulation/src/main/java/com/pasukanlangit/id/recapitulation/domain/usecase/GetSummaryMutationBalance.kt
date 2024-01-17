package com.pasukanlangit.id.recapitulation.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.pasukanlangit.id.recapitulation.domain.RecapRepository
import com.pasukanlangit.id.recapitulation.domain.model.MutationDepositResponse
import com.pasukanlangit.id.recapitulation.domain.usecase.paging.GetMutationBalancePaging
import kotlinx.coroutines.flow.Flow

class GetSummaryMutationBalance(
    private val repository: RecapRepository
) {
    private var pagingConfig: PagingConfig

    init {
        pagingConfig = PagingConfig(pageSize = 1)
    }

    operator fun invoke(dateStart: String, dateEnd: String): Flow<PagingData<MutationDepositResponse>> =
        Pager(config = pagingConfig, pagingSourceFactory = {
            GetMutationBalancePaging(repository = repository, dateStart = dateStart, dateEnd = dateEnd)
        }).flow
}