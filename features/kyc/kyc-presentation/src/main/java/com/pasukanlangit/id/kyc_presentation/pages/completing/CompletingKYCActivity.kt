package com.pasukanlangit.id.kyc_presentation.pages.completing

import android.app.Activity
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.pasukanlangit.id.core.CHANGE_PROFILE_FROM_EKYC
import com.pasukanlangit.id.core.CHANGE_PROFILE_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.PROFILE_KEY_DATA
import com.pasukanlangit.id.core.model.PhoneList
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.getBase64FromUri
import com.pasukanlangit.id.kyc_domain.domain.model.EKycProfileUser
import com.pasukanlangit.id.kyc_presentation.*
import com.pasukanlangit.id.kyc_presentation.databinding.ActivityCompletingKycBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompletingKYCActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompletingKycBinding
    private val viewModel: CompletingDataViewModel by viewModel()
    private lateinit var navController: NavController

    private var stateCanBack: Boolean = true
    private var profileResponse: EKycProfileUser? = null

    val takePhotoIdCard =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val resultData = result.data

            when(resultCode) {
                Activity.RESULT_OK -> {
                    GenericModalDialogCashplus.isShown = false
                    viewModel.onTriggerEvents(
                        CompletingEvents.EKycUploadVerify(
                            verificationType = EKycVerifyType.OCR.value,
                            uploadType = EKycUploadType.NIK.value,
                            base64Data = getBase64FromUri(resultData?.data, contentResolver)
                        )
                    )
                }
                Activity.RESULT_CANCELED -> {
                    GenericModalDialogCashplus.isShown = false
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(resultData), Toast.LENGTH_SHORT).show()
                }
            }
        }

    val takePhotoSelfie =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val resultData = result.data

            when(resultCode) {
                Activity.RESULT_OK -> {
                    GenericModalDialogCashplus.isShown = false
                    viewModel.onTriggerEvents(
                        CompletingEvents.EKycUploadVerify(
                            verificationType = EKycVerifyType.SELFIE.value,
                            uploadType = EKycUploadType.SELFIE.value,
                            base64Data = getBase64FromUri(resultData?.data, contentResolver)
                        )
                    )
                }
                Activity.RESULT_CANCELED -> {
                    GenericModalDialogCashplus.isShown = false
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(resultData), Toast.LENGTH_SHORT).show()
                }
            }
        }

    private val changeProfile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        viewModel.onTriggerEvents(
            CompletingEvents.EKycOnlyVerify(EKycVerifyType.OCR.value)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompletingKycBinding.inflate(layoutInflater)
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

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_kyc) as NavHostFragment
        navController = navHostFragment.findNavController()

        if (intent.getBooleanExtra(IS_ID_CARD_UPLOADED_STATE, false))
            viewModel.onTriggerEvents(CompletingEvents.EKycOnlyVerify(EKycVerifyType.OCR.value))

        if (intent.getBooleanExtra(IS_UPLOAD_SELFIE_STATE, false))
            viewModel.onTriggerEvents(CompletingEvents.SetStateFirst(true))

        if (intent.getBooleanExtra(IS_SELFIE_UPLOADED_STATE, false)) {
            viewModel.onTriggerEvents(CompletingEvents.SetStateFirst(true))
            viewModel.onTriggerEvents(CompletingEvents.EKycOnlyVerify(EKycVerifyType.SELFIE.value))
        }

        if (intent.getBooleanExtra(IS_WAITING_EKYC_STATE, false)) {
            viewModel.onTriggerEvents(CompletingEvents.SetStateFirst(true))
            viewModel.onTriggerEvents(CompletingEvents.SetStateSecond(true))
        }

        with(binding) {
            appBar.setOnBackPressed { onFinishActivity() }
        }

        collectProfile()
        collectStateLayout()
        collectUploadAndVerify()
        collectVerifyOnly()
    }

    private fun collectProfile() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateLoading.collectLatest { binding.smallProgressBar.isVisible = it }
                }
                launch {
                    viewModel.profile.collectLatest { if (it != null) profileResponse = it }
                }
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(this@CompletingKYCActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvents(CompletingEvents.RemoveHeadProfileMessage)
                        }
                    }
                }
            }
        }
    }

    private fun collectStateLayout() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateFirst.collectLatest { state ->
                        with(binding) {
                            second.isEnabled = state
                            tvSecond.isEnabled = state
                            if (state) {
                                navController.safeNavigate(KycIdentityFragmentDirections.kycIdentityToKycSelfie())
                                indicatorToSecond.setBackgroundColor(Color.parseColor("#0A57FF"))
                            }
                            else indicatorToSecond.setBackgroundColor(Color.parseColor("#E2E8F0"))
                        }
                    }
                }
                launch {
                    viewModel.stateSecond.collectLatest { state ->
                        with(binding) {
                            third.isEnabled = state
                            tvThird.isEnabled = state
                            if (state) {
                                navController.safeNavigate(KycSelfieFragmentDirections.kycSelfieToKycDone())
                                indicatorToThird.setBackgroundColor(Color.parseColor("#0A57FF"))
                            }
                            else indicatorToThird.setBackgroundColor(Color.parseColor("#E2E8F0"))
                        }
                    }
                }
            }
        }
    }

    private fun collectUploadAndVerify() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uploadLoading.collectLatest {
                        with(binding) {
                            txtProcessUpload.text = getString(R.string.processing_upload)
                            appBar.isEnabled = !it
                            backgroundLoading.isVisible = it
                            layoutLoading.isVisible = it
                        }
                    }
                }
                launch {
                    viewModel.uploadError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.contains("ktp", ignoreCase = true))
                                showDialogState(
                                    getString(R.string.photo_upload_rejected), info,
                                    getString(R.string.re_upload), EKycVerifyType.OCR.value, "Upload"
                                )
                            else if ("""(?i)(nik|nama|lahir|name|birth)""".toRegex().containsMatchIn(info))
                                showDialogState(
                                    getString(R.string.photo_upload_rejected), info,
                                    getString(R.string.change_account), EKycVerifyType.OCR.value, "Upload"
                                )
                            else if ("""(?i)(wajah|kesamaan)""".toRegex().containsMatchIn(info))
                                showDialogState(
                                    getString(R.string.photo_upload_rejected), info,
                                    getString(R.string.re_upload), EKycVerifyType.SELFIE.value, "Upload"
                                )
                            else {
                                val toast =
                                    Toast.makeText(this@CompletingKYCActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggerEvents(CompletingEvents.RemoveHeadUploadMessage)
                            }
                        }
                    }
                }
                launch {
                    viewModel.eKycUploadAndVerify.collectLatest {
                        if (it != null) {
                            if (it.uploadType == EKycUploadType.NIK.value)
                                viewModel.onTriggerEvents(CompletingEvents.SetStateFirst(true))
                            if (it.uploadType == EKycUploadType.SELFIE.value)
                                viewModel.onTriggerEvents(CompletingEvents.SetStateSecond(true))
                        }
                    }
                }
            }
        }
    }

    private fun collectVerifyOnly() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.verifyLoading.collectLatest {
                        with(binding) {
                            txtProcessUpload.text = getString(R.string.check_verify)
                            appBar.isEnabled = !it
                            backgroundLoading.isVisible = it
                            layoutLoading.isVisible = it
                        }
                    }
                }
                launch {
                    viewModel.verifyError.collectLatest { message ->
                        message.peek()?.let { info ->
                            if (info.contains("ktp", ignoreCase = true))
                                showDialogState(
                                    getString(R.string.photo_upload_rejected), info,
                                    getString(R.string.re_upload), EKycVerifyType.OCR.value, "Verify"
                                )
                            else if ("""(?i)(nik|nama|lahir|name|birth)""".toRegex().containsMatchIn(info))
                                showDialogState(
                                    getString(R.string.photo_upload_rejected), info,
                                    getString(R.string.change_account), EKycVerifyType.OCR.value, "Verify"
                                )
                            else if ("""(?i)(wajah|kesamaan)""".toRegex().containsMatchIn(info))
                                showDialogState(
                                    getString(R.string.photo_upload_rejected), info,
                                    getString(R.string.re_upload), EKycVerifyType.SELFIE.value, "Verify"
                                )
                            else {
                                val toast =
                                    Toast.makeText(this@CompletingKYCActivity, info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.onTriggerEvents(CompletingEvents.RemoveHeadVerifyMessage)
                            }
                        }
                    }
                }
                launch {
                    viewModel.eKycOnlyVerify.collectLatest {
                        if (it != null) {
                            if (it.verificationType == EKycVerifyType.OCR.value)
                                viewModel.onTriggerEvents(CompletingEvents.SetStateFirst(true))
                            if (it.verificationType == EKycVerifyType.SELFIE.value)
                                viewModel.onTriggerEvents(CompletingEvents.SetStateSecond(true))
                        }
                    }
                }
            }
        }
    }

    private fun showDialogState(title: String, body: String, actionText: String, type: String, source: String) {
        GenericModalDialogCashplus.Builder()
            .title(title)
            .image(R.drawable.ilustration_warning)
            .description(
                if (type == EKycVerifyType.OCR.value && ("""(?i)(nik|nama|lahir|name|birth)""".toRegex().containsMatchIn(body)))
                    "$body\n${getString(R.string.please_update_your_profile)}"
                else if (type == EKycVerifyType.SELFIE.value && body.contains("kesamaan", ignoreCase = true))
                    getString(R.string.selfie_not_similar)
                else body
            )
            .buttonPositive(
                PositiveButtonAction(
                    btnLabel = actionText,
                    onBtnClicked = {
                        if (source == "Upload") viewModel.onTriggerEvents(CompletingEvents.RemoveHeadUploadMessage)
                        else if (source == "Verify") viewModel.onTriggerEvents(CompletingEvents.RemoveHeadVerifyMessage)

                        if (type == EKycVerifyType.OCR.value && body.contains("ktp", ignoreCase = true))
                            takePhotoIdCard.launch(imagePickerIDIntent(this, ImagePickerType.CAMERA_ONLY))
                        else if (type == EKycVerifyType.OCR.value && ("""(?i)(nik|nama|lahir|name|birth)""".toRegex().containsMatchIn(body))) {
                            profileResponse?.let { p ->
                                val profile = ProfileResponse(
                                    full_name = p.full_name, balance = 0.0, point = 0.0, address = p.address,
                                    prov = p.prov, city = p.city, district = p.district, village = p.village,
                                    zipcode = p.zipcode, nik = p.nik, email = p.email, img_url = p.img_url,
                                    referral = "", is_kyc_approved = "", owner_name = p.owner_name,
                                    placeOfBorn = p.placeOfBorn, dateOfBirth = p.dateOfBirth, my_referral_code = "",
                                    referral_full_name = "", referral_code = "", phones = p.phones?.map { PhoneList(it.phone) }
                                )
                                changeProfile.launch(
                                    ModuleRoute.internalIntent(this, CHANGE_PROFILE_PATH).apply {
                                        putExtra(PROFILE_KEY_DATA, profile)
                                        putExtra(CHANGE_PROFILE_FROM_EKYC, true)
                                    }
                                )
                            }
                        }
                        else if (type == EKycVerifyType.SELFIE.value && ("""(?i)(wajah|kesamaan)""".toRegex().containsMatchIn(body)))
                            takePhotoSelfie.launch(imagePickerSelfieIntent(this, ImagePickerType.CAMERA_ONLY))
                    }
                )
            ).show(supportFragmentManager)
    }

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
    }

    private fun onFinishActivity() {
        if (!stateCanBack) return
        else finish()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvents(CompletingEvents.EKycProfileUser)
    }

    companion object {
        const val IS_ID_CARD_UPLOADED_STATE = "is_id_card_uploaded_state"
        const val IS_UPLOAD_SELFIE_STATE = "is_upload_selfie_state"
        const val IS_SELFIE_UPLOADED_STATE = "is_selfie_uploaded_state"
        const val IS_WAITING_EKYC_STATE = "is_waiting_ekyc_state"
    }
}