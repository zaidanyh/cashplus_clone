package com.pasukanlangit.cashplus.ui.checkout

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.pasukanlangit.cashplus.MainActivityNavComp
import com.pasukanlangit.cashplus.PageMenu
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentMenusAllBinding
import com.pasukanlangit.cashplus.ui.pages.others.settings.pin.ChangePinActivity
import com.pasukanlangit.cashplus.utils.MyUtils
import com.pasukanlangit.id.core.utils.CoreUtils.copyClipboard
import com.pasukanlangit.id.library_core.domain.model.NotifType

class ButtomSheetNotif(
    private val title: String? = "Terjadi kesalahan, Coba lagi nanti",
    private val notifType: NotifType,
    private val bottomSheetEvent: BottomSheetEvent? = null,
    private val bottomSheetEventLogin: BottomSheetEventLogin? = null,
    private val bottomSheetResetReferral: BottomSheetEventResetReferral? = null,
    private val additionalToken: String ?= null
) : DialogFragment() {
    
    private var _binding: FragmentMenusAllBinding? = null
    private val binding get() = _binding!!

    //best practice to instate fragment, read the reasons here https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment#:~:text=While%20%40yydl%20gives,used%20by%20Android.
    constructor() : this(
        title = "Terjadi kesalahan, Coba lagi nanti",
        notifType = NotifType.NOTIF_ERROR,
        bottomSheetEvent = null,
        bottomSheetEventLogin = null,
        bottomSheetResetReferral = null,
        additionalToken = null
    )


    override fun show(manager: FragmentManager, tag: String?) {
        if (isShown) return

        super.show(manager, tag)
        isShown = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        //make dialog fullscreen
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenusAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.dialog_animation_slide
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            tvBottomsheetTitle.text = title

            when (notifType) {
                NotifType.NOTIF_INPUT_UNCOMPLETED -> {
                    Glide.with(this@ButtomSheetNotif).asGif().load(R.raw.cashplus_gagal).into(ivButtomsheet)
                    btnTopupNotif.visibility = View.GONE
                }
                NotifType.NOTIF_ERROR -> {
                    title?.let { text ->
                        when {
                            text.contains("PIN yang anda masukkan salah", ignoreCase = true) -> {
                                    Glide.with(this@ButtomSheetNotif).asGif().load(R.raw.cashplus_gagal).into(ivButtomsheet)
                                btnTopupNotif.text = getString(R.string.change_pin)
                                btnTopupNotif.setOnClickListener {
                                    this@ButtomSheetNotif.dismiss()

                                    val intent = Intent(activity, ChangePinActivity::class.java)
                                    activity?.startActivity(intent)
                                }
                            }
                            text.contains("koneksi internet", ignoreCase = true) -> {
                                Glide.with(this@ButtomSheetNotif).asGif().load(R.raw.cashplus_nointernet).into(
                                    ivButtomsheet
                                )
                                btnTopupNotif.text = getString(R.string.refresh_network)
                                btnTopupNotif.setOnClickListener { refreshActivity() }
                            }
                            text.contains("already login", ignoreCase = true) ||
                            text.contains("sudah login", ignoreCase = true) -> {
                                Glide.with(this@ButtomSheetNotif).asGif().load(R.raw.cashplus_gagal).into(ivButtomsheet)
                                btnTopupNotif.text = getString(R.string.reset_password)
                                btnTopupNotif.setOnClickListener {
                                    bottomSheetEventLogin?.let { event ->
                                        event.resetPassword()
                                        this@ButtomSheetNotif.dismiss()
                                    }
                                }
                            }
                            text.contains("kode referral", ignoreCase = true) -> {
                                Glide.with(this@ButtomSheetNotif).asGif().load(R.raw.cashplus_gagal).into(ivButtomsheet)
                                btnTopupNotif.text = getString(R.string.reset_referral)
                                btnTopupNotif.setOnClickListener {
                                    bottomSheetResetReferral?.let { event ->
                                        event.resetReferralCode()
                                        this@ButtomSheetNotif.dismiss()
                                    }
                                }
                            }
                            else -> {
                                Glide.with(this@ButtomSheetNotif).asGif().load(R.raw.cashplus_gagal).into(ivButtomsheet)
                                btnTopupNotif.text = getString(R.string.call_cs_wa) //"Hubungi CS (Whatsapp)"
                                btnTelegram.isVisible = true
                                btnTopupNotif.setOnClickListener {
                                    this@ButtomSheetNotif.dismiss()
                                    MyUtils.callCustomerService(requireContext())
                                }

                                btnTelegram.setOnClickListener {
                                    this@ButtomSheetNotif.dismiss()
                                    MyUtils.callCustomerServiceTelegram(requireContext())
                                }
                            }
                        }
                    }
                }
                NotifType.NOTIF_COMING_SOON -> {
                    Glide.with(this@ButtomSheetNotif).asGif().load(R.raw.cashplus_comingsoon).into(ivButtomsheet)
                    btnTopupNotif.visibility = View.GONE
                }
                NotifType.NOTIF_SUCCESS -> {
                    Glide.with(this@ButtomSheetNotif).asGif().load(R.raw.cashplus_sukses).into(ivButtomsheet)
                    btnTopupNotif.visibility = View.GONE
                }
                NotifType.NOTIF_TRX_SUCCESS -> {
                    if(additionalToken != null){
                        wrapperToken.isVisible = true
                        tvToken.text = additionalToken
                        btnCopyToken.setOnClickListener {
                            copyClipboard(requireContext(), additionalToken)
                        }
                    }
                    Glide.with(this@ButtomSheetNotif).asGif().load(R.raw.cashplus_sukses).into(ivButtomsheet)
                    btnTopupNotif.text = getString(R.string.check_history) //"Check History"
                    btnTopupNotif.setOnClickListener {
                        if(activity is MainActivityNavComp){
                            (activity as MainActivityNavComp).navigateToPage(PageMenu.HISTORY)
                        }
                        this@ButtomSheetNotif.dismiss()
                    }
                }
                NotifType.NOTIF_PENDING_OR_PROCESS -> {
                    Glide.with(this@ButtomSheetNotif).asGif().load(R.raw.cashplus_pending).into(ivButtomsheet)
                    btnTopupNotif.text = getString(R.string.check_history)
                    btnTopupNotif.setOnClickListener {
                        if(activity is MainActivityNavComp){
                            (activity as MainActivityNavComp).navigateToPage(PageMenu.HISTORY)
                        }
                        this@ButtomSheetNotif.dismiss()
                    }
                }
                NotifType.NOTIF_TRX_HAS_BEEN_COMPLETED -> {
                    Glide.with(this@ButtomSheetNotif).asGif().load(R.raw.cashplus_gagal).into(ivButtomsheet)
                    btnCancel.visibility = View.VISIBLE
                    btnTopupNotif.text = getString(R.string.yes) //"Ya"

                    btnTopupNotif.setOnClickListener {
                        bottomSheetEvent?.let { event ->
                            event.onRepeatOrder()
                            this@ButtomSheetNotif.dismiss()
                        }
                    }

                    btnCancel.setOnClickListener {
                        bottomSheetEvent?.let { event ->
                            event.onCancelOrder()
                            this@ButtomSheetNotif.dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun refreshActivity(){
        activity?.finish()
        activity?.overridePendingTransition(0, 0)
        startActivity(activity?.intent)
        activity?.overridePendingTransition(0, 0)
    }

    interface BottomSheetEvent {
        fun onRepeatOrder()
        fun onCancelOrder()
    }

    interface BottomSheetEventLogin {
        fun resetPassword()
    }

    interface BottomSheetEventResetReferral {
        fun resetReferralCode()
    }

    override fun onDismiss(dialog: DialogInterface) {
        isShown = false
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var isShown: Boolean = false
    }
}