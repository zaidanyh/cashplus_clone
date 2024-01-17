package com.pasukanlangit.cashplus.ui.checkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.databinding.ActivityPembayaranTagihanBinding
import com.pasukanlangit.cashplus.model.request_body.ProfileRequest
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.model.response.TransactionTAGResponse
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.utils.SaldoNotEnoughNotif
import com.pasukanlangit.id.core.utils.parcelable
import com.pasukanlangit.id.library_core.domain.model.NotifType
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PembayaranTagihanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPembayaranTagihanBinding
    private val viewModel: PembayaranTagihanViewModel by viewModel()
    private val sharedPrefDataSource : SharedPrefDataSource by inject()

    private lateinit var adapter: DetailTrxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranTagihanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = DetailTrxAdapter()
        with(binding){
            appBar.setOnBackPressed { finish() }

            val transactionInfo: TransactionTAGResponse? = intent.parcelable(
                KEY_TRANSACTION_INFO)
            val destination : String? = intent.getStringExtra(KEY_DESTINATION)
            val imgProduct : Int = intent.getIntExtra(KEY_IMG_PRODUCT, -1)
            val imgUrl : String? = intent.getStringExtra(KEY_IMG_PRODUCT_URL)

            if(transactionInfo != null && destination != null ){
                tvNumberTagihan.text = destination

                if(imgProduct != -1 ) logoTagihan.setImageResource(imgProduct)
                if(imgUrl != null && imgUrl.isNotEmpty()) Glide.with(this@PembayaranTagihanActivity).load(imgUrl).into(logoTagihan)
                val showInfoDetail = transactionInfo.billData.originInfo ?: transactionInfo.billData.info
                val splittedInfo = showInfoDetail.split("||")
                tvInfo.text = splittedInfo.first()

                rvDetailTrx.layoutManager = LinearLayoutManager(this@PembayaranTagihanActivity)
                rvDetailTrx.adapter = adapter
                adapter.setInfoDetails(splittedInfo[1].split("|"))

                btnBayarPembayaran.setOnClickListener {
                    val uuid = sharedPrefDataSource.getUUID()
                    val token = sharedPrefDataSource.getAccessToken()

                    if(uuid!= null && token != null){
                        //karena yang dibutuhkan hanya uuid nomer telepon gak perlu diisi
                        val request = ProfileRequest(uuid)
                        viewModel.getBalance(request, token)
                    }
                }

                viewModel.balance.observe(this@PembayaranTagihanActivity){
                    when(it.status){
                        Status.SUCCESS -> {
                            loadingCheckout.isVisible = false

                            if(it.data != null){
                                if(it.data.balance!! < transactionInfo.billData.total.toDouble()){
                                    val notifModal = SaldoNotEnoughNotif()
                                    notifModal.show(supportFragmentManager,null)
                                }else{
                                    val productModel = ProductModel(
                                        dsc = transactionInfo.msg,
                                        kode_provider = "",
                                        kode_produk = transactionInfo.billData.payCode?.replaceRange(0,3,"PAY") ?: "",
                                        short_dsc = transactionInfo.billData.info, admin = transactionInfo.billData.admin,
                                        price = transactionInfo.billData.total, outlet_price = "",
                                        category = "", img_url = "", img_url_2 = "", opr_name = "", product_type = ""
                                    )
                                    val intent = Intent(this@PembayaranTagihanActivity, EnterPinPayActivity::class.java)
                                    intent.putExtra(EnterPinPayActivity.KEY_PRODUCT_TRANSACTION, productModel)
                                    intent.putExtra(EnterPinPayActivity.KEY_DEST_TRANSACTION, destination)
                                    startActivity(intent)
                                }
                            }
                        }
                        Status.LOADING -> {
                            loadingCheckout.isVisible = true
                        }
                        Status.ERROR -> {
                            loadingCheckout.isVisible = false
                            val menusAllFragment = ButtomSheetNotif(it.message, NotifType.NOTIF_ERROR)
                            menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val KEY_DESTINATION = "KEY_DESTINATION"
        const val KEY_TRANSACTION_INFO = "KEY_TRANSACTION_INFO"
        const val KEY_IMG_PRODUCT = "KEY_IMG_PRODUCT"
        const val KEY_IMG_PRODUCT_URL = "KEY_IMG_PRODUCT_URL"
    }
}