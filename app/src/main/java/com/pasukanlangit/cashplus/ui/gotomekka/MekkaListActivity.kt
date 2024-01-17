package com.pasukanlangit.cashplus.ui.gotomekka

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.databinding.ActivityMekkaListBinding
import com.pasukanlangit.cashplus.utils.MyUtils

class MekkaListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMekkaListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMekkaListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnBack.setOnClickListener { finish() }

            rvMenu.layoutManager = LinearLayoutManager(this@MekkaListActivity, LinearLayoutManager.HORIZONTAL,false)
            rvMenu.adapter = MekkaMenuAdapter(getMekkaMenu())

            rvList.layoutManager = LinearLayoutManager(this@MekkaListActivity)
            rvList.adapter = MekkaAdapter(
                listOf(
                    R.drawable.mekka_1,
                    R.drawable.mekka_2,
                    R.drawable.mekka_3
                )
            ){
                MyUtils.showCoomingSon(supportFragmentManager)
            }
        }
    }
}