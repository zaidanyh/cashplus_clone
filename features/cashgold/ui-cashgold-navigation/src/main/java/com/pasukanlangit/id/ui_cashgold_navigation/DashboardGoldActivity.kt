package com.pasukanlangit.id.ui_cashgold_navigation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.ui_cashgold_buy.register.RegisterCashGoldActivity
import com.pasukanlangit.id.ui_cashgold_navigation.databinding.ActivityDashboardGoldBinding
import com.pasukanlangit.id.ui_core.components.GenericCashGoldModalDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class DashboardGoldActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityDashboardGoldBinding
    private val viewModel: DashboardGoldViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardGoldBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appbar.setIconBackPressed { finish() }

        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.container_main_cashgold) as NavHostFragment
        navController = navHostFragment.findNavController()

        with(binding.btmNavMain){
            setupWithNavController(navController)
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.addressGoldHomeFragment -> {
                        val isLoading = viewModel.stateLoading.value
                        if(!isLoading){
                            val isRegistered = viewModel.stateStatusRegister.value
                            if(isRegistered == true){
                                val navOptions = NavOptions.Builder()
                                    .setPopUpTo(R.id.addressGoldHomeFragment, true)
                                    .build()
                                navController.navigate(R.id.addressGoldHomeFragment,null, navOptions)
                                true
                            }else{
                                showRegisterModel()
                                false
                            }
                        }else{
                            false
                        }
                    }
                    R.id.updateProfileCashGoldFragment -> {
                        val isLoading = viewModel.stateLoading.value
                        if(!isLoading){
                            val isRegistered = viewModel.stateStatusRegister.value
                            if(isRegistered == true){
                                val navOptions = NavOptions.Builder()
                                    .setPopUpTo(R.id.updateProfileCashGoldFragment, true)
                                    .build()
                                navController.navigate(R.id.updateProfileCashGoldFragment, null, navOptions)
                                true
                            }else{
                                showRegisterModel()
                                false
                            }
                        }else{
                            false
                        }
                    }
                    else -> {
                        it.onNavDestinationSelected(navController)
                    }
                }
            }
        }
        collectState()
    }

    private fun collectState() {
        collectStateError()
    }

    private fun showRegisterModel() {
        GenericCashGoldModalDialog.Builder()
            .title(getString(R.string.warning))
            .description("Untuk menggunakan fitur ini silahkan daftar terlebih dahulu.")
            .image(com.pasukanlangit.id.ui_cashgold_buy.R.drawable.ilustration_warning)
            .buttonPositive(
                PositiveButtonAction(
                    btnLabel = getString(R.string.register),
                    onBtnClicked = {
                        startActivity(
                            Intent(this@DashboardGoldActivity, RegisterCashGoldActivity::class.java)
                        )
                    }
                )
            )
            .buttonNegative(
                NegativeButtonAction(
                    btnLabel = getString(R.string.close),
                    setClickOnDismiss = true
                )
            )
            .show(supportFragmentManager)
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkRegisterStatus()
    }

    private fun collectStateError() {
        lifecycleScope.launch {
            viewModel.stateError.collectLatest { message ->
                message.peek()?.let { info ->
                    val toast = Toast.makeText(this@DashboardGoldActivity, info, Toast.LENGTH_SHORT)
                    toast.show()
                    delay(toast.duration.toLong() + 500L)
                    viewModel.removeHeadQueue()
                }
            }
        }
    }

}