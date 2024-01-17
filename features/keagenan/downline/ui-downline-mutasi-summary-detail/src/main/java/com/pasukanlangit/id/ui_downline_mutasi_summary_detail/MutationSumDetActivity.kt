package com.pasukanlangit.id.ui_downline_mutasi_summary_detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.MUTASI_SUMMARY_DETAIL_DOWNLINE_KEY_IS_FROM_SUBDOWNLINE
import com.pasukanlangit.id.core.MUTASI_SUMMARY_DETAIL_DOWNLINE_KEY_NAME
import com.pasukanlangit.id.core.MUTASI_SUMMARY_DETAIL_DOWNLINE_KEY_PHONE_NUMBER
import com.pasukanlangit.id.core.extensions.setOnlyNumberAllowed
import com.pasukanlangit.id.core.presentation.MutationSumDetPageType
import com.pasukanlangit.id.core.utils.getEnumExtra
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.ui_downline_mutasi_summary_detail.databinding.ActivityMutasiSumDetBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MutationSumDetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMutasiSumDetBinding
    private val viewModel: MutationSumDetViewModel by viewModel()

    private var destination: String? = null
    private var isFromSubDownLine: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMutasiSumDetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            edtInputDest.setOnlyNumberAllowed()
            destination = intent.getStringExtra(MUTASI_SUMMARY_DETAIL_DOWNLINE_KEY_PHONE_NUMBER)
            destination?.let { phoneNumber ->
                viewModel.onTriggerEvent(MutasiSumDetEvent.SetPhoneNumberDownLine(phoneNumberDownLine = phoneNumber))
            }
            intent.getEnumExtra<MutationSumDetPageType>()?.let { pageType ->
                viewModel.onTriggerEvent(MutasiSumDetEvent.SetPageType(mutationSumDetPageType = pageType))
            }

            intent.getBooleanExtra(MUTASI_SUMMARY_DETAIL_DOWNLINE_KEY_IS_FROM_SUBDOWNLINE, false).let { value ->
                isFromSubDownLine = value
                viewModel.onTriggerEvent(MutasiSumDetEvent.SetPageFrom(
                    isFromSubDownLine = value
                ))
            }
            
            val nameIntent = intent.getStringExtra(MUTASI_SUMMARY_DETAIL_DOWNLINE_KEY_NAME)

            edtInputDest.isFocusable = destination == null
            edtInputDest.isEnabled = destination == null
            edtInputDest.setText(destination)

            tvDestName.isVisible = nameIntent != null
            tvDestName.text = getString(R.string.nama_akun_destination, nameIntent)

            appbar.setOnBackPressed { finish() }

            buttonSend.setUpToProgressButton(this@MutationSumDetActivity)
            buttonSend.setOnClickListener {
                getCurrentDate()
                submitAction()
            }
        }

        setUpRv()
        collectState()
    }

    private fun setUpRv() {
        binding.rvResult.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        getCurrentDate()
        submitAction()
    }

    private fun getCurrentDate() {
        viewModel.onTriggerEvent(MutasiSumDetEvent.SetDate(
            dateStart = binding.dateFrom.getValue(),
            dateEnd = binding.dateTo.getValue()
        ))
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                //STATE BALANCE SUCCESS MESSAGE
                launch {
                    viewModel.balanceMutation.collectLatest {
                        it?.let { balanceMutation ->
                            binding.emptyData.isVisible = balanceMutation.isEmpty()
                            binding.rvResult.adapter = MutasiDownlineAdapter(balanceMutation)
                        }
                    }
                }

                //STATE DETAIL TRANSFER SUCCESS MESSAGE
                launch {
                    viewModel.detailTransfer.collectLatest {
                        it?.let { detailTransfer ->
                            binding.emptyData.isVisible = detailTransfer.isEmpty()
                            binding.rvResult.adapter = DetailTransferAdapter(detailTransfer)
                        }
                    }
                }

                //STATE SUMMARY TRANSFER SUCCESS MESSAGE
                launch {
                    viewModel.summaryTransfer.collectLatest {
                        it?.let { summaryTransfer ->
                            binding.emptyData.isVisible = summaryTransfer.isEmpty()
                            binding.rvResult.adapter = SummaryTransferAdapter(summaryTransfer)
                        }
                    }
                }

                //STATE LOADING BUTTON
                launch {
                    viewModel.isLoadingButton.collectLatest {
                        binding.buttonSend.isEnabled = !it

                        if(it){
                            binding.buttonSend.showLoading()
                            //hide empty data when loading
                            binding.emptyData.isVisible = false
                        }else{
                            binding.buttonSend.hideProgress(getString(R.string.send))
                        }
                    }
                }

                //STATE PAGE
                launch {
                    viewModel.pageType.collectLatest {
                        binding.appbar.setTitle(
                            getString(
                                when(it){
                                    MutationSumDetPageType.CEK_MUTATION -> {
                                        if(isFromSubDownLine){
                                            R.string.cek_mutation_subdownline
                                        }else{
                                            R.string.cek_mutation_downline
                                        }
                                    }
                                    MutationSumDetPageType.CEK_DETAIL_TRANSFER -> R.string.cek_detail_transfer
                                    MutationSumDetPageType.CEK_SUMMARY_TRANSFER -> R.string.cek_sumary_transfer
                                }
                            )
                        )

                    }
                }

                //STATE ERROR
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(this@MutationSumDetActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong() + 500L)
                            viewModel.onTriggerEvent(MutasiSumDetEvent.RemoveHeadMessageQueue)
                        }
                    }
                }
            }
        }
    }

    private fun submitAction() {
        viewModel.onTriggerEvent(MutasiSumDetEvent.OnSubmit)
    }
}
