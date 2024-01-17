package com.pasukanlangit.id.ui_cashgold_withdraw.home

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.KYC_INIT_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.GridSpacingItemDecoration
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.ui_cashgold_withdraw.R
import com.pasukanlangit.id.ui_cashgold_withdraw.databinding.ActivityCashGoldWdactivityBinding
import com.pasukanlangit.id.ui_cashgold_withdraw.tag.TagWithDrawActivity
import com.pasukanlangit.id.ui_core.components.GenericCashGoldModalDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.StringBuilder

class CashGoldWDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCashGoldWdactivityBinding
    private lateinit var providerAdapter: WDProviderAdapter
    private val viewModel: CashGoldWDViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashGoldWdactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appbar.setIconBackPressed {
            finish()
        }

        binding.btnAdd.setUpToProgressButton(this)
        binding.btnAdd.setOnClickListener {
            viewModel.onTriggerEvent(CashGoldWDEvent.CheckKyc)
        }

        initRecyclerView()
        collectState()
    }

    private fun initRecyclerView() {
        with(binding){
            rvProvider.addItemDecoration(GridSpacingItemDecoration(10))
            rvProductDetail.layoutManager = LinearLayoutManager(this@CashGoldWDActivity)
        }
    }

    private fun collectState() {
        collectGoldBalance()
        collectStateLoading()
        collectStateError()
        collectWithDrawProduct()
        collectUserBalance()
        collectStateKyc()
    }

    private fun collectStateKyc() {
        lifecycleScope.launch {
            viewModel.kycStatus.collectLatest {
                it?.let { status ->
                    if(!status.isIdentityCardUploaded || !status.isIdentityWithSelfieUploaded || !status.isTaxIdUploaded){
                        showModalDialog("Silahkan lengkapi data KYC untuk dapat menggunakan semua fitur cashgold")
                    }else if(status.isRejected){
                        showModalDialog("Data kyc kamu ditolak, silahkan melakukan verifikasi ulang")
                    }else if(!status.isApproved){
                        GenericCashGoldModalDialog.Builder()
                            .title("Peringatan")
                            .description("KYC Masih dalam proses verifikasi, tunggu beberapa saat ya")
                            .image(R.drawable.ilustration_warning)
                            .show(supportFragmentManager)
                    }
                    else{
                        navigateToDetailWithDraw()
                    }
                }
            }
        }
    }

    private fun showModalDialog(desc: String) {
        GenericCashGoldModalDialog.Builder()
            .title("Peringatan")
            .description(desc)
            .image(R.drawable.ilustration_warning)
            .buttonPositive(
                PositiveButtonAction(
                    btnLabel = "KYC Upload",
                    onBtnClicked = {
                        startActivity(ModuleRoute.internalIntent(this@CashGoldWDActivity, KYC_INIT_PATH))
                    }
                )
            )
            .buttonNegative(
                NegativeButtonAction(
                    btnLabel = "Nanti Saja",
                    setClickOnDismiss = true
                )
            )
            .show(supportFragmentManager)
    }

    private fun navigateToDetailWithDraw() {
        if(::providerAdapter.isInitialized){
            val productChosen = providerAdapter.getProductSelected()
            if(productChosen != null){
                startActivity(
                    Intent(this@CashGoldWDActivity, TagWithDrawActivity::class.java)
                        .putExtra(TagWithDrawActivity.KEY_PRODUCT_SELECTED, productChosen)
                )
            }else{
                Toast.makeText(this@CashGoldWDActivity, "Please choose product first", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this@CashGoldWDActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun collectWithDrawProduct() {
        lifecycleScope.launch {
            viewModel.withDrawResponse.collectLatest {
                it?.let { products ->
                    //reset first product, when fetch new product
                    binding.rvProductDetail.adapter = null

                    binding.bottomWrapper.slideVisibility(false)

                    providerAdapter = WDProviderAdapter(
                        providers = products.provider.toMutableList()
                    ){
                        binding.rvProductDetail.adapter = WDProductAdapter(it.toMutableList()){
                            binding.bottomWrapper.slideVisibility(it != null)

                            it?.let { productSelected ->
                                val priceTotal = (productSelected.feeRaw * productSelected.inputQty).toInt()
                                val dominationTotal = productSelected.denominationRaw.times(productSelected.inputQty)

                                binding.labelBiaya.text = getString(R.string.label_qty, productSelected.denominationGram, productSelected.inputQty.toString())
                                binding.tvPrice.text = getNumberRupiah(priceTotal)

                                checkError(priceTotal, dominationTotal)
                            }
                        }
                    }
                    binding.rvProvider.adapter = providerAdapter
                }
            }
        }
    }

    private fun checkError(priceTotal: Int, dominationTotal: Double) {
        val error = StringBuilder()
        val userCurrentBalance = viewModel.stateUserBalanceRaw.value
        val userCurrentGold = viewModel.stateGoldBalanceRaw.value
        if(userCurrentBalance != null){
            if(userCurrentBalance < priceTotal) error.append("Deposit anda tidak mencukupi.")
        }

        if(userCurrentGold != null){
            if(userCurrentGold < dominationTotal) error.append(" Jumlah Emas anda tidak mencukupi.")
        }

        binding.tvError.isVisible = error.trim().isNotEmpty()
        binding.btnAdd.isEnabled = error.trim().isEmpty()
        binding.tvError.text = error.trim()

    }

    fun View.slideVisibility(visibility: Boolean, durationTime: Long = 300) {
        val transition = Slide(Gravity.BOTTOM)
        transition.apply {
            duration = durationTime
            addTarget(this@slideVisibility)
        }
        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        this.isVisible = visibility
    }

    private fun collectUserBalance() {
        lifecycleScope.launch {
            viewModel.stateUserBalance.collectLatest {
                it?.let { userBalance ->
                    binding.tvSaldo.text = userBalance
                }
            }
        }
    }

    private fun collectGoldBalance() {
        lifecycleScope.launch {
            viewModel.stateGoldBalance.collectLatest { goldBalance ->
                goldBalance?.let { balance ->
                    binding.tvSaldoGold.text = balance
                }
            }
        }
    }

    private fun collectStateError() {
        lifecycleScope.launch {
            viewModel.stateError.collectLatest { message ->
                message.peek()?.let { info ->
                    val toast = Toast.makeText(this@CashGoldWDActivity, info, Toast.LENGTH_SHORT)
                    toast.show()
                    delay(toast.duration.toLong() + 500L)
                    viewModel.onTriggerEvent(CashGoldWDEvent.RemoveHeadQueue)
                }
            }
        }
    }

    private fun collectStateLoading() {
        lifecycleScope.launch {
            viewModel.stateLoading.collectLatest { isLoading ->
                binding.loading.isVisible = isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.stateLoadingButton.collectLatest { isLoading ->
                binding.btnAdd.isEnabled = !isLoading

                if(isLoading){
                    binding.btnAdd.showLoading()
                }else{
                    binding.btnAdd.hideProgress(getString(R.string.tinjau))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(CashGoldWDEvent.GetGoldBalance)
        viewModel.onTriggerEvent(CashGoldWDEvent.GetWithDrawProduct)
        viewModel.onTriggerEvent(CashGoldWDEvent.GetSt24Balance)

    }
}