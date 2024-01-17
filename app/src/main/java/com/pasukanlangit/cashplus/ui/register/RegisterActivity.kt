package com.pasukanlangit.cashplus.ui.register

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityRegisterBinding
import com.pasukanlangit.cashplus.view_model.RegisterViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel : RegisterViewModel by viewModel()

    private lateinit var navController: NavController
    private val resultFromOTP = arrayListOf<String>()
    private var stateCanBack: Boolean = true
    private var stateFirst: Boolean = false
    private var stateSecond: Boolean = false
    private var stateThird: Boolean = false

    val startActivityToOTPActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.getStringArrayListExtra(EnterOTPActivity.KEY_RESULT_OTP)
            data?.let {
                resultFromOTP.addAll(it)
            }
            registerViewModel.setStateSecond(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) { onFinishActivity() }
        } else {
            onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() { onFinishActivity() }
            })
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_register) as NavHostFragment
        navController = navHostFragment.findNavController()

        with(binding) {
            appBar.setOnBackPressed { onFinishActivity() }
            wrapperFirst.setOnClickListener { wrapperFirstOnClick() }
        }

        wrapperFirstOnClick()
        collectState()
    }

    fun wrapperFirstOnClick() {
        if (!stateFirst && !stateSecond && !stateThird) return
        else {
            registerViewModel.setStateFirst(false)
            navController.popBackStack(R.id.firstLayoutRegister, false)
        }
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE FIRST
                launch {
                    registerViewModel.stateFirst.collectLatest { state ->
                        stateFirst = state
                        with(binding) {
                            second.isEnabled = stateFirst
                            tvSecond.isEnabled = stateFirst
                            if (state) indicatorToSecond.setBackgroundColor(Color.parseColor("#0A57FF"))
                            else indicatorToSecond.setBackgroundColor(Color.parseColor("#E2E8F0"))
                        }
                    }
                }

                // STATE SECOND
                launch {
                    registerViewModel.stateSecond.collectLatest { state ->
                        stateSecond = state
                        with(binding) {
                            third.isEnabled = stateSecond
                            tvThird.isEnabled = stateSecond
                            if (state) {
                                findNavController(R.id.nav_host_register).navigate(SecondRegisterFragmentDirections.actionSecondToThird(resultFromOTP.toTypedArray()))
                                indicatorToThird.setBackgroundColor(Color.parseColor("#0A57FF"))
                            }
                            else indicatorToThird.setBackgroundColor(Color.parseColor("#E2E8F0"))
                        }
                    }
                }

                // STATE THIRD
                launch {
                    registerViewModel.stateThird.collectLatest { state ->
                        stateThird = state
                        with(binding) {
                            fourth.isEnabled = stateThird
                            tvFourth.isEnabled = stateThird
                            if (state) indicatorToFourth.setBackgroundColor(Color.parseColor("#0A57FF"))
                            else indicatorToFourth.setBackgroundColor(Color.parseColor("#E2E8F0"))
                        }
                    }
                }
            }
        }
    }

    fun finishing() {
        stateCanBack = false
        binding.wrapperFirst.isEnabled = false
    }

    private fun onFinishActivity() {
        if (!stateCanBack) return
        else finish()
    }
}
