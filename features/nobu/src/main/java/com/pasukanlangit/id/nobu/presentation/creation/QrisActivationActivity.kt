package com.pasukanlangit.id.nobu.presentation.creation

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.pasukanlangit.id.nobu.R
import com.pasukanlangit.id.nobu.databinding.ActivityQrisActivationBinding
import com.pasukanlangit.id.nobu.presentation.ConfirmBackDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class QrisActivationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrisActivationBinding
    private val viewModel: ActivationViewModel by viewModel()

    private lateinit var navController: NavController
    private var stateCanBack: Boolean = true
    private var stateFirst: Boolean = false
    private var stateSecond: Boolean = false
    private var stateThird: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrisActivationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { onFinishActivity() }
        })

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_activation) as NavHostFragment
        navController = navHostFragment.findNavController()

        with(binding) {
            appBar.setOnBackPressed { onFinishActivity() }
            wrapperFirst.setOnClickListener { wrapperFirstOnClick() }
        }
        collectState()
    }

    private fun wrapperFirstOnClick() {
        if (!stateFirst && !stateSecond && !stateThird) return
        else {
            viewModel.setStateFirst(false)
            navController.popBackStack(R.id.completingData, false)
        }
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateFirst.collectLatest { state ->
                        stateFirst = state
                        with(binding) {
                            second.isEnabled = state
                            tvSecond.isEnabled = state
                            if (state) indicatorToSecond.setBackgroundColor(Color.parseColor("#0A57FF"))
                            else indicatorToSecond.setBackgroundColor(Color.parseColor("#E2E8F0"))
                        }
                    }
                }

                launch {
                    viewModel.stateSecond.collectLatest { state ->
                        stateSecond = state
                        with(binding) {
                            third.isEnabled = state
                            tvThird.isEnabled = state
                            if (state) indicatorToThird.setBackgroundColor(Color.parseColor("#0A57FF"))
                            else indicatorToThird.setBackgroundColor(Color.parseColor("#E2E8F0"))
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
        else {
            ConfirmBackDialogFragment.newInstance(
                isAccountBinding = false
            ).show(supportFragmentManager, "Leave Account Creation")
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getQuestionsAndTermConditionAndProfile()
    }
}