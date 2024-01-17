package com.pasukanlangit.cashplus.ui.pembayaran_provider

import android.Manifest
import android.annotation.SuppressLint
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.tabs.TabLayoutMediator
import com.pasukanlangit.cashplus.databinding.ActivityIsiProviderBinding
import com.pasukanlangit.cashplus.ui.pages.home.KEY_PULSA_DAN_DATA_ACTIVE_PAGE
import com.pasukanlangit.cashplus.utils.MyUtils
import com.pasukanlangit.cashplus.view_model.TopupProviderViewModel
import com.pasukanlangit.id.core.utils.CoreUtils.hasPermissions
import com.pasukanlangit.id.core.utils.DrawablePosition
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopUpProviderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIsiProviderBinding
    private val topUpViewModel: TopupProviderViewModel by viewModel()

    private val titlesTab = listOf("Pulsa", "Data", "Telepon", "SMS")

    private val getContactLauncher = registerForActivityResult(ActivityResultContracts.PickContact()) {
        it?.let { contactData ->
            val phone: Cursor? = contentResolver.query(contactData, null, null, null, null)
            if(phone == null){
                Toast.makeText(this@TopUpProviderActivity, "Gagal mendapatkan kontak", Toast.LENGTH_SHORT).show()
            }else{
                try {
                    if (phone.moveToFirst()) {
                        val id: String = phone.getString(phone.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        if (phone.getString(phone.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)).toInt() > 0) {
                            val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)
                            if(phones != null){
                                if(phones.moveToFirst()) {
                                    val phoneNumber = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                    val phoneDigitOnly = phoneNumber.trim().filter { num -> num.isDigit() }
                                    val phoneDigit = if (phoneDigitOnly.substring(0, 2) == "62")
                                        phoneDigitOnly.replaceRange(0, 2, "0")
                                    else phoneDigitOnly
                                    binding.inputNomerPulsa.setText(phoneDigit)
                                    topUpViewModel.setNumberProvider(Pair(phoneDigit, true))
                                }
                            }
                            phones?.close()
                        }
                    }
                }catch (e: Exception){
                    Toast.makeText(this@TopUpProviderActivity, e.message ?: "Unknown Error", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private val permReqLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions.entries.all {
            it.value
        }
        if (granted) {
            showContact()
        }else{
            Toast.makeText(this@TopUpProviderActivity, "Aktifkan izin akses kontak untuk menggunakan fitur ini", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIsiProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            appBar.setOnClickListener {
                finish()
            }

            setPagerAdapter()
            setUpTabLayout()

            var activeTab = intent.getIntExtra(KEY_ACTIVE_VIEWPAGER, 0)
            val prefixInput = intent.getStringExtra(KEY_PREFIX_NUMBER)

            //when navigate using deeplink should send data
            intent.data?.getQueryParameter(KEY_PULSA_DAN_DATA_ACTIVE_PAGE)?.let { pageFromUri ->
                activeTab = pageFromUri.toInt()
            }

            if (activeTab < titlesTab.size && activeTab >= 0) viewPagerAdapter.currentItem = activeTab

            if (!prefixInput.isNullOrEmpty()) inputNomerPulsa.setText(prefixInput)

            inputNomerPulsa.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (inputNomerPulsa.right - inputNomerPulsa.compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                        if (hasPermissions(this@TopUpProviderActivity, arrayOf(Manifest.permission.READ_CONTACTS))) {
                            showContact()
                        }else{
                            permReqLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS))
                        }
                        return@setOnTouchListener true
                    }
                }
                false
            }

            inputNomerPulsa.addTextChangedListener {
                topUpViewModel.setNumberProvider(Pair(it.toString(), false))
            }
        }
    }

    private fun showContact() {
        getContactLauncher.launch(null)
    }

    private fun setUpTabLayout() {
        TabLayoutMediator(
            binding.tabLayout, binding.viewPagerAdapter
        ) { tab, position -> tab.text = titlesTab[position] }.attach()

        setMarginTabs()
    }

    private fun setMarginTabs() {
        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(0, 0, 18, 0)
            tab.requestLayout()
        }
    }

    private fun setPagerAdapter() {
        val providerFragmentAdapter = ProviderFragmentAdapter(this)
        binding.viewPagerAdapter.isUserInputEnabled = false
        binding.viewPagerAdapter.adapter = providerFragmentAdapter
    }

    fun setOperatorLogo(imgUrl: String?) {
        Glide.with(this)
            .load(imgUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(binding.ivOperator)
        binding.ivOperator.isVisible = !imgUrl.isNullOrEmpty()
    }

    private inner class ProviderFragmentAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        override fun createFragment(position: Int): Fragment =
            when (position) {
                0 -> TopupPulsaFragment()
                1 -> TopupDataFragment()
                2 -> TopUpTeleponFragment()
                3 -> TopUpSmsFragment()
                else -> Fragment()
            }

        override fun getItemCount(): Int = titlesTab.size
    }

    companion object {
        const val KEY_ACTIVE_VIEWPAGER = "KEY_ACTIVE_VIEWPAGER"
        const val KEY_PREFIX_NUMBER = "KEY_PREFIX_NUMBER"
    }
}