package com.pasukanlangit.cashplus.ui.pages.history.generatepdf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.adapter.PreviewAdapter
import com.pasukanlangit.cashplus.databinding.FragmentPreviewPDFBinding
import com.pasukanlangit.cashplus.utils.MyUtils.addCharAtEachIndex

// Not used
class PreviewPDFFragment(
    private val eventShare: OnShareClickListener
) : DialogFragment() {

    private var _binding: FragmentPreviewPDFBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogMax)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviewPDFBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receipt = arguments?.getString(RECEIPT_PDF_KEY) ?: ""

        with(binding) {
            tvAchievement.text = getString(R.string.achievement_struck)
            val content = setPreviewPDFContent(receipt).split("|").toMutableList()
            content.removeLast()
            with(rvPdfPreview) {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = PreviewAdapter(content)
            }
            btnSetStruck.setOnClickListener { this@PreviewPDFFragment.dismiss() }
            btnShare.setOnClickListener {
                this@PreviewPDFFragment.dismiss()
                eventShare.onShareClicked(receipt)
            }
        }
    }

    private fun setPreviewPDFContent(receipt: String): String {
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
                    tvCounterName.text = header.first().trim()
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
                        tvAchievement.text = getString(R.string.achievement_pln)
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

    interface OnShareClickListener {
        fun onShareClicked(receipt: String)
    }

    companion object {

        private const val RECEIPT_PDF_KEY = "receipt_print_key"

        @JvmStatic
        fun newInstance(receipt: String, eventShare: OnShareClickListener) =
            PreviewPDFFragment(eventShare).apply {
                arguments = Bundle().apply {
                    putString(RECEIPT_PDF_KEY, receipt)
                }
            }
    }
}