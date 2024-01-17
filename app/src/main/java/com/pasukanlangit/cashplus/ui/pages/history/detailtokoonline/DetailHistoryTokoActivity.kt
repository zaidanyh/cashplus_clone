package com.pasukanlangit.cashplus.ui.pages.history.detailtokoonline

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.ActivityDetailHistoryTokoBinding
import com.pasukanlangit.cashplus.model.request_body.OrderCheckTokoRequest
import com.pasukanlangit.cashplus.model.response.OrderTransactionToko
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.cashplus.ui.pembayarancart.PembayaranCartAdapter
import com.pasukanlangit.cashplus.utils.MyUtils
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.utils.CoreUtils.copyClipboard
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class DetailHistoryTokoActivity : AppCompatActivity() {

    private lateinit var orderDetailReqeust: OrderCheckTokoRequest
    private var trxId: String?=null
    private var token: String?= null
    private var uuid: String?= null

    private lateinit var binding: ActivityDetailHistoryTokoBinding
    private val viewModel: DetailHistoryTokoViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryTokoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        trxId = intent.getStringExtra(KEY_TRX_ID)
        uuid = sharedPref.getUUID()
        token = sharedPref.getAccessToken()

        binding.appBar.setOnClickListener { finish() }

        trxId?.let { id ->
            if(uuid != null && token != null){
                orderDetailReqeust = OrderCheckTokoRequest(id,uuid!!, "")
                viewModel.getDetail(orderDetailReqeust, token!!)
                viewModel.tracking(orderDetailReqeust, token!!)
            }
        }

        setUpRvProduct()
        observeDetail()
        observeTracking()
    }

    private fun observeTracking() {
        viewModel.tracking.observe(this) {
            with(binding) {
                loading.isVisible = it.status == Status.LOADING
                wrapperTracking.isVisible = it.status == Status.SUCCESS

                when (it.status) {
                    Status.SUCCESS -> {
//                    it?.data?.data?.let { dataDetail ->
//                        rv_product.adapter = PembayaranCartAdapter(dataDetail.orderDetail)
//                        setUpUI(dataDetail.orderTransaction)
//                    }
                        if (it.data?.data != null) {
                            it?.data.data.let { data ->
                                val courierAndResi =
                                    "${data.summary.serviceCode} ${data.summary.courierName} - ${data.summary.waybillNumber}"
                                tvCourierAndResi.text = courierAndResi
                                tvStatusTrack.text =
                                    "${data.deliveryStatus.status} ${data.deliveryStatus.podReceiver}"
                                if (data.deliveryStatus.podDate.isEmpty() && data.deliveryStatus.podTime.isEmpty()) {
                                    tvTimeTrack.isVisible = false
                                } else {
                                    tvTimeTrack.text =
                                        "${data.deliveryStatus.podDate} ${data.deliveryStatus.podTime}"
                                }

                                btnLihatTracking.setOnClickListener {
                                    Intent(this@DetailHistoryTokoActivity, TrackingTokoActivity::class.java).apply {
                                        putParcelableArrayListExtra(
                                            TrackingTokoActivity.KEY_MANIFEST,
                                            ArrayList(data.manifest)
                                        )
                                        startActivity(this)
                                    }
                                }
                            }
                        } else {
                            wrapperTracking.isVisible = false
                        }

                    }
                    Status.ERROR -> {
                        val menusAllFragment = ButtomSheetNotif(it.message, NotifType.NOTIF_ERROR)
                        menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private fun setUpRvProduct() {
        binding.rvProduct.layoutManager = LinearLayoutManager(this)
    }

    private fun observeDetail() {
        viewModel.detail.observe(this){
            binding.loading.isVisible = it.status == Status.LOADING
            when (it.status) {
                Status.SUCCESS -> {
                    it?.data?.data?.let { dataDetail ->
                        binding.rvProduct.adapter = PembayaranCartAdapter(dataDetail.orderDetail)
                        setUpUI(dataDetail.orderTransaction)
                    }

                }
                Status.ERROR -> {
                    val menusAllFragment = ButtomSheetNotif(it.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                }
                else -> {}
            }
        }
    }

    private fun setUpUI(orderTransaction: OrderTransactionToko) {
        with(binding) {
            tvStatus.text = orderTransaction.orderStatus
            tvAddress.text = orderTransaction.shippingAddress
            tvPrice.text = MyUtils.getNumberRupiah(orderTransaction.totalOrder)
            tvEkspedisi.text = MyUtils.getNumberRupiah(orderTransaction.courierFee)
            tvDiscount.text = MyUtils.getNumberRupiah(orderTransaction.totalDiscount)
            tvTotal.text = MyUtils.getNumberRupiah(orderTransaction.total)

            tvDate.text = getDateTimeFormatted(orderTransaction.orderDate)
            wrapperResi.isVisible = orderTransaction.noResi != "0"
            tvCourier.text = orderTransaction.courierService
            tvResi.text = orderTransaction.noResi
            btnCopyResi.setOnClickListener {
                copyClipboard(
                    this@DetailHistoryTokoActivity,
                    orderTransaction.noResi
                )
            }

            val regexTrxDone = """(?i)\b(ondelivery)\b""".toRegex()

            btnTrxDone.setUpToProgressButton(this@DetailHistoryTokoActivity)
            btnTrxDone.isVisible = regexTrxDone.containsMatchIn(orderTransaction.orderStatus)
            btnTrxDone.setOnClickListener {
                if (uuid != null && token != null) {
                    viewModel.setTrxDone(
                        OrderCheckTokoRequest(
                            "",
                            uuid!!,
                            orderTransaction.orderId
                        ), token!!
                    )
                }
            }
        }
        observeTrxDone()
    }

    private fun observeTrxDone() {
        viewModel.trxDone.observe(this){
            when (it.status) {
                Status.SUCCESS -> {
                    binding.btnTrxDone.hideProgress("Barang Sudah Diterima")

                    it.data?.getContentIfNotHandled()?.let { data ->
                        ButtomSheetNotif(data.message ?: data.msg ?: "Sukses", NotifType.NOTIF_SUCCESS).show(supportFragmentManager,null)

                        if(::orderDetailReqeust.isInitialized && token != null){
                            viewModel.getDetail(orderDetailReqeust, token!!)
                        }
                    }
                }
                Status.LOADING -> {
                    binding.btnTrxDone.showLoading()
                }
                Status.ERROR -> {
                    binding.btnTrxDone.hideProgress("Barang Sudah Diterima")
                    val menusAllFragment = ButtomSheetNotif(it.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                }
            }
        }
    }

    private fun getDateTimeFormatted(orderDate: String): String {
        val dateSplitted = orderDate.split(" ")
        val dateFormatted = SimpleDateFormat("MM/dd/yyyy").parse(dateSplitted[0]) as Date
        val fullDateFormat =
            SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        return fullDateFormat.format(dateFormatted) + "   " + dateSplitted[1] + dateSplitted[2]
    }

    companion object {
        const val KEY_TRX_ID = "KEY_TRX_ID"
    }
}