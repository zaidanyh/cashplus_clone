package com.pasukanlangit.cashplus.ui.pembayaran_pln

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.pasukanlangit.cashplus.databinding.ActivityTopUpPlnBinding

class TopUpPlnActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopUpPlnBinding
    private val titles = arrayOf("Token Listrik", "Tagihan Listrik")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpPlnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            appBar.setOnBackPressed { finish() }
            setPagerAdapter()
            setupTabLayout()

            val position = intent.getIntExtra(KEY_PAGE_POSITION, -1)

            if (position < titles.size && position >= 0) {
                viewPagerPln.currentItem = position
            }
        }
    }

    private fun setPagerAdapter() {
        val plnAdapter = PlnFragmentAdapter(this)
        binding.viewPagerPln.adapter = plnAdapter
        binding.viewPagerPln.isSaveEnabled = false
    }

    private fun setupTabLayout() {
        with(binding) {
            TabLayoutMediator(tabLayoutPln, viewPagerPln) { tab, position ->
                tab.text = titles[position]
            }.attach()
        }
    }

    private inner class PlnFragmentAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        override fun createFragment(position: Int): Fragment =
            when (position) {
                0 ->  TopUpTokenPLNFragment()
                1 ->  TopUpTagihanPLNFragment()
                else -> Fragment()
            }

        override fun getItemCount(): Int = 2
    }

    companion object {
        const val KEY_PAGE_POSITION = "KEY_PAGE_POSITION"
    }
}