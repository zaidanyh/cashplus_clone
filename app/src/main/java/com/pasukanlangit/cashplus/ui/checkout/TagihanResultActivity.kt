package com.pasukanlangit.cashplus.ui.checkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.MainActivityNavComp
import com.pasukanlangit.cashplus.databinding.ActivityTagihanResultBinding
import com.pasukanlangit.cashplus.model.response.TransactionPayResponse
import com.pasukanlangit.id.core.utils.parcelable

class TagihanResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTagihanResultBinding

    private lateinit var adapter: DetailTrxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTagihanResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transactionPayResponse = intent.parcelable<TransactionPayResponse>(KEY_TRANSACTION_RESPONSE)
        adapter = DetailTrxAdapter()
        with(binding){
            btnGoHistory.setOnClickListener {
                finishAffinity()
                startActivity(Intent(this@TagihanResultActivity, MainActivityNavComp::class.java).putExtra(MainActivityNavComp.FORWARDING_TO_HISTORY, true))
            }

            transactionPayResponse?.let { pay ->
                val splittedInfo = pay.billData?.info?.split("||")

                if(pay.billData != null && !splittedInfo.isNullOrEmpty()){
                    tvInfo.text = splittedInfo[0]
                    rvDetailTrx.layoutManager = LinearLayoutManager(this@TagihanResultActivity)
                    rvDetailTrx.adapter = adapter
                    adapter.setInfoDetails(splittedInfo[1].split("|"))
                }
            }
        }
    }

    companion object {
        const val KEY_TRANSACTION_RESPONSE = "KEY_TRANSACTION_RESPONSE"
    }
}