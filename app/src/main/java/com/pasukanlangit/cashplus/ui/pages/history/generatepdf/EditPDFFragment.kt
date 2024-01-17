package com.pasukanlangit.cashplus.ui.pages.history.generatepdf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentEditPDFBinding

// Not Used
class EditPDFFragment(
    private val eventGeneratePDF: OnPreviewClickListener
) : DialogFragment() {

    private var _binding: FragmentEditPDFBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogMax)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPDFBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            arguments?.getString(KEY_RECEIPT)?.let { data ->
                edtPreviewPdf.setText(data)
            }
            btnShare.setOnClickListener {
                val dataPDF = edtPreviewPdf.text.toString().trim()
                eventGeneratePDF.onPreviewClicked(dataPDF)
                this@EditPDFFragment.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface OnPreviewClickListener {
        fun onPreviewClicked(receipt: String)
    }

    companion object {
        private const val KEY_RECEIPT = "key_receipt"

        @JvmStatic
        fun newInstance(receipt: String, eventGeneratePDF: OnPreviewClickListener) =
            EditPDFFragment(eventGeneratePDF).apply {
                arguments = Bundle().apply {
                    putString(KEY_RECEIPT, receipt)
                }
            }
    }
}