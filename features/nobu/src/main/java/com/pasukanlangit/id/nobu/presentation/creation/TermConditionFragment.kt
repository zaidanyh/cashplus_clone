package com.pasukanlangit.id.nobu.presentation.creation

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.razir.progressbutton.hideProgress
import com.pasukanlangit.id.core.utils.setUpToProgressButton
import com.pasukanlangit.id.core.utils.showLoading
import com.pasukanlangit.id.nobu.R
import com.pasukanlangit.id.nobu.databinding.FragmentTermConditionBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TermConditionFragment : Fragment() {

    private var _binding: FragmentTermConditionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ActivationViewModel by sharedViewModel()
    private val args: TermConditionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermConditionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectTermCondition()
        with(binding) {
            readMore.setOnClickListener {
                tvTermCondition.setOnClickListener()
                readMore.isVisible = false
                changeHeight()
            }
            btnAgree.setUpToProgressButton(viewLifecycleOwner)
            btnAgree.setOnClickListener {
                viewModel.nobuAccountCreation(args.completingToTerm)
                observeCreation()
            }
        }
    }

    private fun collectTermCondition() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //STATE TERM CONDITION
                launch {
                    viewModel.termCondition.collectLatest {
                        if (it != null) {
                            with(binding.tvTermCondition) {
                                text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                    Html.fromHtml(it.textHtml, Html.FROM_HTML_MODE_LEGACY)
                                else Html.fromHtml(it.textHtml)
                                setTrimLength(930)
                            }
                        }
                    }
                }
                //STATE LOADING
                launch {
                    viewModel.stateLoading.collectLatest {
                        binding.progressBar.isVisible = it
                    }
                }
                //STATE ERROR
                launch {
                    viewModel.stateError.collectLatest { message ->
                        if (message.isNotEmpty()) {
                            message.peek()?.let { info ->
                                val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong() + 500L)
                                viewModel.removeHeadQueue()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun changeHeight() {
        with(binding) {
            val constraint = ConstraintSet()
            constraint.clone(root)
            constraint.constrainHeight(wrapperTermCondition.id, ConstraintSet.MATCH_CONSTRAINT_SPREAD)
            constraint.applyTo(root)
        }
    }

    private fun observeCreation() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //STATE ACCOUNT CREATION
                launch {
                    viewModel.accountCreation.collectLatest { response ->
                        if (response != null) {
                            viewModel.setStateSecond(true)
                            findNavController().navigate(TermConditionFragmentDirections.termConditionToOTP())
                        }
                    }
                }
                //STATE LOADING
                launch {
                    viewModel.stateLoading.collectLatest {
                        if (it) binding.btnAgree.showLoading()
                        else binding.btnAgree.hideProgress(getString(R.string.term_agree))
                    }
                }
                //STATE ERROR
                launch {
                    viewModel.stateError.collectLatest { message ->
                        message.peek()?.let { info ->
                            val toast = Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong() + 500L)
                            viewModel.removeHeadQueue()
                        }
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