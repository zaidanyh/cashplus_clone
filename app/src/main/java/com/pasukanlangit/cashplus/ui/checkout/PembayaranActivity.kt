package com.pasukanlangit.cashplus.ui.checkout

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityPembayaranBinding
import com.pasukanlangit.cashplus.model.request_body.ProfileRequest
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.utils.*
import com.pasukanlangit.cashplus.utils.MyUtils.convertToNormalNumber
import com.pasukanlangit.cashplus.utils.MyUtils.getAvaImagePlaceholder
import com.pasukanlangit.cashplus.view_model.PembayaranViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.utils.*
import com.pasukanlangit.id.library_core.domain.model.NotifType
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PembayaranActivity: AppCompatActivity() {

    private lateinit var binding: ActivityPembayaranBinding
    private val viewModel: PembayaranViewModel by viewModel()
    private val sharedPrefDataSource: SharedPrefDataSource by inject()

    private lateinit var detailTrxAdapter: DetailTrxAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailTrxAdapter = DetailTrxAdapter()
        with(binding){
            appBar.setOnBackPressed { finish() }

            btnBayarPembayaran.setUpToProgressButton(this@PembayaranActivity)

            getDataIntent()

            //make input all caps
            inputDiskon.filters = arrayOf<InputFilter>(AllCaps())
            inputDiskon.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (inputDiskon.right - inputDiskon.compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                        MyUtils.showCoomingSon(supportFragmentManager)
                        //true
                    }
                }
                false
            }   
        }
    }

    private fun getDataIntent() {
        val productModel = intent.parcelable<ProductModel>(KEY_PRODUCT)
        val destinationOrigin = intent.getStringExtra(KEY_NUMBER)
        var destination = destinationOrigin
        val detailInfo = intent.getStringExtra(KEY_INFO_DETAIL)
        val operator = intent.getIntExtra(KEY_LOGO_PRODUCT, -1)

        if (productModel != null && !destinationOrigin.isNullOrEmpty()) {
            binding.btnBayarPembayaran.setOnClickListener {
                val uuid = sharedPrefDataSource.getUUID()
                val token = sharedPrefDataSource.getAccessToken()

                if(uuid!= null && token != null){
                    //karena yang dibutuhkan hanya uuid nomer telepon gak perlu diisi
                    val request = ProfileRequest(uuid)
                    viewModel.getBalance(request, token)
                }
            }

            viewModel.balance.observe(this){
                when(it.status){
                    Status.SUCCESS -> {
                        binding.btnBayarPembayaran.hideProgress(getString(R.string.pay))

                        if(it.data != null){
                            if(it.data.balance!! < convertToNormalNumber(binding.tvTotalBayar.text.toString())){
                                val notifModal = SaldoNotEnoughNotif()
                                notifModal.show(supportFragmentManager,null)
                            }else{
                                val intent = Intent(this, EnterPinActivity::class.java)
                                intent.putExtra(EnterPinActivity.KEY_PRODUCT_TRANSACTION, productModel)
                                intent.putExtra(EnterPinActivity.KEY_DEST_TRANSACTION, destinationOrigin)
                                startActivity(intent)
                            }
                        }
                    }
                    Status.LOADING -> {
                        binding.btnBayarPembayaran.showLoading()
                    }
                    Status.ERROR -> {
                        binding.btnBayarPembayaran.hideProgress(getString(R.string.pay))

                        val menusAllFragment = ButtomSheetNotif(it.message, NotifType.NOTIF_ERROR)
                        menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                    }
                }
            }

            val totalBayar : Int = productModel.price.toInt().plus(productModel.admin.toInt())

            binding.apply {
                detailInfo?.let { info ->
                    val splittedInfo = info.split("||")
                    wrapperInfoDetail.visibility = View.VISIBLE
                    tvLabelDetail.text = splittedInfo.first()
                    rvDataPln.layoutManager = LinearLayoutManager(this@PembayaranActivity)
                    rvDataPln.adapter = detailTrxAdapter
                    detailTrxAdapter.setInfoDetails(splittedInfo[1].split("|"))
                }
                tvOperatorPulsa.text = productModel.dsc
                tvBiayaAdmin.text = MyUtils.getNumberRupiah(productModel.admin.toInt())

                if(productModel.category == CategoryProduct.VOUCHER.value) {
                    destination = destination!!.split("-").first()
                }

                tvNumberPembayaran.text = destination
                tvHargaPulsaBeli.text = MyUtils.getNumberRupiah(productModel.price.toInt())
                tvTotalBayar.text = MyUtils.getNumberRupiah(totalBayar)
                if(operator != -1) logoOperator.setImageResource(operator)
                else {
                    var imgUrl = productModel.img_url
                    if(imgUrl.isEmpty()) imgUrl = getAvaImagePlaceholder(productModel.kode_provider.replace("_"," ")) //"https://ui-avatars.com/api/?name=${productModel.kode_provider.replace("_"," ")}"
                    Glide.with(this@PembayaranActivity)
                        .load(imgUrl)
                        .thumbnail(
                            Glide.with(this@PembayaranActivity)
                                .load(R.raw.image_loading_state)
                        )
                        .error(R.drawable.ic_empty)
                        .transition(DrawableTransitionOptions.withCrossFade(300))
                        .circleCrop()
                        .into(logoOperator)
                }
            }
        }
    }

    companion object {
        const val KEY_NUMBER = "KEY_NUMBER"
        const val KEY_PRODUCT = "KEY_PRODUCT"
        const val KEY_INFO_DETAIL = "KEY_INFO_DETAIL"
        const val KEY_LOGO_PRODUCT = "KEY_LOGO_PRODUCT"
    }
}