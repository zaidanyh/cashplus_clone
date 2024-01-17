package com.pasukanlangit.cashplus.ui.ultravoucher

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pasukanlangit.cashplus.databinding.FragmentInputVoucherDataBinding
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.ui.checkout.PembayaranActivity

class InputVoucherDataFragment(private val product: ProductModel) : BottomSheetDialogFragment() {

    private var _binding: FragmentInputVoucherDataBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputVoucherDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTopup.setOnClickListener {
            val name = binding.edtInputName.text.toString().trim()
            val email = binding.edtInputEmail.text.toString().trim()
            val noHp = binding.edtInputNohp.text.toString().trim()

            if(name.isNotEmpty() && email.isNotEmpty() && noHp.isNotEmpty()){
                val intent = Intent(requireContext(), PembayaranActivity::class.java)
                intent.putExtra(PembayaranActivity.KEY_PRODUCT, product)
                intent.putExtra(
                    PembayaranActivity.KEY_NUMBER, "$noHp-$email-$name"
                )
                startActivity(intent)
            }else{
                val menusAllFragment = ButtomSheetNotif("Data tidak boleh kosong", com.pasukanlangit.id.library_core.domain.model.NotifType.NOTIF_INPUT_UNCOMPLETED)
                menusAllFragment.show(childFragmentManager,menusAllFragment.tag)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}