package com.pasukanlangit.cashplus.ui.pages.others.settings

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentOtherSettingsBottomSheetBinding
import com.pasukanlangit.cashplus.ui.pages.others.settings.email.ChangeEmailFragment
import com.pasukanlangit.cashplus.ui.pages.others.settings.password.ChangePasswordActivity
import com.pasukanlangit.cashplus.ui.pages.others.settings.pin.ChangePinActivity
import com.pasukanlangit.cashplus.ui.pages.others.settings.profile.ChangeProfilActivity
import com.pasukanlangit.cashplus.view_model.MainViewModel
import com.pasukanlangit.id.core.KYC_INIT_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.PROFILE_KEY_DATA
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.core.presentation.components.GenericConfirmDialogFragment
import com.pasukanlangit.id.core.presentation.components.NegativeButton
import com.pasukanlangit.id.core.presentation.components.PositiveButton
import com.pasukanlangit.id.core.utils.parcelable
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class OtherSettingsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentOtherSettingsBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by sharedViewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    private val uuid get() = sharedPref.getUUID()
    private val accessToken get() = sharedPref.getAccessToken()

    private var profile: ProfileResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtherSettingsBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile = arguments?.parcelable(DATA_PROFILE_KEY)
        with(binding) {
            btnEditProfile.setOnClickListener {
                val intent = Intent(activity, ChangeProfilActivity::class.java)
                intent.putExtra(PROFILE_KEY_DATA, profile)
                startActivity(intent)
                this@OtherSettingsBottomSheet.dismiss()
            }
            btnEditEmail.setOnClickListener {
                ChangeEmailFragment
                    .newInstance(profile?.email)
                    .show(requireActivity().supportFragmentManager, "Config Email")
                this@OtherSettingsBottomSheet.dismiss()
            }
            btnChangePin.setOnClickListener {
                startActivity(Intent(activity, ChangePinActivity::class.java))
                this@OtherSettingsBottomSheet.dismiss()
            }
            btnChangePassword.setOnClickListener {
                val intent = Intent(activity, ChangePasswordActivity::class.java)
                intent.putExtra(
                    ChangePasswordActivity.KEY_PHONE_NUMBER,
                    profile?.phones?.getOrNull(0)?.phone
                )
                startActivity(intent)
                this@OtherSettingsBottomSheet.dismiss()
            }
            btnEKyc.setOnClickListener {
                startActivity(ModuleRoute.internalIntent(requireContext(), KYC_INIT_PATH))
                this@OtherSettingsBottomSheet.dismiss()
            }
            btnSignOut.setOnClickListener {
                GenericConfirmDialogFragment.Builder()
                    .title(getString(R.string.warning))
                    .description(getString(R.string.confirm_leave_cashplus))
                    .buttonPositive(
                        PositiveButton(
                            backgroundButton = R.drawable.bg_red600_rounded_12,
                            buttonText = getString(R.string.sign_out),
                            buttonTextColor = Color.parseColor("#FFFFFF"),
                            onBtnAction = {
                                viewModel.logout(uuid ?: "", accessToken ?: "")
                            }
                        )
                    )
                    .buttonNegative(
                        NegativeButton(
                            backgroundButton = R.drawable.bg_red50_rounded_12,
                            buttonText = getString(R.string.cancel),
                            buttonTextColor = Color.parseColor("#FF6150"),
                            actionDismiss = true
                        )
                    ).show(childFragmentManager, "Leave Sign Out")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val DATA_PROFILE_KEY = "data_profile_key"

        @JvmStatic
        fun newInstance(profile: ProfileResponse?) =
            OtherSettingsBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(DATA_PROFILE_KEY, profile)
                }
            }
    }
}