package com.pasukanlangit.id.cash_transfer.presentation.local.bank_add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.cash_transfer.databinding.FragmentAddBankSuccessBinding

class AddBankSuccessFragment(
    private val event: SetOnButtonsClickListener
) : DialogFragment() {

    private var _binding: FragmentAddBankSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.fullScreenEnterPINOrOTP)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBankSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.isCancelable = false

        val destination = arguments?.getString(ARG_DESTINATION)
        val infoName = arguments?.getString(ARG_INFO_NAME)
        val bankImgUrl = arguments?.getString(ARG_IMAGE_URL)

        with(binding) {
            val destinationSplit = destination?.split("-")
            Glide.with(requireContext())
                .load(bankImgUrl)
                .into(imgBank)
            tvBankName.text = infoName
            tvBankNum.text = destinationSplit?.get(1)

            btnSave.setOnClickListener {
                event.onSavingBank(destinationSplit, infoName)
            }
            btnContinueTransfer.setOnClickListener {
                event.onContinueTransfer(destinationSplit, infoName)
            }
        }
    }

    fun setLoading(state: Boolean) {
        if (isVisible) binding.progressBar.isVisible = state
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface SetOnButtonsClickListener {
        fun onSavingBank(destination: List<String>?, infoName: String?)
        fun onContinueTransfer(destination: List<String>?, infoName: String?)
    }

    companion object {
        private const val ARG_DESTINATION = "arg_destination"
        private const val ARG_INFO_NAME = "arg_info_name"
        private const val ARG_IMAGE_URL = "arg_image_url"

        @JvmStatic
        fun newInstance(
            destination: String?, infoName: String?, imgUrl: String?, event: SetOnButtonsClickListener
        ) = AddBankSuccessFragment(event).apply {
            arguments = Bundle().apply {
                putString(ARG_DESTINATION, destination)
                putString(ARG_INFO_NAME, infoName)
                putString(ARG_IMAGE_URL, imgUrl)
            }
        }
    }
}