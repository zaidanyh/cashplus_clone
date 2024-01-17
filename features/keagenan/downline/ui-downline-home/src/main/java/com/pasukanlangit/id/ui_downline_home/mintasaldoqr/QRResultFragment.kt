package com.pasukanlangit.id.ui_downline_home.mintasaldoqr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.ui_downline_home.DownLineEvent
import com.pasukanlangit.id.ui_downline_home.DownLineViewModel
import com.pasukanlangit.id.ui_downline_home.databinding.FragmentQRResultBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class QRResultFragment : Fragment() {

    private var _binding: FragmentQRResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DownLineViewModel by sharedViewModel()
    private val navArgs by navArgs<QRResultFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQRResultBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navArgs.nominal.let {
            viewModel.onTriggerEvent(DownLineEvent.GenerateQR(amount = it))
            binding.tvNominal.text = getNumberRupiah(it.toDoubleOrNull())
        }

        binding.btnGenerateNewQr.setOnClickListener {
            findNavController().popBackStack()
        }

        collectState()
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                //RESULT STATE
                launch {
                    viewModel.imageQR.collectLatest {
                        it?.let { imageUrl ->
                            Glide
                                .with(requireContext())
                                .load(imageUrl)
                                .into(binding.ivQr)
                        }
                    }
                }

                //LOADING STATE
                launch {
                    viewModel.isLoadingQr.collectLatest {
                        binding.loading.isVisible = it
                    }
                }

                //ERROR STATE
                launch {
                    viewModel.stateErrorSetMarkup.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong() + 500L)
                            viewModel.onTriggerEvent(DownLineEvent.RemoveHeadMessage)
                        }
                    }
                }

                launch {
                    viewModel.imageQRExpired.collectLatest { imageQr ->
                        binding.labelTimeExpired.isVisible = imageQr.isCounting
                        binding.tvTimeExpired.isVisible = imageQr.isCounting
                        binding.wrapperQrExpired.isVisible = imageQr.currentTickTime == 0L

                        binding.tvTimeExpired.text = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(imageQr.currentTickTime),
                            TimeUnit.MILLISECONDS.toSeconds(imageQr.currentTickTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(imageQr.currentTickTime)))
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}