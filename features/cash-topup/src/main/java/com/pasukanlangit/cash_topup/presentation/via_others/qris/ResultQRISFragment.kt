package com.pasukanlangit.cash_topup.presentation.via_others.qris

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.pasukanlangit.cash_topup.R
import com.pasukanlangit.cash_topup.databinding.FragmentResultQRISBinding
import com.pasukanlangit.cash_topup.presentation.InitialTopUpActivity
import com.pasukanlangit.id.core.utils.CoreUtils
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class ResultQRISFragment : Fragment() {

    private var _binding: FragmentResultQRISBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ViaQRISViewModel by viewModel()

    private var countdownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultQRISBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val totalAmount = arguments?.getInt(TOTAL_NOMINAL_TOP_UP)
        val qrCodeUrl = arguments?.getString(QR_CODE_RESULT_KEY)
        initCountDownTimer()
        loadImage(qrCodeUrl.toString())
        with(binding) {
            tvNominalTopUp.text = getString(R.string.nominal_top_up_qris_format, getNumberRupiah(totalAmount))
            btnGenerateNewQr.setOnClickListener {
                viewModel.onTriggerEvent(ViaQRISEvent.TopUpViaQRIS(amount = totalAmount.toString()))
            }
            btnSaveImage.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveImage(requireActivity().window.decorView.rootView)
                    return@setOnClickListener
                }
                if (
                    ActivityCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    saveImage(requireActivity().window.decorView.rootView)
                    return@setOnClickListener
                }
                (activity as InitialTopUpActivity).permissionAccessStorage.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
        collectDataQRISResult()
    }

    private fun initCountDownTimer() {
        countdownTimer = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val durationSeconds = millisUntilFinished / 1000
                binding.tvTimeExpired.text = String.format(
                    "%02d:%02d",
                    (durationSeconds % 3600) / 60,
                    (durationSeconds % 60)
                )
            }

            override fun onFinish() {
                with(binding) {
                    tvStateExpired.text = getString(R.string.qr_code_is_expired)
                    tvTimeExpired.isVisible = false
                    wrapperExpired.isVisible = true
                    btnGenerateNewQr.isEnabled = true
                    btnSaveImage.isEnabled = false
                }
            }
        }
    }

    private fun loadImage(source: String) {
        Glide.with(requireContext())
            .load(source)
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    with(binding) {
                        countdownTimer?.cancel()
                        tvStateExpired.text = getString(R.string.generate_qr_code_failed)
                        tvTimeExpired.isVisible = false
                        btnGenerateNewQr.isEnabled = true
                        btnSaveImage.isEnabled = false
                    }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.btnSaveImage.isEnabled = true
                    countdownTimer?.start()
                    return false
                }
            })
            .error(R.drawable.ic_empty)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(binding.imgQrCode)
    }

    private fun collectDataQRISResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.viaQRISLoading.collectLatest {
                        with(binding) {
                            progressLoading.isVisible = it
                            btnGenerateNewQr.isEnabled = false
                        }
                    }
                }
                launch {
                    viewModel.viaQRIS.collectLatest { response ->
                        if (response != null) {
                            loadImage(response.qrUrl)
                            with(binding) {
                                tvStateExpired.text = getString(R.string.qr_code_expired_in)
                                wrapperExpired.isVisible = false
                                tvTimeExpired.isVisible = true
                                btnGenerateNewQr.isEnabled = false
                            }
                        }
                    }
                }
                launch {
                    viewModel.viaQRISError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onTriggerEvent(ViaQRISEvent.RemoveTopUpViaQRISError)
                        }
                    }
                }
            }
        }
    }

    fun saveImage(view: View) {
        val contentResolver = requireContext().contentResolver
        val images: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "images/*")
        }

        val uri = contentResolver.insert(images, contentValues)
        try {
            val bitmap = CoreUtils.getBitmapFromView(view)

            val outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri as Uri))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            Objects.requireNonNull(outputStream)

            Toast.makeText(requireContext(), getString(R.string.qr_code_saved), Toast.LENGTH_SHORT).show()
            binding.btnSaveImage.isEnabled = false
            countdownTimer?.onFinish()
        } catch (e: Exception) {
            Log.d("QR Save", e.message.toString())
            Toast.makeText(requireContext(), getString(R.string.qr_code_not_saved), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownTimer?.cancel()
        countdownTimer = null
        _binding = null
    }

    companion object {
        const val TOTAL_NOMINAL_TOP_UP = "total_nominal_top_up"
        const val QR_CODE_RESULT_KEY = "qr_code_result_key"
    }
}