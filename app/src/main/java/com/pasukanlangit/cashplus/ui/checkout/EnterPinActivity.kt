package com.pasukanlangit.cashplus.ui.checkout

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityEnterPinBinding
import com.pasukanlangit.cashplus.model.request_body.TransactionRequest
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.ui.pages.others.settings.pin.ChangePinActivity
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.cashplus.utils.MyUtils.goToMainAndSendMessage
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.cashplus.view_model.TransactionViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.library_core.domain.model.NotifType
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class EnterPinActivity : AppCompatActivity(), ButtomSheetNotif.BottomSheetEvent {

    private val transactionViewModel: TransactionViewModel by viewModel()
    private lateinit var trxId : String
    private lateinit var lastPinEntered : String
    private var productModel: ProductModel ?=null
    private var destination: String ?=null
    private val sharedPref: SharedPrefDataSource by inject()

    private lateinit var binding: ActivityEnterPinBinding

    override fun onResume() {
        super.onResume()
        // auto open keyboard when activity create
        KeyboardUtil.openSoftKeyboard(this@EnterPinActivity,  binding.inputOtpTransaction)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            appBar.setOnBackPressed { finish() }
            tvResetPin.setOnClickListener {
                val intent = Intent(this@EnterPinActivity, ChangePinActivity::class.java)
                startActivity(intent)
            }

            productModel = intent.parcelable(KEY_PRODUCT_TRANSACTION)
            destination = intent.getStringExtra(KEY_DEST_TRANSACTION)

            val uuid = sharedPref.getUUID()
            val accessToken = sharedPref.getAccessToken()

            transactionViewModel.responseTransaction.observe(this@EnterPinActivity){ response ->
                response.getContentIfNotHandled()?.let {
                    tvError.isVisible = it.status == Status.ERROR
                    when(it.status){
                        Status.SUCCESS -> {
                            loadingEnterPin.visibility = View.INVISIBLE

                            it.data?.let { trx ->
                                trx.rcMsg?.let { rcMsg ->
                                    if(rcMsg.contains("SUDAH ADA DENGAN STATUS")){
                                        trxId = trx.trxId ?: ""
                                        val menusAllFragment = ButtomSheetNotif(getString(R.string.new_transaction_confirmation), NotifType.NOTIF_TRX_HAS_BEEN_COMPLETED,this@EnterPinActivity)
                                        menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                                        return@observe
                                    }
                                }

                                val message = "Pembelian ${it.data.desc?.lowercase()} ke ${it.data.dest?.replace(Regex("\\D"), "")} berhasil, silahkan cek di halaman riwayat untuk lebih detail"

                                if(it.data.kode_produk?.contains("PLNFULL", true) == true){
                                    val token = trx.sn?.split("/")?.first()
                                    goToMainAndSendMessage(this@EnterPinActivity, message, NotifType.NOTIF_TRX_SUCCESS, additionalToken = token)
                                }else {
                                    goToMainAndSendMessage(this@EnterPinActivity, message, NotifType.NOTIF_TRX_SUCCESS)
                                }
                            }
                        }
                        Status.LOADING -> {
                            loadingEnterPin.visibility = View.VISIBLE
                        }
                        Status.ERROR -> {
                            loadingEnterPin.visibility = View.INVISIBLE
                            inputOtpTransaction.text = null

                            it.data?.let { trx ->
                                var status = "gagal"
                                if(trx.rc == "68") status = "pending"
                                val message = "Pembelian ${it.data.desc?.lowercase(Locale.ROOT)} ke ${it.data.dest?.replace(Regex("[^0-9]"), "")} $status. ${
                                    it.data.rcMsg?.lowercase(
                                        Locale.ROOT
                                    )
                                }"
                                trx.rcMsg?.let { rcMessage ->
                                    if(rcMessage.contains("SUDAH ADA DENGAN STATUS")){
                                        trxId = trx.trxId ?: ""
                                        val menusAllFragment = ButtomSheetNotif(getString(R.string.new_transaction_confirmation), NotifType.NOTIF_TRX_HAS_BEEN_COMPLETED,this@EnterPinActivity)
                                        menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                                        return@observe
                                    }
                                }
                                if(trx.rc == "68") {
                                    goToMainAndSendMessage(this@EnterPinActivity,trx.msg ?: "",  NotifType.NOTIF_PENDING_OR_PROCESS)
                                    return@observe
                                }

                                tvError.text = message
                            }
                        }
                    }
                }
            }

            inputOtpTransaction.setOtpCompletionListener {
                lastPinEntered = it

                if(productModel != null && destination != null && uuid != null && accessToken != null){
                    val transactionRequest = TransactionRequest(uuid,productModel!!.kode_produk,destination!!, it)

                    transactionViewModel.sendTransaction(transactionRequest, accessToken)
                }
            }
        }
    }

    override fun onRepeatOrder() {
        val uuid = sharedPref.getUUID()
        val token = sharedPref.getAccessToken()
        val phoneNumber = sharedPref.getPhoneNumber()
        val lastFourPhoneNumber = phoneNumber?.substring(phoneNumber.length.minus(5), phoneNumber.length.minus(1))
        val currentDateTime = getCurrentDateTime().replace(".", "")

       if(::trxId.isInitialized && ::lastPinEntered.isInitialized && productModel != null && destination != null && uuid != null && token != null){
           //TODO : try repeat order
           var nextTrxId = "$lastFourPhoneNumber$currentDateTime"
           if(trxId.isNotEmpty()) nextTrxId = trxId.toInt().inc().toString()

           val transactionRequest = TransactionRequest(
               uuid = uuid, kode_produk = productModel!!.kode_produk, dest = destination!!,
               pin = lastPinEntered, reqId = nextTrxId)
           transactionViewModel.sendTransaction(transactionRequest, token)
       }
    }

    override fun onCancelOrder() {
        Toast.makeText(this,getString(R.string.check_history_message), Toast.LENGTH_LONG).show()
        finish()
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd-HH-mm")
        return sdf.format(Date())
    }

    companion object {
        const val KEY_PRODUCT_TRANSACTION = "KEY_PRODUCT_TRANSACTION"
        const val KEY_DEST_TRANSACTION = "KEY_DEST_TRANSACTION"
    }
}