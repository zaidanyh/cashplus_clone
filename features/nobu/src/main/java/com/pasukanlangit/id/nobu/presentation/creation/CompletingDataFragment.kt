package com.pasukanlangit.id.nobu.presentation.creation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.pasukanlangit.id.core.CHANGE_PROFILE_PATH
import com.pasukanlangit.id.core.ModuleRoute
import com.pasukanlangit.id.core.PROFILE_KEY_DATA
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.nobu.R
import com.pasukanlangit.id.nobu.databinding.FragmentCompletingDataBinding
import com.pasukanlangit.id.nobu.domain.model.SecurityQuestions
import com.pasukanlangit.id.nobu.domain.temp.AccountCreation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CompletingDataFragment : Fragment() {

    private var _binding: FragmentCompletingDataBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ActivationViewModel by sharedViewModel()

    private val questions = arrayListOf<SecurityQuestions>()
    private lateinit var profileResponse: ProfileResponse
    private var position: Int = 0
    private var stateAnswer: Boolean = false
    private var stateEmail: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompletingDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectQuestionsAndProfile()
        with(binding) {
            edtAnswer.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val input = s.toString()
                    if (input.isEmpty()) {
                        inputAnswer.error = "Jawaban perlu diisi"
                        stateAnswer = false
                        return
                    }
                    inputAnswer.error = null
                    stateAnswer = true
                }
                override fun afterTextChanged(s: Editable?) {
                    btnNext.isEnabled = position != 0 && stateAnswer && stateEmail
                }
            })
            btnInfo.setOnClickListener { AboutIdentityFragment().show(childFragmentManager, null) }
            btnUpdateProfile.setOnClickListener {
                startActivity(
                    ModuleRoute.internalIntent(requireActivity(), CHANGE_PROFILE_PATH).apply {
                        putExtra(PROFILE_KEY_DATA, profileResponse)
                    }
                )
            }
            btnNext.setOnClickListener { onNextClicked() }
        }
    }

    private fun onNextClicked() {
        with(binding) {
            val fullName = tvFullname.text.toString().trim()
            val phone = tvPhone.text.toString().trim()
            val email = tvEmail.text.toString().trim()
            val securityAnswer = edtAnswer.text.toString().trim()
            val securityQuestion = questions[position+1].securityQuestion
            val securityCode = questions[position+1].securityCode

            val account = AccountCreation(phone, email, fullName, securityAnswer, securityQuestion, securityCode)

            viewModel.setStateFirst(true)
            findNavController().navigate(CompletingDataFragmentDirections.completingDataToTermCond(account))
        }
    }

    private fun collectQuestionsAndProfile() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                //STATE QUESTIONS
                launch {
                    viewModel.questions.collectLatest {
                        val listQuestion = arrayListOf("Pilih Pertanyaan")
                        questions.add(SecurityQuestions("", ""))
                        if (!it.isNullOrEmpty()) {

                            it.forEach { question ->
                                listQuestion.add(question.securityQuestion)
                                questions.add(SecurityQuestions(question.securityCode, question.securityQuestion))
                            }
                            questions.toList()

                            val adapter: ArrayAdapter<String> = ArrayAdapter(
                                requireActivity(),
                                R.layout.item_spinner_small_selected, listQuestion
                            )
                            adapter.setDropDownViewResource(R.layout.item_spinner_small)

                            with(binding) {
                                spinnerQuestion.adapter = adapter
                                spinnerQuestion.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parentView: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        btnNext.isEnabled = position != 0 && stateAnswer && stateEmail
                                        this@CompletingDataFragment.position = position
                                    }

                                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                                }
                            }
                        }
                    }
                }

                //STATE PROFILE
                launch {
                    viewModel.profile.collectLatest {
                        if (it != null) {
                            profileResponse = it
                            with(binding) {
                                tvFullname.text = it.full_name
                                tvPhone.text = it.phones?.first()?.phone
                                tvEmail.text = it.email
                                tvEmail.isVisible = it.email.isNotEmpty() || (it.email != "")
                                txtEmailIsEmpty.isVisible = it.email.isEmpty() || (it.email == "")
                                btnUpdateProfile.isVisible = it.email.isEmpty() || (it.email == "")
                                stateEmail = it.email.isNotEmpty() || (it.email != "")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}