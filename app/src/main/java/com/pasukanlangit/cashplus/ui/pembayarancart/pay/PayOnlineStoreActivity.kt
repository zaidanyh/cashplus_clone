package com.pasukanlangit.cashplus.ui.pembayarancart.pay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.ActivityPayOnlineStoreBinding
import com.pasukanlangit.cashplus.model.request_body.ProfileRequest
import com.pasukanlangit.cashplus.model.response.OnlineStoreOrderResponse
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.utils.MyUtils
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.utils.parcelable
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PayOnlineStoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPayOnlineStoreBinding
    private val viewModel : PayOnlineStoreViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOnlineStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uuid = sharedPref.getUUID()
        val token = sharedPref.getAccessToken()

        val onlineStoreOrder = intent.parcelable<OnlineStoreOrderResponse>(KEY_ORDER_RESPONSE)
        val isDirectBuy = intent.getBooleanExtra(KEY_IS_DIRECT_BUY, false)

        if(!uuid.isNullOrEmpty() && !token.isNullOrEmpty()){
            viewModel.getProfile(ProfileRequest(uuid), token)
        }

        binding.btnBack.setOnClickListener { finish() }

        onlineStoreOrder?.let {
            binding.tvTotal.text = MyUtils.getNumberRupiah(it.data.orderTransaction.total)
        }

        binding.btnPay.setOnClickListener {
            if(onlineStoreOrder != null){
                with(Intent(this, EnterPinOnlineStoreActivity::class.java)){
                    putExtra(EnterPinOnlineStoreActivity.KEY_DEST_TRANSACTION, onlineStoreOrder.data.orderTransaction.orderId)
                    putExtra(EnterPinOnlineStoreActivity.KEY_PRODUCT_CODE, ProductCode.TOKO_ONLINE.value)
                    putExtra(EnterPinOnlineStoreActivity.KEY_IS_DIRECT_BUY, isDirectBuy)
                    startActivity(this)
                }
            }
        }

        observeProfile()
    }

    private fun observeProfile() {
        viewModel.user.observe(this) {
            binding.btnPay.isEnabled = it.status == Status.SUCCESS
            binding.tvSaldo.isVisible = it.status == Status.SUCCESS

            when (it.status) {
                Status.SUCCESS -> {
                    binding.tvSaldo.text = MyUtils.getNumberRupiah(it.data?.balance ?: 0.0)
                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    val menusAllFragment = ButtomSheetNotif(
                        it.message,
                        com.pasukanlangit.id.library_core.domain.model.NotifType.NOTIF_ERROR
                    )
                    menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                }
            }
        }
    }

    companion object {
        const val KEY_ORDER_RESPONSE = "KEY_ORDER_RESPONSE"
        const val KEY_IS_DIRECT_BUY = "KEY_IS_DIRECT_BUY"
    }
}