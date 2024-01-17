package com.pasukanlangit.cashplus.ui.pages.history.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.adapter.PreviewAdapter
import com.pasukanlangit.cashplus.databinding.FragmentPreviewBinding
import com.pasukanlangit.cashplus.utils.MyUtils.addCharAtEachIndex

class PreviewFragment(
    private val event: OnButtonClickListener
) : DialogFragment() {

    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogNoClose)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val codeProvider = arguments?.getString(CODE_PROVIDER_RESULT_KEY) ?: ""
        val receipt = arguments?.getString(RECEIPT_RESULT_KEY) ?: ""

        with(binding) {
            tvAchievement.text = if (codeProvider.contains("pln", ignoreCase = true))
                getString(R.string.achievement_pln)
            else getString(R.string.achievement_struck)
            val content = setPreviewContent(receipt).split("|").toMutableList()
            content.removeLast()
            with(rvPrintPreview) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = PreviewAdapter(content)
            }
            btnPrint.setOnClickListener {
                event.onPrintClickListener(receipt)
                this@PreviewFragment.dismiss()
            }
            btnShare.setOnClickListener {
                event.onShareClickListener(receipt)
                this@PreviewFragment.dismiss()
            }
        }
    }

    private fun setPreviewContent(receipt: String): String {
        val newReceipt = receipt.replace("\n", "")
        val receiptPreview = StringBuilder()
        val nextLine = newReceipt.split("[L]")
        with(binding) {
            nextLine.forEachIndexed { index, value ->
                if (value.isEmpty()) return@forEachIndexed
                val splitLeftAndRight = value.split(":[R]")
                if (index == 1) {
                    val header = splitLeftAndRight.last().split("[C]")
                    tvCounter.text = splitLeftAndRight.first().trim()
                    tvCounterName.text = header.first().trim().replace("[R]", " ")
                    tvDate.text = header[1]
                    return@forEachIndexed
                }
                if (splitLeftAndRight.first().trim().isNotEmpty())
                    receiptPreview.append("${splitLeftAndRight.first().trim()} : ")
                else receiptPreview.append(" : ")

                val splitRightAndCenter = splitLeftAndRight.last().split("[C]")
                if (splitRightAndCenter.size > 1) {
                    receiptPreview.append("${splitRightAndCenter.first().trim()}|")
                    if (splitRightAndCenter[2].trim().contains("token", true)) {
                        val token = splitRightAndCenter[3].filter { it.isDigit() }
                        val newToken = token.addCharAtEachIndex(4, '-'.toString())
                        txtToken.isVisible = true
                        tvToken.isVisible = true
                        tvToken.text = newToken
                    }
                }
                else receiptPreview.append("${splitLeftAndRight.last().trim()}|")
            }
        }
        return receiptPreview.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface OnButtonClickListener {
        fun onPrintClickListener(receipt: String)
        fun onShareClickListener(receipt: String)
    }

    companion object {

        private const val CODE_PROVIDER_RESULT_KEY = "code_provider_result_key"
        private const val RECEIPT_RESULT_KEY = "receipt_result_key"

        @JvmStatic
        fun newInstance(codeProvider: String?, receipt: String, event: OnButtonClickListener) =
            PreviewFragment(event).apply {
                arguments = Bundle().apply {
                    putString(CODE_PROVIDER_RESULT_KEY, codeProvider)
                    putString(RECEIPT_RESULT_KEY, receipt)
                }
            }
    }
}