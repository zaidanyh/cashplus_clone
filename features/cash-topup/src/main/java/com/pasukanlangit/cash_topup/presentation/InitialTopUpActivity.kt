package com.pasukanlangit.cash_topup.presentation

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.view.ViewGroup
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.pasukanlangit.cash_topup.R
import com.pasukanlangit.cash_topup.databinding.ActivityInitialTopUpBinding
import com.pasukanlangit.cash_topup.presentation.via_bank.TopUpViaBankFragment
import com.pasukanlangit.cash_topup.presentation.via_ewallet.TopUpViaEWalletFragment
import com.pasukanlangit.cash_topup.presentation.via_merchant.TopUpViaMerchantFragment
import com.pasukanlangit.cash_topup.presentation.via_others.TopUpViaOthersFragment
import com.pasukanlangit.cash_topup.presentation.via_others.qris.ResultQRISFragment
import com.pasukanlangit.cash_topup.presentation.via_va.TopUpViaVirtualAccFragment
import com.pasukanlangit.cash_topup.utils.TopUpUtils.getMenuTopUp
import com.pasukanlangit.id.core.STATE_REQUEST_FLIP_ACCEPT_KEY
import com.pasukanlangit.id.core.extensions.setRupiahListener
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class InitialTopUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInitialTopUpBinding
    private val viewModel: NewTopUpViewModel by viewModel()

    private lateinit var titles: List<String>

    val permissionAccessStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                val fragment = supportFragmentManager.findFragmentByTag("Result QR fragment") as ResultQRISFragment
                fragment.saveImage(window.decorView.rootView)
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.provide_request_permission_access_storage),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitialTopUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) { finishActivity() }
        } else {
            onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() { finishActivity() }
            })
        }

        val stateIsFlipAccept = intent.getBooleanExtra(STATE_REQUEST_FLIP_ACCEPT_KEY, false)
        if (stateIsFlipAccept) viewModel.onTriggerEvent(InitialEvent.GetFlipAcceptList)

        titles = getMenuTopUp()
        setPagerAdapter()
        setupTabLayout()
        setupNominal()
        with(binding) {
            btnBack.setOnClickListener { finishActivity() }
            edtNominalTopup.filters = arrayOf(InputFilter.LengthFilter(11))
            edtNominalTopup.setRupiahListener()
        }

        collectData()
    }

    private fun setPagerAdapter() {
        val topUpFragmentAdapter = TopUpFragmentAdapter(this)
        binding.viewPagerTopUp.isUserInputEnabled = false
        binding.viewPagerTopUp.adapter = topUpFragmentAdapter
    }

    private fun setupTabLayout() {
        with(binding) {
            TabLayoutMediator(
                tabLayoutTopUp, viewPagerTopUp
            ) { tab, position -> tab.text = titles[position] }.attach()

            for (i in 0 until tabLayoutTopUp.tabCount) {
                val tab = (tabLayoutTopUp.getChildAt(0) as ViewGroup).getChildAt(i)
                val p = tab.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(if (i == 0) 40 else 0, 0, if (i == tabLayoutTopUp.tabCount - 1) 40 else 16, 0)
                tab.requestLayout()
            }
        }
    }

    private fun setupNominal() {
        with(binding.rvNominalTopUp) {
            layoutManager = LinearLayoutManager(
                this@InitialTopUpActivity, LinearLayoutManager.HORIZONTAL, false
            )
            adapter = NominalTopUpAdapter { nominal ->
                binding.edtNominalTopup.setText(nominal)
            }
            addItemDecoration(CashplusItemDecoration(10))
        }
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.balanceLoading.collectLatest {
                        with(binding) {
                            if (it) {
                                tvSaldoTopupShimmer.isVisible = true
                                tvSaldoTopupShimmer.startShimmer()
                                return@collectLatest
                            }
                            tvSaldoTopupShimmer.isVisible = false
                            tvSaldoTopupShimmer.stopShimmer()
                        }
                    }
                }
                launch {
                    viewModel.balance.collectLatest { response ->
                        if (response != null) {
                            binding.tvSaldoTopup.text = getNumberRupiah(response.balance)
                        }
                    }
                }
                launch {
                    viewModel.balanceError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@InitialTopUpActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(InitialEvent.RemoveBalanceError)
                        }
                    }
                }

                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@InitialTopUpActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(InitialEvent.RemoveHeadMessage)
                        }
                    }
                }
            }
        }
    }

    private inner class TopUpFragmentAdapter(
        fragmentActivity: FragmentActivity
    ): FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = titles.size

        override fun createFragment(position: Int): Fragment =
            when(position) {
                0 -> TopUpViaBankFragment()
                1 -> TopUpViaVirtualAccFragment()
                2 -> TopUpViaMerchantFragment()
                3 -> TopUpViaEWalletFragment()
                4 -> TopUpViaOthersFragment()
                else -> Fragment()
            }
    }

    private fun finishActivity() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_top_up)
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
            return
        }
        finish()
    }

    fun setErrorEditText(error: String) {
        binding.edtNominalTopup.error = error
    }

    fun getNominal(): String = binding.edtNominalTopup.text.toString().replace(",","").trim()

    fun setIsLoading(state: Boolean) {
        binding.progressBarTopUp.isVisible = state
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(InitialEvent.GetBalance)
    }
}