package com.pasukanlangit.cashplus.ui.pages.others.settings.profile.close

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityCloseAccountBinding
import com.pasukanlangit.cashplus.ui.login.LoginActivity
import com.pasukanlangit.id.core.PROFILE_KEY_DATA
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.parcelable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CloseAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCloseAccountBinding
    private val viewModel: CloseAccountViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private var uuid: String? = null
    private var accessToken: String? = null
    private var stateAlready = false

    private val chooseReason = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultData = result?.data?.getStringExtra(ReasonCloseAccountActivity.REASON_RESULT_KEY)
            binding.apply {
                wrapperResult.isVisible = resultData?.isNotEmpty() == true
                if (!resultData.isNullOrEmpty()) {
                    stateAlready = true
                    tvReason.text = resultData
                    btnNext.text = getString(R.string.close_account_now)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloseAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uuid = sharedPref.getUUID()
        accessToken = sharedPref.getAccessToken()
        intent.parcelable<ProfileResponse>(PROFILE_KEY_DATA)?.let { data ->
            binding.apply {
                appBar.setOnBackPressed { finish() }
                tvNameProfile.text = data.full_name
                tvEmailPhoneProfile.text = if (data.email.isEmpty()) data.phones?.first()?.phone
                else getString(R.string.email_phone_profile_format, data.email.lowercase(), data.phones?.first()?.phone)

                val imgUrl = data.img_url.ifEmpty {
                    "https://ui-avatars.com/api/?name=${data.full_name}&size=300&rounded=true&background=0A57FF&color=FFFFFF&bold=true"
                }
                Glide.with(this@CloseAccountActivity)
                    .load(imgUrl)
                    .thumbnail(
                        Glide.with(this@CloseAccountActivity)
                            .load(R.raw.image_loading_state)
                    )
                    .error(R.drawable.ic_empty)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ivProfile)

                wrapperResult.setOnClickListener {
                    if (wrapperResult.isVisible) {
                        chooseReason.launch(
                            Intent(this@CloseAccountActivity, ReasonCloseAccountActivity::class.java).apply {
                                putExtra(ReasonCloseAccountActivity.REASON_RESULT_KEY, tvReason.text)
                            }
                        )
                    }
                }
                btnNext.setOnClickListener {
                    if (stateAlready) {
                        val reason = tvReason.text.toString()
                        GenericModalDialogCashplus.Builder()
                            .title(getString(R.string.confirm))
                            .image(R.drawable.illustration_login_error)
                            .description(getString(R.string.confirm_close_account))
                            .buttonPositive(
                                PositiveButtonAction(
                                    btnLabel = getString(R.string.yes_close_account),
                                    backgroundButton = R.drawable.bg_primary_rounded_20,
                                    buttonTextColor = Color.parseColor("#FFFFFF"),
                                    onBtnClicked = {
                                        viewModel.deleteAccount(uuid.toString(), reason, accessToken.toString())
                                    }
                                )
                            )
                            .buttonNegative(
                                NegativeButtonAction(
                                    btnLabel = getString(R.string.cancel),
                                    backgroundButton = R.drawable.bg_transparent_border_primary_rounded_20,
                                    buttonTextColor = Color.parseColor("#0A57FF"),
                                    setClickOnDismiss = true
                                )
                            )
                            .show(supportFragmentManager)
                        return@setOnClickListener
                    }
                    chooseReason.launch(Intent(this@CloseAccountActivity, ReasonCloseAccountActivity::class.java))
                }
            }
        }

        collectData()
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.deleteAccountLoading.collectLatest {
                        binding.apply {
                            progressBarLoading.isVisible = it
                            btnNext.isEnabled = !it
                            appBar.isEnabled = !it
                        }
                    }
                }
                launch {
                    viewModel.deleteAccount.collectLatest { response ->
                        if (response != null) {
                            sharedPref.deleteAuth()
                            sharedPref.deleteProfile()
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.close_account_success))
                                .image(R.drawable.illustration_bye)
                                .description(getString(R.string.acknowledgments))
                                .setIsCanClose(isCanClose = false)
                                .buttonPositive(
                                    PositiveButtonAction(
                                        btnLabel = getString(R.string.sign_out),
                                        backgroundButton = R.drawable.bg_primary_rounded_20,
                                        buttonTextColor = Color.parseColor("#FFFFFF"),
                                        onBtnClicked = {
                                            finishAffinity()
                                            val intent = Intent(this@CloseAccountActivity, LoginActivity::class.java).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            startActivity(intent)
                                        }
                                    )
                                )
                                .show(supportFragmentManager)
                        }
                    }
                }
                launch {
                    viewModel.deleteAccountError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(this@CloseAccountActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.removeMessageError()
                        }
                    }
                }
            }
        }
    }
}