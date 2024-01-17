package com.pasukanlangit.cashplus.ui.onlinestore

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityAllProductEcommerceBinding
import com.pasukanlangit.cashplus.ui.cartproduct.CartProductActivity
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.id.library_core.domain.model.NotifType
import com.pasukanlangit.cashplus.view_model.AllProductEcommerceViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class AllProductEcommerceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllProductEcommerceBinding
    private val viewModel: AllProductEcommerceViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllProductEcommerceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackProduckAll.setOnClickListener { finish() }
        viewModel.getAllProductCarts()
        observeCartProduct()
    }

    private fun observeCartProduct() {
        viewModel.productsInCart.observe(this) { carts ->
            with(binding) {
                tvQty.isVisible = !carts.isNullOrEmpty()
                tvQty.text = carts.size.toString()

                ivCart.setOnClickListener {
                    if(carts.isNullOrEmpty()){
                        val menusAllFragment = ButtomSheetNotif(getString(R.string.empty_cart), NotifType.NOTIF_INPUT_UNCOMPLETED)
                        menusAllFragment.show(supportFragmentManager,menusAllFragment.tag)
                    }else{
                        startActivity(Intent(this@AllProductEcommerceActivity, CartProductActivity::class.java))
                    }
                }
            }
        }
    }
}