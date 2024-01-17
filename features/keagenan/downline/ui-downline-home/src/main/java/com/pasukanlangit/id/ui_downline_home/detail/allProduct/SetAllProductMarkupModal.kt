package com.pasukanlangit.id.ui_downline_home.detail.allProduct

import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.extensions.onDone
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.domain_downline.utils.PagingDataType
import com.pasukanlangit.id.ui_downline_home.DownLineActivity
import com.pasukanlangit.id.ui_downline_home.DownLineEvent
import com.pasukanlangit.id.ui_downline_home.DownLineViewModel
import com.pasukanlangit.id.ui_downline_home.R
import com.pasukanlangit.id.ui_downline_home.databinding.FragmentSetAllProductMarkupModalBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetAllProductMarkupModal: DialogFragment() {

    private var _binding: FragmentSetAllProductMarkupModalBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DownLineViewModel by viewModel()

    private var downlinePhone: String? = null
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenEnterPinDialogNoClose)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetAllProductMarkupModalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        downlinePhone = arguments?.getString(DOWNLINE_PHONE_KEY)
        name = arguments?.getString(NAME_KEY)
        val currentMarkup = arguments?.getString(CURRENT_MARKUP_KEY)

        with(binding) {
            tvDesc.text = getString(R.string.desc_set_markup_all_product_from_downline, name)
            edtNewMarkupValue.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_CLASS_NUMBER
            edtNewMarkupValue.filters = arrayOf(InputFilter.LengthFilter(4))
            edtNewMarkupValue.setText(currentMarkup)
            btnSubmit.setUpToProgressButton(viewLifecycleOwner)
            btnSubmit.setOnClickListener { submitFromDownline() }
            edtNewMarkupValue.onDone { submitFromDownline() }
        }
        collectState()
    }

    private fun submitFromDownline() {
        val newMarkupValue = binding.edtNewMarkupValue.text.toString().trim()
        if (newMarkupValue.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.markup_value_required), Toast.LENGTH_SHORT).show()
            return
        }
        KeyboardUtil.closeKeyboardDialog(requireContext(), binding.edtNewMarkupValue)
        viewModel.onTriggerEvent(
            DownLineEvent.SetAllProductMarkup(
                dest = downlinePhone ?: "", markupValue = newMarkupValue
            )
        )
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoadingSetMarkup.collectLatest { isLoading ->
                        binding.btnSubmit.isEnabled = !isLoading
                        if(isLoading){
                            binding.btnSubmit.showLoading()
                        }else{
                            binding.btnSubmit.hideProgress(getString(R.string.submit))
                        }
                    }
                }
                launch {
                    viewModel.stateErrorSetMarkup.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(DownLineEvent.RemoveHeadMessage)
                        }
                    }
                }
                launch {
                    viewModel.isSetAllMarkup.collectLatest {
                        it?.let { isSuccess ->
                            if (isSuccess) {
                                Toast.makeText(requireActivity(), getString(R.string.setting_all_markup_successfully, name), Toast.LENGTH_SHORT).show()
                                (activity as DownLineActivity).collectDownLine(PagingDataType.NONE.value, "")
                            }
                            this@SetAllProductMarkupModal.dismiss()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DOWNLINE_PHONE_KEY = "downline_phone_key"
        private const val NAME_KEY = "name_key"
        private const val CURRENT_MARKUP_KEY = "current_markup_key"

        @JvmStatic
        fun newInstance(downlinePhone: String, name: String, markup: String) =
            SetAllProductMarkupModal().apply {
                arguments = Bundle().apply {
                    putString(DOWNLINE_PHONE_KEY, downlinePhone)
                    putString(NAME_KEY, name)
                    putString(CURRENT_MARKUP_KEY, markup)
                }
            }
    }
}