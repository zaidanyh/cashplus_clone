package com.pasukanlangit.id.nobu.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pasukanlangit.id.nobu.databinding.ActivityLoadingStateBinding
import com.pasukanlangit.id.nobu.presentation.scan.QrScanActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoadingStateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingStateBinding
    private val viewModel: StateViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingStateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        collectStateBinded()
    }

    private fun collectStateBinded() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //STATE LOADING
                launch {
                    viewModel.stateLoading.collectLatest { isLoading ->
                        binding.progressBar.isVisible = isLoading
                    }
                }
                //STATE ERROR
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.contains("Belum terbinding")) {
                                DialogChooseFragment().show(supportFragmentManager, DialogChooseFragment::class.java.name)
                            } else {
                                val toast =
                                    Toast.makeText(this@LoadingStateActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                            }
                            viewModel.onTriggerEvent(StateEvent.RemoveHeadMessage)
                        }
                    }
                }
                // CHECK IS BINDED
                launch {
                    viewModel.stateIsBinded.collectLatest { response ->
                        if (response != null) {
                            if (response.isBinded == "1") {
                                startActivity(Intent(this@LoadingStateActivity, QrScanActivity::class.java))
                                finish()
                            } else DialogChooseFragment().show(supportFragmentManager, DialogChooseFragment::class.java.name)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(StateEvent.CheckBinding)
    }
}