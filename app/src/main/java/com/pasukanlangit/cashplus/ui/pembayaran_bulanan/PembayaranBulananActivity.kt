package com.pasukanlangit.cashplus.ui.pembayaran_bulanan

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityPembayaranBulananBinding

class PembayaranBulananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPembayaranBulananBinding
    private val titleCode = arrayOf(
        "Pasca Bayar", "BPJS", "Pajak Bumi & Bangunan", "TELKOM", "PDAM", "Tv Kabel", "SAMSAT", "Multifinance",
        "Kartu Kredit", "Asuransi", "Gas Pertamina", "Gas Negara"
    )
    private val iconCode = intArrayOf(
        R.drawable.ic_tab_menu_pasca_bayar, R.drawable.ic_tab_menu_bpjs, R.drawable.ic_tab_menu_pbb,
        R.drawable.ic_tab_menu_tellkom, R.drawable.ic_tab_menu_pdam, R.drawable.ic_tab_menu_tv_kabel,
        R.drawable.ic_tab_menu_samsat, R.drawable.ic_tab_menu_multi_finance, R.drawable.ic_tab_menu_kartu_kredit,
        R.drawable.ic_tab_menu_asuransi, R.drawable.ic_tab_menu_gas_pertamina, R.drawable.ic_tab_menu_gas_negara
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranBulananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appBar.setOnClickListener { finish() }

        setPagerAdapter()
        setupTabLayout()

        val positionActive = intent.getIntExtra(KEY_POSITION, -1)

        if(positionActive < titleCode.size && positionActive >= 0){
            binding.viewPagerBulanan.currentItem = positionActive
        }
    }

    private fun setMarginTab(){
        for (i in 0 until binding.tabLayoutPembayaranBulanan.tabCount) {
            val tab = (binding.tabLayoutPembayaranBulanan.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams

            p.setMargins(if (i == 0) 18 else 0, 12, 18, 16)
            tab.requestLayout()
        }
    }

    private fun setupTabLayout() {
        TabLayoutMediator(
            binding.tabLayoutPembayaranBulanan,
            binding.viewPagerBulanan
        ) { tab, position ->
            tab.icon = ContextCompat.getDrawable(this, iconCode[position])
            tab.text = titleCode[position]
        }.attach()

        setMarginTab()
    }

    private fun setPagerAdapter() {
        val bulananPagesAdapter = BulananFragmentAdapter(this)
        binding.viewPagerBulanan.adapter = bulananPagesAdapter
    }

    private inner class BulananFragmentAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        override fun createFragment(position: Int): Fragment =
            when (position) {
                0 -> PembayaranPascaBayarFragment()
                1 -> PembayaranBPJSFragment()
                2 -> PembayaranPBBFragment()
                3 -> PembayaranTelkomFragment()
                4 -> PembayaranPDAMFragment()
                5 -> PembayaranTvFragment()
                6 -> PembayaranSamsatFragment()
                7 -> PembayaranMultiFinanceFragment()
                8 -> PembayaranKreditFragment()
                9 -> PembayaranAsuransiFragment()
                10 -> PembayaranGasPertaminaFragment()
                11 -> PembayaranGasNegaraFragment()
                else -> Fragment()
            }

        override fun getItemCount(): Int = titleCode.size
    }

    companion object {
        const val KEY_POSITION = "KEY_POSITION"
    }
}