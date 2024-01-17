package com.pasukanlangit.cashplus.ui.pages.home

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.location.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.MainActivityNavComp
import com.pasukanlangit.cashplus.PageMenu
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.adapter.*
import com.pasukanlangit.cashplus.databinding.FragmentHomeBinding
import com.pasukanlangit.cashplus.model.request_body.UpdateLatLongRequest
import com.pasukanlangit.cashplus.ui.all_menus.AllMenusProduct
import com.pasukanlangit.cashplus.ui.ewallet.EwalletListActivity
import com.pasukanlangit.cashplus.ui.gotomekka.MekkaListActivity
import com.pasukanlangit.cashplus.ui.info_promo.DetailPromoActivity
import com.pasukanlangit.cashplus.ui.info_promo.InfoPromoListActivity
import com.pasukanlangit.cashplus.ui.injectvoucher.InjectVoucherActivity
import com.pasukanlangit.cashplus.ui.login.LoginActivity
import com.pasukanlangit.cashplus.ui.omni.EnterNumOmniActivity
import com.pasukanlangit.cashplus.ui.pages.others.settings.profile.ChangeProfilActivity
import com.pasukanlangit.cashplus.ui.pembayaran_bulanan.PembayaranBulananActivity
import com.pasukanlangit.cashplus.ui.pembayaran_game_menu.GameMenuActivity
import com.pasukanlangit.cashplus.ui.pembayaran_pln.TopUpPlnActivity
import com.pasukanlangit.cashplus.ui.pembayaran_provider.TopUpProviderActivity
import com.pasukanlangit.cashplus.utils.*
import com.pasukanlangit.cashplus.utils.MyUtils.showCoomingSon
import com.pasukanlangit.cashplus.utils.model.HandlingFlipAccept
import com.pasukanlangit.cashplus.utils.model.HandlingGlobalBank
import com.pasukanlangit.id.core.*
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.CenterZoomLinearLayoutManager
import com.pasukanlangit.id.ui_cashgold_navigation.DashboardGoldActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModel()
    private val sharedPrefDataSource : SharedPrefDataSource by inject()

    private lateinit var locationRequest: LocationRequest.Builder
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationProviderClient : FusedLocationProviderClient
    private lateinit var bannerAdapter: BannerAdapter

    private var animBalanceScroll: AnimatorSet? = null
    private var balanceAdapter = lazy { BalanceAdapter() }
//    private var hotelCityName: String? = ""
    private var stateIsBinded: Boolean = false
    private var balanceCashplus: Double? = 0.0
    private var stateDisableFBanks: Boolean = false
    private var stateIsFlipAccept: Boolean = false
//    private var fullAddress: String? = null

    private var token: String? = null
    private var uuid: String? = null

    private val sliderHandler = Handler(Looper.getMainLooper())
    private val sliderRunnable = Runnable {
        binding.viewPager2Banner.currentItem = binding.viewPager2Banner.currentItem + 1
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                getCurrentLocation()
            }else{
                Toast.makeText(requireContext(), getString(R.string.provide_request_permission_location), Toast.LENGTH_SHORT).show()
            }
        }
    private val turnOnGPS = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        getCurrentLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = sharedPrefDataSource.getAccessToken()
        uuid = sharedPrefDataSource.getUUID()

        getCurrentLocation()
        startAnimationService()
        setUpBalance()
        setUpBannerCashplus()
        setUpRecyclerBannerCashGold()
        setUpRecyclerBannerGotomekka()

        if (!token.isNullOrEmpty() && !uuid.isNullOrEmpty()) {
            val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10)
                .build()
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
            mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val gson = GsonBuilder().create()
                    val handlingFBanks = mFirebaseRemoteConfig.getString("handlings")
                    val handlingFlipAccept = mFirebaseRemoteConfig.getString("handlings_flip_accept")

                    val isAvailableFBank = gson.fromJson(handlingFBanks, HandlingGlobalBank::class.java)
                    val isFlipAccept = gson.fromJson(handlingFlipAccept, HandlingFlipAccept::class.java)

                    val handling = HandlingGlobalBank(disableGlobalBank = isAvailableFBank.disableGlobalBank)
                    stateDisableFBanks = handling.disableGlobalBank
                    val handling2 = HandlingFlipAccept(isFlipAccept = isFlipAccept.isFlipAccept)
                    stateIsFlipAccept = handling2.isFlipAccept
                }
            }
            viewModel.getBanner()
            viewModel.getBannerCashGold()
            viewModel.getBannerGotomekka()

            with(binding) {
                swiperefreshHome.setOnRefreshListener {
                    viewModel.getBalanceAccount(uuid, token)
                    viewModel.getBanner()
                    startAnimationService()
                    swiperefreshHome.isRefreshing = false
                }
                ivProfile.setOnClickListener { (activity as MainActivityNavComp).navigateToPage(PageMenu.OTHERS) }

                btnTopUpBalance.setOnClickListener {
                    ChooseTopUpFragment.newInstance(
                        isBinded = stateIsBinded, isFlipAccept = stateIsFlipAccept, balance = balanceCashplus!!
                    ).show(requireActivity().supportFragmentManager, "Choose Top UP")
                }
                tvTransfer.text = getString(R.string.cash_transfer).replace(" ", "\n")
                btnTransfer.setOnClickListener {
                    ChooseTransferFragment.newInstance(stateDisableFBanks).show(childFragmentManager, null)
                }

                btnMenuPulsa.setOnClickListener {
                    val intent = Intent(activity, TopUpProviderActivity::class.java)
                    intent.putExtra(TopUpProviderActivity.KEY_ACTIVE_VIEWPAGER, 0)
                    startActivity(intent)
                }

                btnMenuData.setOnClickListener {
                    val intent = Intent(activity, TopUpProviderActivity::class.java)
                    intent.putExtra(TopUpProviderActivity.KEY_ACTIVE_VIEWPAGER, 1)
                    startActivity(intent)
                }

                btnMenuPembayaran.setOnClickListener {
                    startActivity(Intent(activity, PembayaranBulananActivity::class.java))
                }

                btnMenuTokenpln.setOnClickListener {
                    startActivity(Intent(activity, TopUpPlnActivity::class.java))
                }

                btnMenuEwallet.setOnClickListener {
                    startActivity(
                        Intent(requireContext(), EwalletListActivity::class.java)
                    )
                }

                btnMenuInjectVoucher.setOnClickListener {
                    startActivity(
                        Intent(activity, InjectVoucherActivity::class.java)
                    )
                }

                btnMenuGame.setOnClickListener {
                    val intent = Intent(activity, GameMenuActivity::class.java)
                    startActivity(intent)
                }

                btnMenuOmni.setOnClickListener {
                    startActivity(
                        Intent(requireContext(), EnterNumOmniActivity::class.java)
                    )
                }

                btnMenuTagihanListrik.setOnClickListener {
                    val intent = Intent(activity, TopUpPlnActivity::class.java)
                    intent.putExtra(TopUpPlnActivity.KEY_PAGE_POSITION, 1)
                    startActivity(intent)
                }

                btnMenuLainnya.setOnClickListener {
                    startActivity(Intent(activity, AllMenusProduct::class.java))
                }

                btnPromoLainnya.setOnClickListener {
                    startActivity(Intent(activity, InfoPromoListActivity::class.java))
                }

                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        val mCurrentLocation = locationResult.lastLocation
                        mCurrentLocation?.let { latLong ->
                            viewModel.updateLatLong(
                                UpdateLatLongRequest(
                                    uuid = uuid ?: "",
                                    lat = latLong.latitude,
                                    long = latLong.longitude
                                ),
                                token ?: ""
                            )
                        }
                    }
                }
            }
        } else {
            activity?.finishAffinity()
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        collectBalance()
        observeBanners()
        observeCashGoldBanners()
        observeGotoMakkahBanners()
    }

    private fun getCurrentLocation() {
        FirebaseCrashlytics.getInstance().log("user get location")
        locationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL_IN_MILLISECONDS)

        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permReqLauncher.launch(PERMISSION_LOCATION)
            return
        }
        buildAlertMessageNoGps()
        locationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                val latitude = location?.latitude
                val longitude = location?.longitude
                if (latitude == null || longitude == null) return@addOnSuccessListener
                viewModel.updateLatLong(
                    UpdateLatLongRequest(uuid = uuid ?: "", lat = latitude, long = longitude),
                    token = token?: ""
                )
            }
            .addOnFailureListener { exception ->
                locationProviderClient.requestLocationUpdates(
                    locationRequest.build(),
                    locationCallback,
                    Looper.getMainLooper()
                )
                Log.d("Location on failure", exception.message.toString())
                FirebaseCrashlytics.getInstance().log(exception.message.toString())
            }
    }
//
//    private fun getCityByLatLong(lat: Double, long: Double) {
//        try {
//            val geoCoder = Geocoder(requireContext(), Locale.getDefault())
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                geoCoder.getFromLocation(lat, long, 1) { addresses ->
//                    fullAddress = addresses.first().getAddressLine(0)
//                    val fullAddressSplit = fullAddress?.split(",")
//                    hotelCityName = findCityName(fullAddressSplit)
//                    Log.d("Kota/Kabupaten", hotelCityName.toString())
//                }
//            } else {
//                val addresses = geoCoder.getFromLocation(lat, long, 1)
//                fullAddress = addresses?.getOrNull(0)?.getAddressLine(0)
//                val fullAddressSplit = fullAddress?.split(",")
//                hotelCityName = findCityName(fullAddressSplit)
//                Log.d("Kota/Kabupaten", hotelCityName.toString())
//            }
//        } catch (exception: Exception) {
//            hotelCityName = "Bali"
//            FirebaseCrashlytics.getInstance().log(exception.message.toString())
//        }
//    }
//
//    private fun findCityName(list: List<String>?): String {
//        var findCity: String? = ""
//        list?.forEach { addressSplitted ->
//            addressSplitted.trim().split(" ").forEach { city ->
//                if(city.isNotEmpty() &&
//                    city.contains("kota", ignoreCase = true) ||
//                    city.contains("kabupaten", ignoreCase = true)
//                ) {
//                    val cityWithoutPrefix = addressSplitted.lowercase().replace("^(kota|kabupaten)".toRegex(), "").trim()
//                    val cityWithoutPrefixSplitted = cityWithoutPrefix.split(" ")
//                    if(cityWithoutPrefixSplitted.isNotEmpty()) {
//                        findCity = cityWithoutPrefixSplitted.last()
//                    }
//                }
//            }
//        }
//        return findCity ?: "Bali"
//    }
//
    private fun buildAlertMessageNoGps() {
        val manager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            GenericModalDialogCashplus.Builder()
                .title(getString(R.string.notification))
                .image(R.drawable.ilustration_warning)
                .description(getString(R.string.location_is_disabled))
                .buttonPositive(
                    PositiveButtonAction(
                        btnLabel = getString(R.string.yes),
                        onBtnClicked = {
                            turnOnGPS.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        }
                    )
                )
                .buttonNegative(
                    NegativeButtonAction(
                        btnLabel = getString(R.string.no),
                        setClickOnDismiss = true
                    ))
                .show(childFragmentManager, "Check Location")
            return
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpBalance() {
        binding.rvBalance.apply {
            adapter = balanceAdapter.value
            layoutManager = CenterZoomLinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setOnTouchListener { _, _ ->
                animBalanceScroll?.cancel()
                false
            }
        }

        balanceAdapter.value.setOnButtonClickListener(object: BalanceAdapter.BindingAccountClickListener {
            override fun onDetailNobuFeatures() {
                showCoomingSon(requireActivity().supportFragmentManager)
//                activity?.startActivity(
//                    ModuleRoute.internalIntent(requireActivity(), NOBU_HISTORY_PATH)
//                )
            }

            override fun onBindingAcountClicked() {
                showCoomingSon(requireActivity().supportFragmentManager)
//                activity?.startActivity(
//                    ModuleRoute.internalIntent(requireActivity(), NOBU_PATH)
//                )
            }
        })
    }

    private fun setUpBannerCashplus() {
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(20))
        bannerAdapter = BannerAdapter { banner, animView ->
            //create shared animation
            val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                    (activity as MainActivityNavComp),
                    *animView
                )
            startActivity(
                Intent(activity, DetailPromoActivity::class.java).apply {
                    putExtra(DetailPromoActivity.KEY_BANNER, banner)
                },
                optionsCompat.toBundle()
            )
        }
        with(binding.viewPager2Banner) {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            setPageTransformer(compositePageTransformer)
            adapter = bannerAdapter
        }
    }

    private fun setUpRecyclerBannerCashGold() {
        binding.rvBannerCashgold.layoutManager =  LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    private fun setUpRecyclerBannerGotomekka() {
        binding.rvGotomekka.layoutManager =  LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    private fun startAnimationService() {
        var offsetAnimation : Long = 0

        with(binding) {
            arrayOf(
                wrapperMenuPulsa,
                wrapperMenuData,
                wrapperMenuPembayaran,
                wrapperMenuTokenpln,
                wrapperMenuEwallet,
                wrapperMenuInjectVoucher,
                wrapperMenuGame,
                wrapperMenuOmni,
                wrapperMenuTagihanListrik,
                wrapperMenuLainnya
            ).forEach { btn ->
                val animation = AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.translate_fade_in_anim
                )
                animation.startOffset = offsetAnimation

                btn.startAnimation(animation)

                offsetAnimation += 60
            }
        }
    }

//    private fun observeCurrentCity() {
//        viewModel.currentCityUser.observe(viewLifecycleOwner) {
//            if (token != null && uuid != null) {
//
//                val nameUser = sharedPrefDataSource.getNameProfile()
//                FirebaseCrashlytics.getInstance().setCustomKey("location", "$nameUser = $it")
//
//                if (!it.isNullOrEmpty()) {
//                    viewModel.getHotelListByCity(
//                        HotelListByCityRequest(
//                            uuid ?: "", "",
//                            it, "", "", fullAddress
//                        ), token ?: ""
//                    )
//                }
//            }
//        }
//    }

    private fun collectBalance() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.stateError.collectLatest { message ->
                        if (message.isNotEmpty()) {
                            binding.shimmerBalance.stopShimmer()
                            binding.shimmerImgProfile.stopShimmer()
                            message.peek()?.let { info ->
                                val toast =
                                    Toast.makeText(requireContext(), info, Toast.LENGTH_SHORT)
                                toast.show()
                                delay(toast.duration.toLong())
                                viewModel.removeHeadQueue()
                            }
                        }
                    }
                }
                launch {
                    viewModel.balanceAccount.collectLatest { response ->
                        if (response != null) {
                            val profile = response.profileResponse
                            val stateBinding = response.itemBalance.last().stateBalance
                            stateIsBinded = stateBinding
                            balanceCashplus = response.itemBalance.first().balance
                            (activity as MainActivityNavComp).setStateBinded(stateBinding)

                            with(sharedPrefDataSource) {
                                if (getPhoneNumber().isNullOrEmpty() && getNameProfile().isNullOrEmpty()) {
                                    setPhoneNumber(profile.phones?.first()?.phone.toString())
                                    setNameProfile(profile.full_name)
                                }
                                else if (getNameProfile() != profile.full_name)
                                    setNameProfile(profile.full_name)

                                if (profile.email.isNotEmpty() || getEmailProfile() != profile.email)
                                    setEmailProfile(profile.email)
                            }

                            var imgProfile = profile.img_url
                            if (imgProfile.isEmpty()) imgProfile =
                                "https://ui-avatars.com/api/?name=${profile.full_name}&size=300&rounded=true&background=FFFFFF&color=0A57FF&bold=true"

                            Glide.with(requireContext())
                                .load(imgProfile)
                                .error(R.drawable.ic_empty)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(binding.ivProfile)

                            balanceAdapter.value.submitList(response.itemBalance)
                            if (binding.rvBalance.onFlingListener == null)
                                binding.arIndicator.attachTo(binding.rvBalance, true)

                            animateBalance()
                            stateEmailNTTL(response.profileResponse)
                        }
                    }
                }
                launch {
                    viewModel.stateLoading.collectLatest {
                        (activity as MainActivityNavComp).setStateIsLoading(it)
                        with(binding) {
                            ivProfile.isInvisible = it
                            shimmerImgProfile.isVisible = it
                            shimmerBalance.isVisible = it
                            rvBalance.isInvisible = it
                            if (it) {
                                shimmerImgProfile.startShimmer()
                                shimmerBalance.startShimmer()
                                return@collectLatest
                            }
                            shimmerImgProfile.stopShimmer()
                            shimmerBalance.stopShimmer()
                        }
                    }
                }
            }
        }
    }

    private fun observeBanners() {
        viewModel.banner.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    with(binding) {
                        viewPager2Banner.isInvisible = false
                        viewPager2BannerShimmer.isVisible = false
                        viewPager2BannerShimmer.stopShimmer()

                        if (it.data != null) {
                            bannerAdapter.setDataBanner(it.data, viewPager2Banner)

                            viewPager2Banner.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {

                                override fun onPageSelected(position: Int) {
                                    super.onPageSelected(position)
                                    sliderHandler.removeCallbacks(sliderRunnable)
                                    sliderHandler.postDelayed(sliderRunnable, 2200L)
                                }
                            })
                        }
                        bannerAdapter.notifyDataSetChanged()
                    }
                }
                Status.LOADING -> {
                    with(binding) {
                        viewPager2Banner.isInvisible = true
                        viewPager2BannerShimmer.isVisible = true
                        viewPager2BannerShimmer.startShimmer()
                    }
                }
                Status.ERROR -> {
                    with(binding) {
                        viewPager2Banner.isInvisible = true
                        viewPager2BannerShimmer.isVisible = true
                        viewPager2BannerShimmer.startShimmer()
                    }
                }
            }
        }
    }

//    private fun observeHotels() {
//        viewModel.hotelList.observe(viewLifecycleOwner) { response ->
//            with(binding) {
//                when (response.status) {
//                    Status.SUCCESS -> {
//                        rvHotelHome.isVisible = true
//                        rvHotelHomeShimmer.isVisible = false
//                        rvHotelHomeShimmer.stopShimmer()
//
//                        if (response.data != null) {
//                            rvHotelHome.adapter = response.data.data?.let { HomeHotelAdapter(it) }
//                        }
//                    }
//                    Status.LOADING -> {
//                        rvHotelHome.isVisible = false
//                        rvHotelHomeShimmer.isVisible = true
//                        rvHotelHomeShimmer.startShimmer()
//                    }
//                    Status.ERROR -> {
//                        rvHotelHome.isVisible = false
//                        rvHotelHomeShimmer.isVisible = true
//                        rvHotelHomeShimmer.startShimmer()
//                    }
//                }
//            }
//        }
//    }

//    private fun observeProducts() {
//        viewModel.products.observe(viewLifecycleOwner) {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    with(binding) {
//                        rvProdukHome.isVisible = true
//                        rvProductHomeShimmer.isVisible = false
//                        rvProductHomeShimmer.stopShimmer()
//
//                        if (it.data != null) {
//                            rvProdukHome.adapter = ProductHomeAdapter(it.data.data) { product ->
//                                val intent = Intent(activity, ProductDetailActivity::class.java)
//                                intent.putExtra(ProductDetailActivity.KEY_IMG_PRODUCT, product)
//                                startActivity(intent)
//                            }
//                        }
//                    }
//                }
//                Status.LOADING -> {
//                    with(binding) {
//                        rvProdukHome.isVisible = false
//                        rvProductHomeShimmer.isVisible = true
//                        rvProductHomeShimmer.startShimmer()
//                    }
//                }
//                Status.ERROR -> {
//                    with(binding) {
//                        rvProdukHome.isVisible = true
//                        rvProductHomeShimmer.isVisible = false
//                        rvProductHomeShimmer.stopShimmer()
//                    }
//                }
//            }
//        }
//    }

    private fun observeCashGoldBanners() {
        viewModel.bannerCashGold.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    with(binding) {
                        if (it.data != null) {
                            val adapter = CashGoldBannerAdapter(it.data) { _, _ ->
                                startActivity(Intent(activity, DashboardGoldActivity::class.java))
                            }

                            rvBannerCashgold.adapter = adapter
                            rvBannerCashgold.scrollToPosition(Integer.MAX_VALUE/2)
                        }
                    }
                }
                Status.LOADING -> {}
                Status.ERROR -> {}
            }
        }
    }

    private fun observeGotoMakkahBanners() {
        viewModel.bannerGotomekka.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    with(binding) {
                        if (it.data != null) {
                            val adapter = GoToMakkahBannerAdapter(it.data) { _, _ ->
                                startActivity(Intent(activity, MekkaListActivity::class.java))
                            }

                            rvGotomekka.adapter = adapter
                            rvGotomekka.scrollToPosition(Integer.MAX_VALUE/2)
                        }
                    }
                }
                Status.LOADING -> {}
                Status.ERROR -> {}
            }
        }
    }

    private fun animateBalance() {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(300)
            val scrollRight = ValueAnimator.ofFloat(0f, 100f)
            scrollRight.duration = 1700
            scrollRight.addUpdateListener { animation ->
                binding.rvBalance.smoothScrollBy((animation.animatedValue as Float).toInt(), 0)
            }
            val scrollLeft = ValueAnimator.ofFloat(0f, -100f)
            scrollLeft.duration = 1700
            scrollLeft.addUpdateListener { animation ->
                binding.rvBalance.smoothScrollBy((animation.animatedValue as Float).toInt(), 0)
            }
            animBalanceScroll?.playSequentially(scrollRight, scrollLeft)
            animBalanceScroll?.start()
        }
    }

    private fun stateEmailNTTL(profile: ProfileResponse) {
        if (profile.email.isEmpty() || profile.placeOfBorn.isEmpty() || profile.dateOfBirth.isEmpty()) {
            GenericModalDialogCashplus.Builder()
                .title(getString(R.string.notification))
                .image(R.drawable.illustration_notification)
                .description(
                    if (profile.email.isEmpty()) getString(R.string.data_email_is_empty)
                    else if (profile.placeOfBorn.isEmpty() || profile.dateOfBirth.isEmpty())
                        getString(R.string.data_place_n_date_of_born_is_empty)
                    else getString(R.string.data_profile_is_empty)
                )
                .buttonPositive(
                    PositiveButtonAction(
                        btnLabel = if (profile.email.isEmpty()) getString(R.string.email_configuration)
                        else getString(R.string.update_profile),
                        backgroundButton = R.drawable.bg_green600_rounded_20,
                        onBtnClicked = {
                            if (profile.email.isEmpty()) {
                                (activity as MainActivityNavComp).stateIsOpenEmailConfig = true
                                (activity as MainActivityNavComp).navigateToPage(PageMenu.OTHERS)
                            }
                            else startActivity(
                                Intent(requireContext(), ChangeProfilActivity::class.java).apply {
                                    putExtra(PROFILE_KEY_DATA, profile)
                                }
                            )
                        }
                    )
                )
                .buttonNegative(
                    NegativeButtonAction(
                        btnLabel = getString(R.string.close),
                        backgroundButton = R.drawable.bg_transparent_border_white_rounded_8,
                        setClickOnDismiss = true
                    )
                )
                .show(childFragmentManager, "Data not complete")
        }
    }

    private fun startLocationUpdates() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL_IN_MILLISECONDS)
        locationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        buildAlertMessageNoGps()
        locationProviderClient.requestLocationUpdates(
            locationRequest.build(),
            locationCallback, Looper.getMainLooper()
        )
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.post(sliderRunnable)
        animBalanceScroll = AnimatorSet()
        viewModel.getBalanceAccount(uuid, token)
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
        animBalanceScroll?.cancel()
        locationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sliderHandler.removeCallbacks(sliderRunnable)
        animBalanceScroll?.cancel()
        animBalanceScroll = null
        _binding = null
    }
}