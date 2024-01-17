package com.pasukanlangit.id.cash_transfer.presentation.local.bank_add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.pasukanlangit.id.cash_transfer.R
import com.pasukanlangit.id.cash_transfer.databinding.FragmentRegisBankAccountBinding
import com.pasukanlangit.id.cash_transfer.domain.model.LocalBankSavedResponse
import com.pasukanlangit.id.cash_transfer.presentation.local.LocalBankEvent
import com.pasukanlangit.id.cash_transfer.presentation.local.LocalBankViewModel
import com.pasukanlangit.id.cash_transfer.presentation.local.utils.AccountBank
import com.pasukanlangit.id.core.utils.InputUtil.checkIsOnlyNumber
import com.pasukanlangit.id.core.utils.KeyboardUtil
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegisBankAccountFragment : Fragment() {

    private var _binding: FragmentRegisBankAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LocalBankViewModel by sharedViewModel()
    private val args: RegisBankAccountFragmentArgs by navArgs()

    private var parent: Fragment? = null
    private var stateBankAccountNum = false

    private lateinit var listBanksSaved: List<LocalBankSavedResponse>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisBankAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parent = (parentFragment as NavHostFragment).parentFragment
        listBanksSaved = args.bankSaved
        (parent as BankListBottomSheetFragment).setBtnBackIsActive(true, requireContext().getString(R.string.destination_rekening))
        with(binding) {
            tvBankName.text = args.bankChosen.bankName
            Glide.with(requireContext())
                .load(args.bankChosen.imgBank)
                .into(imgBank)
            edtBankAccNum.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        inputBankAccNum.error = getString(R.string.input_required, getString(R.string.bank_account_number))
                        stateBankAccountNum = false
                        return
                    }
                    if(!input.checkIsOnlyNumber()) {
                        inputBankAccNum.error = getString(R.string.any_input_is_not_valid, getString(R.string.bank_account_number))
                        stateBankAccountNum = false
                        return
                    }
                    stateBankAccountNum = true
                    inputBankAccNum.error = null
                }
                override fun afterTextChanged(s: Editable?) {
                    btnCheck.isEnabled = stateBankAccountNum
                }
            })
            btnCheck.setOnClickListener {
                onCheckClicked()
            }
        }
    }

    private fun onCheckClicked() {
        val bankNum = binding.edtBankAccNum.text.toString().trim()
        val isAvailable = listBanksSaved.find { item -> item.bank_acc_num == bankNum }

        if (isAvailable != null) {
            Toast.makeText(requireContext(), getString(R.string.already_saved), Toast.LENGTH_SHORT).show()
            return
        }
        KeyboardUtil.closeKeyboardDialog(requireContext(), binding.edtBankAccNum)

        viewModel.onTriggerEvent(
            LocalBankEvent.SetAccountBank(
                AccountBank(
                    productCode = "TAGBANK", destination = "${args.bankChosen.bankCode}-$bankNum-0-",
                    imgUrl = args.bankChosen.imgBank
                )
            )
        )
        (parent as BankListBottomSheetFragment).dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}