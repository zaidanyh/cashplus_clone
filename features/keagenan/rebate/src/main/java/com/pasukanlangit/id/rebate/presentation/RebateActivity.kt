package com.pasukanlangit.id.rebate.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.rebate.R
import com.pasukanlangit.id.rebate.databinding.ActivityRebateBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class RebateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRebateBinding
    private val viewModel: RebateViewModel by viewModel()

    private var isCanClick = false
    private var now: String? = null
    private var dateStart: String? = null
    private var dateEnd: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRebateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val date = SimpleDateFormat("yyyyMMdd", Locale(getString(R.string.locale_language), getString(R.string.locale_country)))
        now = date.format(Date())
        with(binding){
            appbar.setOnBackPressed { finish() }
            btnCek.setUpToProgressButton(this@RebateActivity)
            getRebate()
            btnCek.setOnClickListener {
                getRebate()
            }
            wrapperRebate.setOnClickListener {
                if (isCanClick) {
                    val intent = Intent(this@RebateActivity, RebateDetailActivity::class.java).apply {
                        putExtra(RebateDetailActivity.KEY_DATE_START, dateStart)
                        putExtra(RebateDetailActivity.KEY_DATE_END, dateEnd)
                    }
                    startActivity(intent)
                }
            }
        }
        collectState()
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //STATE REBATE
                launch {
                    viewModel.rebateTotal.collectLatest {
                        it?.let { rebate ->
                            with(binding) {
                                txtCheckRebate.text = if (dateStart == now && dateEnd == now) getString(R.string.check_rebate_this_day)
                                    else if (dateStart == dateEnd) getString(R.string.check_rebate_1_day, parsingDate(dateEnd))
                                    else getString(R.string.check_rebate_by_date, parsingDate(dateStart), parsingDate(dateEnd))
                                tvRebate.text = rebate
                                wrapperMore.isVisible = rebate != "Rp 0"
                                isCanClick = rebate != "Rp 0"
                            }
                        }
                    }
                }
                //STATE REMAINING REBATE
                launch {
                    viewModel.remainingRebate.collectLatest {
                        it?.let { remainingRebate ->
                            with(binding) {
                                tvRemainingRebate.text = getNumberRupiah(remainingRebate.toInt())
                            }
                        }
                    }
                }
                //STATE LOADING
                launch {
                    viewModel.stateLoadingButton.collectLatest {
                        with(binding){
                            wrapperRebate.isVisible = !it
                            wrapperRemainingRebate.isVisible = !it
//                            rvProductRebate.isVisible = !it && isDetailRebateShown
                            btnCek.isEnabled = !it

                            if(it){
                                btnCek.showLoading()
                            }else{
                                btnCek.hideProgress(getString(R.string.cek))
                            }
                        }
                    }
                }
                //STATE ERROR
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@RebateActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong() + 500L)
                            viewModel.removeHeadQueue()
                        }
                    }
                }
            }
        }
    }

    private fun parsingDate(date: String?): String {
        val format = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val parsing = format.parse(date.toString())
        val newFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id"))
        return newFormat.format(parsing as Date)
    }

    private fun getRebate() {
        dateStart = binding.dateFrom.getValue()
        dateEnd = binding.dateTo.getValue()
        viewModel.getRebate(
            dateStart = this.dateStart.toString(),
            dateEnd = this.dateEnd.toString()
        )
    }
}