package com.pasukanlangit.cashplus.ui.pages.history.print

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.FragmentEditStructBinding

// Not Used
class EditStructFragment(
    private val eventPreviewClick: OnPreviewClickListener
    ) : DialogFragment() {

    private var _binding: FragmentEditStructBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogMax)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditStructBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            arguments?.getString(KEY_RECEIPT)?.let { receipt ->
                edtPreview.setText(receipt)
            }
            btnPrint.setOnClickListener {
                val dataPrint = edtPreview.text.toString().trim()
                eventPreviewClick.onPreviewClicked(dataPrint)
                this@EditStructFragment.dismiss()
            }
        }
    }

    interface OnPreviewClickListener {
        fun onPreviewClicked(receipt: String)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_RECEIPT = "KEY_RECEIPT"

        fun newInstance(receipt: String, eventPreviewClick: OnPreviewClickListener) : EditStructFragment {
            return EditStructFragment(eventPreviewClick).apply {
                arguments = Bundle().apply {
                    putString(KEY_RECEIPT, receipt)
                }
            }
        }
    }
}