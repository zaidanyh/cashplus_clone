package com.pasukanlangit.id.rebate.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.id.rebate.R
import com.pasukanlangit.id.rebate.databinding.ActivityRebateDetailBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RebateDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRebateDetailBinding
    private val viewModel: RebateViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRebateDetailBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { onFinishActivity() }
        })

        binding.appbar.setOnBackPressed { onFinishActivity() }
        val dateStart = intent.getStringExtra(KEY_DATE_START)
        val dateEnd = intent.getStringExtra(KEY_DATE_END)
        if (!dateStart.isNullOrEmpty() && !dateEnd.isNullOrEmpty()) {
            viewModel.getRebatePerProduct(dateStart = dateStart, dateEnd = dateEnd)
        }
        initRecycleView()
        collectState()
    }

    private fun initRecycleView() {
        with(binding.rvProductRebate) {
            layoutManager = LinearLayoutManager(this@RebateDetailActivity)
        }
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //STATE REBATE PER PRODUCT
                launch {
                    viewModel.rebatePerProduct.collectLatest {
                        it?.let { rebatePerProduct ->
                            binding.rvProductRebate.adapter = RebateDetailProductAdapter(rebatePerProduct)
                        }
                    }
                }
                //STATE LOADING
                launch {
                    viewModel.stateLoadingButton.collectLatest {
                        with(binding){
                            progressBar.isVisible = it
                            rvProductRebate.isVisible = !it
                        }
                    }
                }
                //STATE ERROR
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@RebateDetailActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong() + 500L)
                            viewModel.removeHeadQueue()
                        }
                    }
                }
            }
        }
    }

    private fun onFinishActivity() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    companion object {
        const val KEY_DATE_START = "key_date_start"
        const val KEY_DATE_END = "key_date_end"
    }
}