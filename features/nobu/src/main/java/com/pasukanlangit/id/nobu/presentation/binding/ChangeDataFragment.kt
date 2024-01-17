package com.pasukanlangit.id.nobu.presentation.binding

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.id.core.utils.InputUtil.checkIsOnlyNumber
import com.pasukanlangit.id.core.utils.InputUtil.checkIsOnlyWords
import com.pasukanlangit.id.core.utils.InputUtil.isValidEmail
import com.pasukanlangit.id.nobu.R
import com.pasukanlangit.id.nobu.databinding.FragmentChangeDataBinding
import com.pasukanlangit.id.nobu.presentation.StateEvent
import com.pasukanlangit.id.nobu.presentation.StateViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ChangeDataFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentChangeDataBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StateViewModel by sharedViewModel()

    private var stateFullName = false
    private var stateEmail = false
    private var statePhone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheet)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChangeDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectData()

        with(binding) {
            btnSave.isEnabled = stateFullName && stateEmail && statePhone
            iconClose.setOnClickListener { dismiss() }
            edtFullname.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if(input.isEmpty()){
                        inputFullname.error = "Nama akun perlu diisi"
                        stateFullName = false
                        return
                    }

                    if(!input.checkIsOnlyWords()) {
                        inputFullname.error = "Nama akun tidak boleh mengandung angka atau simbol"
                        stateFullName = false
                        return
                    }
                    inputFullname.error = null
                    stateFullName = true
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSave.isEnabled = stateFullName && stateEmail && statePhone
                }
            })

            edtEmail.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        inputEmail.error = "Email perlu diisi"
                        stateEmail = false
                        return
                    }
                    if(!input.isValidEmail()) {
                        inputEmail.error = "Email yang kamu gunakan tidak valid"
                        stateEmail = false
                        return
                    }
                    stateEmail = true
                    inputEmail.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSave.isEnabled = stateFullName && stateEmail && statePhone
                }
            })

            edtPhone.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if(input.isEmpty()){
                        inputPhoneNumber.error = "Nomor handphone perlu diisi"
                        statePhone = false
                        return
                    }

                    if(!input.checkIsOnlyNumber()) {
                        inputPhoneNumber.error = "Nomor tidak valid"
                        statePhone = false
                        return
                    }
                    statePhone = true
                    inputPhoneNumber.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnSave.isEnabled = stateFullName && stateEmail && statePhone
                }
            })
            btnSave.setOnClickListener { onSaveClicked() }
        }
    }

    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // COLLECT PHONE
                launch {
                    viewModel.phone.collectLatest {
                        statePhone = if (it.isNullOrEmpty()) false
                        else {
                            binding.edtPhone.setText(it)
                            true
                        }
                    }
                }
                // COLLECT EMAIL
                launch {
                    viewModel.email.collectLatest {
                        stateEmail = if (it.isNullOrEmpty()) {
                            binding.inputEmail.error = "Email perlu diisi"
                            false
                        }
                        else {
                            binding.edtEmail.setText(it)
                            true
                        }
                    }
                }
                // COLLECT NAME
                launch {
                    viewModel.name.collectLatest {
                        stateFullName = if (it.isNullOrEmpty()) false
                        else {
                            binding.edtFullname.setText(it)
                            true
                        }
                    }
                }
            }
        }
    }

    private fun onSaveClicked() {
        with(binding) {
            val newFullName = edtFullname.text.toString().trim()
            val newEmail = edtEmail.text.toString().trim()
            val newPhoneNumber = edtPhone.text.toString().trim()

            viewModel.onTriggerEvent(StateEvent.SetDataBinding(newPhoneNumber, newEmail, newFullName))
            this@ChangeDataFragment.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}