package com.pasukanlangit.cashplus.ui.checkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.pasukanlangit.cashplus.databinding.ActivityEnterPinPayBinding
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.ui.pages.others.settings.pin.ChangePinActivity
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.cashplus.utils.MyUtils.goToMainAndSendMessage
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.utils.parcelable
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class EnterPinPayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnterPinPayBinding
    private val viewModel: TransactionPayViewModel by viewModel()
    private val sharedPrefDataSource: SharedPrefDataSource by inject()

    private var productModel: ProductModel? = null
    private var destination: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterPinPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            btnBack.setOnClickListener { finish() }
            tvResetPin.setOnClickListener {
                val intent = Intent(this@EnterPinPayActivity, ChangePinActivity::class.java)
                startActivity(intent)
            }

            // auto open keyboard when activity create
            KeyboardUtil.openSoftKeyboard(this@EnterPinPayActivity, inputOtpTransaction)

            productModel = intent.parcelable(EnterPinActivity.KEY_PRODUCT_TRANSACTION)
            destination = intent.getStringExtra(EnterPinActivity.KEY_DEST_TRANSACTION)
            val uuid = sharedPrefDataSource.getUUID()

            viewModel.responseTransaction.observe(this@EnterPinPayActivity) {
                it.getContentIfNotHandled()?.let { response ->
                    tvError.isVisible = response.status == Status.ERROR
                    when(response.status){
                        Status.SUCCESS -> {
                            loadingEnterPin.visibility = View.INVISIBLE

                            response.data?.let { trx ->
                                finishAffinity()


//                            showAlarmNotification(
//                                this@EnterPinPayActivity,
//                                 getString(R.string.transaction_success_notif),//"Transaksi anda telah berhasil",
//                                 getString(R.string.transaction_success_with_prefix, trx.shortDsc), //"${trx.shortDsc} berhasil",
//                                Math.random().toInt()
//                            )
                                with(Intent(this@EnterPinPayActivity, TagihanResultActivity::class.java)){
                                    putExtra(TagihanResultActivity.KEY_TRANSACTION_RESPONSE, trx)
                                    startActivity(this)
                                }
                            }
                        }
                        Status.LOADING -> {
                            loadingEnterPin.visibility = View.VISIBLE
                        }
                        Status.ERROR -> {
                            loadingEnterPin.visibility = View.INVISIBLE
                            inputOtpTransaction.text = null

                            response.data?.let { trx ->
                                if(trx.rc == "68"){
//                                showAlarmNotification(
//                                    this@EnterPinPayActivity,
//                                    getString(R.string.transaction_pending), //"Transaksi anda pending",
//                                    getString(R.string.transaction_pending_with_prefix, trx.shortDsc), //"${trx.shortDsc} pending",
//                                    Math.random().toInt()
//                                )
                                    goToMainAndSendMessage(this@EnterPinPayActivity,trx.billData?.info?.replace("|","\n") ?: "Transaksi sedang pending, cek history untuk info detailnya",  com.pasukanlangit.id.library_core.domain.model.NotifType.NOTIF_PENDING_OR_PROCESS)
                                    return@observe
                                }

//                            showAlarmNotification(
//                                this@EnterPinPayActivity,
//                                getString(R.string.transaction_failed), //"Transaksi Gagal",
//                                getString(R.string.transaction_failed_with_prefix, trx.shortDsc), //"${trx.shortDsc} Gagal",
//                                Math.random().toInt()
//                            )
//                            val menusAllFragment = ButtomSheetNotif(if(trx.billData?.info.isNullOrEmpty()) trx.msg else trx.billData?.info?.replace("|","\n") , com.pasukanlangit.id.library_core.domain.model.NotifType.NOTIF_ERROR)
//                            menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                                tvError.text = if(trx.billData?.info.isNullOrEmpty()) trx.msg else trx.billData?.info?.replace("|","\n")
                            }
                        }
                    }
                }
            }

            inputOtpTransaction.setOtpCompletionListener {
                val token = sharedPrefDataSource.getAccessToken()

                if(productModel != null && destination != null && uuid != null && token != null){
                    val transactionRequest = TransactionRequest(
                        uuid = uuid,
                        kode_produk = productModel!!.kode_produk,
                        dest = destination!!, pin = it
                    )

                    viewModel.sendTransaction(transactionRequest, token)
                }
            }
        }
    }


    companion object {
        const val KEY_PRODUCT_TRANSACTION = "KEY_PRODUCT_TRANSACTION"
        const val KEY_DEST_TRANSACTION = "KEY_DEST_TRANSACTION"
    }
}