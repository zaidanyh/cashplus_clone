package com.pasukanlangit.cashplus.ui.pembayaran_game_menu

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.pasukanlangit.cashplus.databinding.ActivityGameMenuBinding
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.utils.Status
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.utils.CategoryProduct
import com.pasukanlangit.id.core.utils.DrawablePosition
import com.pasukanlangit.id.core.utils.GridSpacingItemDecoration
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.library_core.domain.model.NotifType
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameMenuBinding
    private val viewModel: GameMenuViewModel by viewModel()
    private val sharedPref: SharedPrefDataSource by inject()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uuid = sharedPref.getUUID()
        val accessToken = sharedPref.getAccessToken()

        val request = ProductRequest(
            uuid = uuid ?: "",
            category = CategoryProduct.GAMES.value,
            kode_provider = "",
            is_faktur = "",
            is_active = "1"
        )
        viewModel.getMenu(request, accessToken ?: "")

        with(binding) {
            appBar.setOnClickListener { finish() }
            edtSearch.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (edtSearch.right - edtSearch.compoundDrawables[DrawablePosition.DRAWABLE_RIGHT].bounds.width())) {
                        val keyword = edtSearch.text.toString().trim()
                        if (keyword.isNotEmpty()) {
                            viewModel.searchVoucherGame(keyword)
                            wrapperGameSearch.isVisible = true
                            wrapperGameMenu.isVisible = false
                            rvGameSearchShimmer.isVisible = true
                            rvGameSearchShimmer.startShimmer()
                            rvGameSearch.isVisible = false

                            KeyboardUtil.closeKeyboardDialog(this@GameMenuActivity, edtSearch)
                        }
                        return@setOnTouchListener true
                    }
                }
                false
            }
        }

        observeGameMenu()
        observeSearchGame()
        setUpRecyclerGrid()
    }

    private fun setUpRecyclerGrid() {
        with(binding.rvGamePopular) {
            layoutManager = GridLayoutManager(this@GameMenuActivity,3)
            addItemDecoration(GridSpacingItemDecoration(10))
        }

        with(binding.rvGameOthers) {
            layoutManager = GridLayoutManager(this@GameMenuActivity, 3)
            addItemDecoration(GridSpacingItemDecoration(10))
        }

        with(binding.rvGameSearch) {
            layoutManager = GridLayoutManager(this@GameMenuActivity,3)
            addItemDecoration(GridSpacingItemDecoration(10))
        }
    }

    private fun observeGameMenu() {
        viewModel.menuGames.observe(this@GameMenuActivity) {
            when (it.status) {
                Status.SUCCESS -> {
                    with(binding) {
                        rvGamePopularShimmer.isVisible = false
                        rvGamePopularShimmer.stopShimmer()
                        rvGamePopular.isInvisible = false

                        rvGameOthersShimmer.isVisible = false
                        rvGameOthersShimmer.stopShimmer()
                        rvGameOthers.isInvisible = false

                        val menus = it.data?.data?.distinctBy { prod -> prod.kode_provider }
                        menus?.let { products ->
                            rvGameOthers.adapter = GridMenuGameAdapter(products) { providerCode ->
                                startActivity(
                                    Intent(this@GameMenuActivity, DetailGameMenuActivity::class.java).apply {
                                        putExtra(DetailGameMenuActivity.PROVIDER_CODE_KEY, providerCode)
                                    }
                                )
                            }
                        }

                        val populerKeyword = listOf(
                            "MOBILELEGENDS",
                            "FREE FIRE",
                            "ROBLOX",
                            "FIFA",
                            "PUBG MOBILE"
                        )

                        val menusPopular = menus?.filter { this_menus ->
                            this_menus.short_dsc.uppercase()
                                .contains(Regex("\\b(?:${populerKeyword.joinToString(separator = "|")})\\b"))
                        }
                        menusPopular?.let { menusP ->
                            rvGamePopular.adapter = GridMenuGameAdapter(menusP) { providerCode ->
                                startActivity(
                                    Intent(this@GameMenuActivity, DetailGameMenuActivity::class.java).apply {
                                        putExtra(DetailGameMenuActivity.PROVIDER_CODE_KEY, providerCode)
                                    }
                                )
                            }
                        }
                    }
                }
                Status.LOADING -> {
                    with(binding) {
                        rvGamePopularShimmer.isVisible = true
                        rvGamePopularShimmer.startShimmer()
                        rvGamePopular.isInvisible = true

                        rvGameOthersShimmer.isVisible = true
                        rvGameOthersShimmer.startShimmer()
                        rvGameOthers.isInvisible = true
                    }
                }
                Status.ERROR -> {
                    with(binding) {
                        rvGamePopularShimmer.isVisible = false
                        rvGamePopularShimmer.stopShimmer()

                        rvGameOthersShimmer.isVisible = false
                        rvGameOthersShimmer.stopShimmer()
                    }

                    val menusAllFragment = ButtomSheetNotif(it.message, NotifType.NOTIF_ERROR)
                    menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)
                }
            }
        }
    }

    private fun observeSearchGame() {
        viewModel.gamesSearch.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    with(binding) {
                        rvGameSearchShimmer.isVisible = false
                        rvGameSearchShimmer.stopShimmer()
                        rvGameSearch.isVisible = true

                        val menus = it.data?.distinctBy { prod -> prod.kode_provider }

                        if (menus.isNullOrEmpty()) {
                            edtSearch.text?.clear()
                            val menusAllFragment = ButtomSheetNotif(
                                "Maaf voucher game yang anda cari tidak ada dalam sistem kami.",
                                NotifType.NOTIF_INPUT_UNCOMPLETED
                            )
                            menusAllFragment.show(supportFragmentManager, menusAllFragment.tag)

                            wrapperGameSearch.isVisible = false
                            wrapperGameMenu.isVisible = true
                        } else {
                            menus.let { products ->
                                binding.rvGameSearch.adapter = GridMenuGameAdapter(products) { providerCode ->
                                    startActivity(
                                        Intent(this@GameMenuActivity, DetailGameMenuActivity::class.java).apply {
                                            putExtra(DetailGameMenuActivity.PROVIDER_CODE_KEY, providerCode)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Status.LOADING -> {}
                Status.ERROR -> {}
            }
        }
    }
}