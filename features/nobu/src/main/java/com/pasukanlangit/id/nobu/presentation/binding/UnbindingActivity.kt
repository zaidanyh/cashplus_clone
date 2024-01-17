package com.pasukanlangit.id.nobu.presentation.binding

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pasukanlangit.id.core.MAIN_ACTIVITY_FORWARDING_TO_HELP
import com.pasukanlangit.id.core.MAIN_ACTIVITY_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.databinding.CashplusCustomToastBinding
import com.pasukanlangit.id.core.utils.CoreUtils.showCoreComingSoon
import com.pasukanlangit.id.core.utils.InputUtil.toCapitalize
import com.pasukanlangit.id.nobu.R
import com.pasukanlangit.id.nobu.databinding.ActivityUnbindingBinding
import com.pasukanlangit.id.nobu.presentation.ConfirmBackDialogFragment
import com.pasukanlangit.id.nobu.presentation.StateEvent
import com.pasukanlangit.id.nobu.presentation.StateViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class UnbindingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUnbindingBinding
    private val viewModel: StateViewModel by viewModel()

    private var isBinded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnbindingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.onTriggerEvent(StateEvent.FetchProfile)
        viewModel.onTriggerEvent(StateEvent.CheckBinding)

        with(binding) {
            appBar.setOnBackPressed { finish() }
            btnStateBind.setOnClickListener {
                if (isBinded) ConfirmBackDialogFragment.newInstance(isUnbind = true).show(supportFragmentManager, "Unbind Account")
//                else startActivity(Intent(this@UnbindingActivity, LoadingStateActivity::class.java))
                else showCoreComingSoon(this@UnbindingActivity, supportFragmentManager)
            }
            btnCallCs.setOnClickListener {
                startActivity(
                    ModuleRoute.internalIntent(this@UnbindingActivity, MAIN_ACTIVITY_PATH).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(MAIN_ACTIVITY_FORWARDING_TO_HELP, true)
                    }
                )
                finish()
            }
        }
        collectState()
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STATE DATA PROFILE
                launch {
                    viewModel.profile.collectLatest { response ->
                        if (response != null) {
                            with(binding) {
                                tvFullname.text = response.full_name.toCapitalize()
                                tvPhone.text = response.phones?.first()?.phone
                                tvEmail.text = response.email
                                tvEmail.isVisible = response.email.isNotEmpty()
                                txtEmailIsEmpty.isVisible = response.email.isEmpty()
                            }
                        }
                    }
                }

                // STATE IS BINDED
                launch {
                    viewModel.stateIsBinded.collectLatest { response ->
                        if (response != null) {
                            with(binding.btnStateBind) {
                                if (response.isBinded == "1") {
                                    isBinded = true
                                    setBackgroundResource(R.drawable.bg_red50_border_red200_rounded_20)
                                    text = getString(R.string.unbind_nobu)
                                    setTextColor(Color.parseColor("#FF3822"))
                                } else {
                                    isBinded = false
                                    setBackgroundResource(R.drawable.bg_blue50_border_blue200_rounded_20)
                                    text = getString(R.string.bind_nobu)
                                    setTextColor(Color.parseColor("#175CD3"))
                                }
                            }
                        }
                    }
                }

                // STATE UNBIND SUCCESS
                launch {
                    viewModel.stateIsUnbind.collectLatest { response ->
                        if (response != null) {
                            viewModel.onTriggerEvent(StateEvent.CheckBinding)
                            showCustomToast()
                        }
                    }
                }

                // STATE LOADING
                launch {
                    viewModel.stateLoading.collectLatest { binding.progressBar.isVisible = it }
                }

                // STATE ERROR
                launch {
                    viewModel.stateError.collectLatest { message ->
                        if (message.isNotEmpty()) {
                            message.peek()?.let { info ->
                                val toast = Toast.makeText(this@UnbindingActivity, info, Toast.LENGTH_SHORT)
                                if (info.contains("Belum terbinding")) {
                                    isBinded = false
                                    with(binding.btnStateBind) {
                                        setBackgroundResource(R.drawable.bg_blue50_border_blue200_rounded_20)
                                        text = getString(R.string.bind_nobu)
                                        setTextColor(Color.parseColor("#175CD3"))
                                    }
                                } else {
                                    toast.show()
                                }
                                delay(toast.duration.toLong() + 500L)
                                viewModel.onTriggerEvent(StateEvent.RemoveHeadMessage)
                            }
                        }
                    }
                }

                // STATE UNBIND ERROR
                launch {
                    viewModel.stateBindingError.collectLatest { message ->
                        if (message.isNotEmpty()) {
                            message.peek()?.let { info ->
                                val toast =
                                    Toast.makeText(this@UnbindingActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggerEvent(StateEvent.RemoveBindingHeadMessage)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showCustomToast() {
        val customToast = CashplusCustomToastBinding.inflate(LayoutInflater.from(this))
        customToast.txtToast.text = getString(R.string.unbind_success)
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_LONG
        toast.view = customToast.root
        toast.show()
    }
}