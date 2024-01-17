package com.pasukanlangit.id.ui_downline_home.resetmarkup

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.razir.progressbutton.hideProgress
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.core.extensions.onDone
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.domain_downline.utils.PagingDataType
import com.pasukanlangit.id.ui_downline_home.DownLineActivity
import com.pasukanlangit.id.ui_downline_home.DownLineEvent
import com.pasukanlangit.id.ui_downline_home.DownLineViewModel
import com.pasukanlangit.id.ui_downline_home.R
import com.pasukanlangit.id.ui_downline_home.databinding.FragmentResetMarkUpBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import com.pasukanlangit.id.core.R as RCore

class ResetMarkUpFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentResetMarkUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DownLineViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentResetMarkUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int =  RCore.style.DialogStyleResizeKeyboardCore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            edtMarkup.text?.clear()
            edtMarkup.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_CLASS_NUMBER
            iconClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }
            btnReset.setUpToProgressButton(viewLifecycleOwner)
            btnReset.setOnClickListener { submitResetMarkup() }
            edtMarkup.onDone { submitResetMarkup() }
        }

        collectState()
    }

    private fun submitResetMarkup() {
        KeyboardUtil.closeKeyboardDialog(requireContext(), binding.edtMarkup)
        val markUpValue = binding.edtMarkup.text.toString().trim()
        viewModel.onTriggerEvent(DownLineEvent.ResetMarkup(value = markUpValue))
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.isLoadingSetMarkup.collectLatest { isLoading ->
                        binding.btnReset.isEnabled = !isLoading
                        if(isLoading){
                            binding.btnReset.showLoading()
                        }else{
                            binding.btnReset.hideProgress(getString(R.string.reset))
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
                    viewModel.isResetSuccess.collectLatest {
                        it?.let { isSuccess ->
                            if (isSuccess){
                                binding.edtMarkup.text?.clear()
                                Toast.makeText(requireContext(), getString(R.string.reset_markup_successfully), Toast.LENGTH_SHORT).show()
                                (activity as DownLineActivity).collectDownLine(PagingDataType.NONE.value, "")
                                dismiss()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (isShown) return
        super.show(manager, tag)
        isShown = true
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
        private var isShown = false
    }
}