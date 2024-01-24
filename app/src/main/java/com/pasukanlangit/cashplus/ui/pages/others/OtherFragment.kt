package com.pasukanlangit.cashplus.ui.pages.others

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.dhaval2404.imagepicker.ImagePicker
import com.pasukanlangit.cashplus.MainActivityNavComp
import com.pasukanlangit.cashplus.PageMenu
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentOtherBinding
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.compose.component.ProfileIdView
import com.pasukanlangit.cashplus.ui.compose.component.ReferralCodeView
import com.pasukanlangit.cashplus.ui.login.LoginActivity
import com.pasukanlangit.cashplus.ui.pages.others.settings.OtherSettingsBottomSheet
import com.pasukanlangit.cashplus.ui.pages.others.settings.email.ChangeEmailFragment
import com.pasukanlangit.cashplus.ui.product.StatusProductActivity
import com.pasukanlangit.cashplus.utils.MyUtils.callCustomerService
import com.pasukanlangit.cashplus.utils.MyUtils.getPackageInfoCompat
import com.pasukanlangit.cashplus.utils.MyUtils.shareText
import com.pasukanlangit.cashplus.view_model.MainViewModel
import com.pasukanlangit.id.core.CLOSEST_AGENT_PATH_CLASS
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.UNBIND_NOBU_PATH
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.utils.CoreUtils.copyClipboard
import com.pasukanlangit.id.core.utils.TIME_SHOW_NOTIF
import com.pasukanlangit.id.kyc_presentation.AspectRatioCrop
import com.pasukanlangit.id.library_core.domain.model.NotifType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class OtherFragment : Fragment() {

    private var _binding: FragmentOtherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by sharedViewModel()
    private val sharedPrefDataSource: SharedPrefDataSource by inject()
    private val uuid get() = sharedPrefDataSource.getUUID()
    private val token get() = sharedPrefDataSource.getAccessToken()

    private var profileResponse: ProfileResponse? = null
    private lateinit var setting: OtherSettingsBottomSheet

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val version =
                requireActivity().packageManager.getPackageInfoCompat(requireActivity().packageName).versionName
            tvVersionCashplus.text = getString(R.string.cashplus_version, version)
            swiperefreshOthers.setOnRefreshListener {
                viewModel.getProfile(uuid ?: "", token ?: "")
                swiperefreshOthers.isRefreshing = false
            }
            ivProfileOther.setOnClickListener {
                ImagePicker.with(requireActivity())
                    .crop(
                        AspectRatioCrop.PROFILE_PICT_RATIO.widthRatio,
                        AspectRatioCrop.PROFILE_PICT_RATIO.heightRatio
                    )
                    .compress(1024)
                    .maxResultSize(
                        AspectRatioCrop.PROFILE_PICT_RATIO.widthPixels,
                        AspectRatioCrop.PROFILE_PICT_RATIO.heightPixels
                    )
                    .createIntent { intent ->
                        (activity as MainActivityNavComp).startForProfileImageResult.launch(intent)
                    }
            }

            iconChangeProfile.setOnClickListener {
                ImagePicker.with(requireActivity())
                    .crop(
                        AspectRatioCrop.PROFILE_PICT_RATIO.widthRatio,
                        AspectRatioCrop.PROFILE_PICT_RATIO.heightRatio
                    )
                    .compress(1024)
                    .maxResultSize(
                        AspectRatioCrop.PROFILE_PICT_RATIO.widthPixels,
                        AspectRatioCrop.PROFILE_PICT_RATIO.heightPixels
                    )
                    .createIntent { intent ->
                        (activity as MainActivityNavComp).startForProfileImageResult.launch(intent)
                    }
            }
            tvReferralName.setOnClickListener {
                ChangeReferralFragment.newInstance(
                    referralCode = profileResponse?.referral_code,
                    referralName = profileResponse?.referral_full_name,
                    referralNumber = profileResponse?.referral
                )
                    .show(childFragmentManager, "Change Referral")
            }
            btnSettings.setOnClickListener {
                setting = OtherSettingsBottomSheet.newInstance(profileResponse)
                setting.show(requireActivity().supportFragmentManager, setting.tag)
            }
            with(rvOtherMenus) {
                layoutManager = GridLayoutManager(activity, 4)
                adapter = OtherMenusAdapter(OtherMenus.getMenus(requireContext())) { position ->
                    when (position) {
                        0 -> KeagenanBottomFragment().show(childFragmentManager, null)
                        1 -> startActivity(
                            Intent(
                                requireContext(),
                                StatusProductActivity::class.java
                            )
                        )

                        2 -> startActivity(
                            ModuleRoute.internalIntent(
                                requireContext(),
                                UNBIND_NOBU_PATH
                            )
                        )

                        3 -> startActivity(
                            ModuleRoute.internalIntent(
                                requireContext(),
                                CLOSEST_AGENT_PATH_CLASS
                            )
                        )

                        4 -> ChooseRecapitulationFragment().show(childFragmentManager, null)
                        5 -> startActivity(Intent(requireContext(), LicenceActivity::class.java))
                        6 -> SKFragment().show(childFragmentManager, null)
                        7 -> startActivity(
                            Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://cashplus.id/faq")
                            }
                        )

                        else -> {}
                    }
                }
            }
        }
        collectData()
        setComposeContent()
    }

    private fun setComposeContent() {
        binding.wrapperComposeId.setContent {
            val profile by viewModel.profile.collectAsState()
            val firstPhone = remember(profile) { profile?.phones?.firstOrNull()?.phone.orEmpty() }
            val referralCode = remember(profile) { profile?.my_referral_code }
            val referralResult = remember(referralCode) {
                if (referralCode.isNullOrEmpty()) firstPhone
                else profile?.my_referral_code
            }

            ProfileIdView(
                modifier = Modifier.fillMaxWidth(),
                id = firstPhone,
                onBtnCopyClick = {
                    copyClipboard(requireContext(), firstPhone)
                },
                onBtnShareClick = {
                    shareText(
                        requireContext(),
                        "Ayoo daftar cashplus",
                        "Gunakan kode referral $referralResult untuk mendaftar menjadi " +
                                "downline saya di cashplus. More info: https://cashplus.id/"
                    )
                }
            )
        }
        binding.wrapperComposeReferralCode.setContent {
            val profile by viewModel.profile.collectAsState()
            val referralCode = remember(profile) { profile?.my_referral_code }

            ReferralCodeView(
                myReferralCode = referralCode,
                onBtnCallCsClick = {
                    if (!referralCode.isNullOrEmpty()) {
                        startActivity(
                            Intent(requireContext(), ShareReferralActivity::class.java).apply {
                                putExtra(
                                    ShareReferralActivity.KEY_MY_REFERRAL_CODE,
                                    profileResponse?.my_referral_code
                                )
                            }
                        )
                    } else (activity as MainActivityNavComp).navigateToPage(PageMenu.HELP)
                }
            )
        }
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.profileLoading.collectLatest {
                        with(binding) {
                            loading.isVisible = it
                            btnSettings.isEnabled = !it
                            tvReferralName
                        }
                    }
                }
                launch {
                    viewModel.profile.collectLatest { response ->
                        if (response != null) {
                            with(sharedPrefDataSource) {
                                if (getNameProfile() != response.full_name) setNameProfile(response.full_name)
                                if (response.email.isNotEmpty() || getEmailProfile() != response.email)
                                    setEmailProfile(response.email)
                            }
                            with(binding) {
                                profileResponse = response
                                tvNameOther.text = response.full_name
                                if (tvNameOther.lineCount == 2) tvNameOther.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                                else if (tvNameOther.lineCount == 3) tvNameOther.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
                                tvOwnerName.isInvisible = response.owner_name.isEmpty()
                                tvOwnerName.text =
                                    getString(R.string.owner_name, response.owner_name)
                                tvReferralName.text = (response.referral_full_name)

                                var imgProfile = response.img_url
                                if (imgProfile.isEmpty()) imgProfile =
                                    "https://ui-avatars.com/api/?name=${response.full_name}&size=300&rounded=true&background=FFFFFF&color=0A57FF&bold=true"
                                Glide.with(requireContext())
                                    .load(imgProfile)
                                    .thumbnail(
                                        Glide.with(requireContext())
                                            .load(R.raw.image_loading_state)
                                    )
                                    .error(R.drawable.ic_empty)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(ivProfileOther)

                                openEmailConfigFromHome()
                            }
                        }
                    }
                }
                launch {
                    viewModel.profileError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val menusAllFragment = ButtomSheetNotif(info, NotifType.NOTIF_ERROR)
                            menusAllFragment.show(childFragmentManager, menusAllFragment.tag)
                            delay(TIME_SHOW_NOTIF / 2)
                            viewModel.removeProfileError()
                        }
                    }
                }

                launch {
                    viewModel.referralLoading.collectLatest { binding.loading.isVisible = it }
                }
                launch {
                    viewModel.changeReferral.collectLatest { response ->
                        if (response != null) {
                            viewModel.getProfile(uuid ?: "", token ?: "")
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.referral_upline_has_been_changed),
                                Toast.LENGTH_SHORT
                            ).show()
//                            GenericModalDialogCashplus.Builder()
//                                .title(getString(R.string.changes_successful))
//                                .image(R.drawable.illustration_success2)
//                                .description(getString(R.string.referral_upline_has_been_changed))
//                                .buttonPositive(
//                                    PositiveButtonAction(
//                                        btnLabel = getString(R.string.finish),
//                                        backgroundButton = R.drawable.bg_primary_rounded_20,
//                                        buttonTextColor = Color.parseColor("#F1F5F9"),
//                                        onBtnClicked = {
//                                            viewModel.getProfile(uuid ?: "", token ?: "")
//                                        }
//                                    )
//                                ).show(requireActivity().supportFragmentManager, "Update Referral Sucessfully")
                        }
                    }
                }
                launch {
                    viewModel.referralError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.removeReferralError()
                        }
                    }
                }

                launch {
                    viewModel.logoutLoading.collectLatest {
                        if (it) setting.dismiss()
                        binding.loading.isVisible = it
                    }
                }
                launch {
                    viewModel.logout.collectLatest {
                        if (it != null) {
                            sharedPrefDataSource.deleteAuth()
                            sharedPrefDataSource.deleteProfile()

                            val intent = Intent(
                                requireContext(),
                                LoginActivity::class.java
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            activity?.startActivity(intent)
                            activity?.finish()
                        }
                    }
                }
                launch {
                    viewModel.logoutError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val menusAllFragment = ButtomSheetNotif(info, NotifType.NOTIF_ERROR)
                            menusAllFragment.show(
                                requireActivity().supportFragmentManager,
                                menusAllFragment.tag
                            )

                            delay(TIME_SHOW_NOTIF / 2)
                            viewModel.removeLogoutError()
                        }
                    }
                }

                launch {
                    viewModel.uploadPhotoLoading.collectLatest { binding.loading.isVisible = it }
                }
                launch {
                    viewModel.uploadPhoto.collectLatest {
                        if (it != null) viewModel.getProfile(uuid ?: "", token ?: "")
                    }
                }
                launch {
                    viewModel.uploadPhotoError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val menusAllFragment = ButtomSheetNotif(info, NotifType.NOTIF_ERROR)
                            menusAllFragment.show(
                                requireActivity().supportFragmentManager,
                                menusAllFragment.tag
                            )
                            delay(TIME_SHOW_NOTIF / 2)
                            viewModel.removeUploadPhotoError()
                        }
                    }
                }

                launch {
                    viewModel.updateEmailLoading.collectLatest { binding.loading.isVisible = it }
                }
                launch {
                    viewModel.updateEmail.collectLatest {
                        if (it != null) {
                            GenericModalDialogCashplus.Builder()
                                .title(getString(R.string.verify_now))
                                .image(R.drawable.illustration_verify)
                                .description(getString(R.string.verify_email))
                                .buttonNegative(
                                    NegativeButtonAction(
                                        btnLabel = getString(R.string.call_cs),
                                        backgroundButton = R.drawable.bg_primary_rounded_20,
                                        buttonTextColor = Color.parseColor("#FFFFFF"),
                                        onBtnClicked = {
                                            callCustomerService(
                                                context = requireContext(),
                                                message = getString(R.string.format_verify_email_to_cs)
                                            )
                                        }
                                    )
                                )
                                .show(requireActivity().supportFragmentManager, "Dialog Success")
                        }
                    }
                }
                launch {
                    viewModel.updateEmailError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.removeUpdateEmailError()
                        }
                    }
                }
            }
        }
    }

    private fun openEmailConfigFromHome() {
        if ((activity as MainActivityNavComp).stateIsOpenEmailConfig)
            ChangeEmailFragment.newInstance(profileResponse?.email)
                .show(requireActivity().supportFragmentManager, "Config Email")

    }

    override fun onResume() {
        super.onResume()
        viewModel.getProfile(uuid ?: "", token ?: "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
