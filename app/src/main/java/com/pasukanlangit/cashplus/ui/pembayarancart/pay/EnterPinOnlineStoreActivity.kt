package com.pasukanlangit.cashplus.ui.pembayarancart.pay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.pasukanlangit.cashplus.databinding.ActivityEnterPinPayBinding
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.checkout.TagihanResultActivity
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


enum class ProductCode(val value: String, val showBtnDetailFirst: Boolean = false){
    TOKO_ONLINE("TOORDER", true),
    KERETA("KERETA", true),
    KAPAL("KAPAL", true),
    PESAWAT("PESAWAT", true),
    HOTEL("HOTEL",true)
}

class EnterPinOnlineStoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterPinPayBinding
    private val viewModel: EnterPinOnlineStoreViewModel by viewModel()
    private val sharedPrefDataSource: SharedPrefDataSource by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterPinPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // auto open keyboard when activity create
        KeyboardUtil.openSoftKeyboard(this, binding.inputOtpTransaction)

        val destination = intent.getStringExtra(KEY_DEST_TRANSACTION)
        //is direct buy is flag when buy product without cart, so no need clear data cart in database
        val isDirectBuy = intent.getBooleanExtra(KEY_IS_DIRECT_BUY, false)
        val productCode = intent.getStringExtra(KEY_PRODUCT_CODE)

        val token = sharedPrefDataSource.getAccessToken()
        val uuid = sharedPrefDataSource.getUUID()

        viewModel.responseTransaction.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.loadingEnterPin.visibility = View.INVISIBLE

                    it.data?.let { trx ->
                        finishAffinity()

                        if (!isDirectBuy && productCode == ProductCode.TOKO_ONLINE.value) {
                            viewModel.deleteCartSelectedFromDB(
                                sharedPrefDataSource.getPhoneNumber() ?: "-1"
                            )
                        }

//                        showAlarmNotification(
//                            this,
//                            "Transaksi anda telah berhasil",
//                            "${trx.shortDsc} berhasil",
//                            Math.random().toInt()
//                        )
                        with(Intent(this, TagihanResultActivity::class.java)) {
                            putExtra(TagihanResultActivity.KEY_TRANSACTION_RESPONSE, trx)
                            startActivity(this)
                        }
                    }
                }
                Status.LOADING -> {
                    binding.loadingEnterPin.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.loadingEnterPin.visibility = View.INVISIBLE
                    binding.inputOtpTransaction.text = null

                    it.data?.let { trx ->
                        val menusAllFragment = ButtomSheetNotif(
                            if (!trx.rc_msg.isNullOrEmpty()) trx.rc_msg else trx.msg,
                            com.pasukanlangit.id.library_core.domain.model.NotifType.NOTIF_ERROR
                        )
                        menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                    }
                }
            }
        }

        binding.inputOtpTransaction.setOtpCompletionListener {
            if(destination != null && uuid != null && token != null){
                val transactionRequest = TransactionRequest(
                    uuid = uuid, kode_produk = productCode ?: "",
                    dest = destination, pin = it
                )

                viewModel.sendTransaction(transactionRequest, token)
            }
        }
    }

    companion object {
        const val KEY_IS_DIRECT_BUY = "KEY_IS_DIRECT_BUY"
        const val KEY_DEST_TRANSACTION = "KEY_DEST_TRANSACTION"
        const val KEY_PRODUCT_CODE = "KEY_PRODUCT_CODE"
    }
}