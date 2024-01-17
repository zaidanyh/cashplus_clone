package com.pasukanlangit.id.downline_nearby_agent

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pasukanlangit.id.core.extensions.onDone
import com.pasukanlangit.id.core.presentation.components.GenericModalDialogCashplus
import com.pasukanlangit.id.core.presentation.components.NegativeButtonAction
import com.pasukanlangit.id.core.presentation.components.PositiveButtonAction
import com.pasukanlangit.id.core.utils.CashplusItemDecoration
import com.pasukanlangit.id.core.utils.InputUtil.inputAlphabetFilter
import com.pasukanlangit.id.core.utils.KeyboardUtil
import com.pasukanlangit.id.domain_downline.model.NearbyAgentResponse
import com.pasukanlangit.id.downline_nearby_agent.databinding.ActivityNearbyAgentBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class NearbyAgentActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityNearbyAgentBinding
    private val viewModel: NearbyAgentViewModel by viewModel()

    private lateinit var agentAdapter: AgentNearbyAdapter
    private lateinit var locationRequest: LocationRequest.Builder
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationProviderClient : FusedLocationProviderClient

    private val zoomLevelMap = 15.0f //This goes up to 21

    private var mapFragment : SupportMapFragment? = null
    private var googleMap: GoogleMap? = null
    private var isShownClose = false

    private val permissionLocationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                getCurrentLocation()
                return@registerForActivityResult
            }
            Toast.makeText(this, getString(R.string.provide_request_permission_location), Toast.LENGTH_SHORT).show()
        }
    private val turnOnGPS = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        getCurrentLocation()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearbyAgentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpMap()
        setUpRv()
        with(binding) {
            btnBack.setOnClickListener { finish() }
            edtClosestAgent.filters = arrayOf(InputFilter.LengthFilter(16), inputAlphabetFilter)
            edtClosestAgent.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (::agentAdapter.isInitialized) agentAdapter.filter.filter(s.toString())
                    if (!s.isNullOrEmpty()) {
                        isShownClose = true
                        edtClosestAgent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_close, 0)
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
            edtClosestAgent.setOnTouchListener { _, event ->
                val drawableEnd = 2
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (edtClosestAgent.right - edtClosestAgent.compoundDrawables[drawableEnd].bounds.width())) {
                        if (isShownClose) {
                            edtClosestAgent.text?.clear()
                            edtClosestAgent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_search, 0)
                            KeyboardUtil.hideSoftKeyboard(this@NearbyAgentActivity)
                            edtClosestAgent.clearFocus()
                            isShownClose = !isShownClose
                            return@setOnTouchListener true
                        }
                    }
                }
                false
            }
            edtClosestAgent.onDone {
                KeyboardUtil.hideSoftKeyboard(this@NearbyAgentActivity)
            }
            btnRefresh.setOnClickListener {
                getCurrentLocation()
            }
        }
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                val currentLocation = result.lastLocation
                currentLocation?.let { location ->
                    viewModel.onEventClosestAgent(ClosestAgentEvent.UpdateLatLong(lat = location.latitude, long = location.longitude))
                }
            }
        }
        collectState()
    }

    private fun setUpMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun setUpRv() {
        agentAdapter = AgentNearbyAdapter { lat, long ->
            moveCameraMap(lat, long)
        }
        with(binding.rvClosestAgent) {
            layoutManager = LinearLayoutManager(this@NearbyAgentActivity)
            adapter = agentAdapter
            addItemDecoration(CashplusItemDecoration(24))
        }
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.stateLoading.collectLatest {
                        binding.loading.isVisible = it
                    }
                }
                launch {
                    viewModel.updateLoading.collectLatest {
                        binding.loading.isVisible = it
                    }
                }

                launch {
                    viewModel.updateLocation.collectLatest { state ->
                        if (state != null) {
                            if (state) viewModel.onEventClosestAgent(ClosestAgentEvent.GetClosestAgent)
                        }
                    }
                }
                launch {
                    viewModel.nearByAgent.collectLatest { response ->
                         response?.let { data ->
                             binding.wrapperBottomSheet.isVisible = data.agents.isNotEmpty()
                             if(data.agents.isEmpty()) {
                                 GenericModalDialogCashplus.Builder()
                                     .title(getString(R.string.warning))
                                     .description(getString(R.string.closest_agent_not_found))
                                     .image(R.drawable.ic_agent_empty)
                                     .buttonNegative(
                                         NegativeButtonAction(
                                             btnLabel = getString(R.string.close),
                                             onBtnClicked = {
                                                 this@NearbyAgentActivity.finish()
                                             }
                                         )
                                     )
                                     .show(supportFragmentManager)
                                 return@collectLatest
                             }
                             agentAdapter.setAgents(data.agents)
                             googleMap?.clear()
                             bindMap(data)
                         }
                    }
                }

                launch {
                    viewModel.stateError.collectLatest { message ->
                        binding.wrapperBottomSheet.isVisible = false
                        message.peek()?.let { info ->
                            val toast =
                                Toast.makeText(this@NearbyAgentActivity, info, Toast.LENGTH_SHORT)
                            toast.show()
                            delay(toast.duration.toLong())
                            viewModel.onEventClosestAgent(ClosestAgentEvent.RemoveHeadMessage)
                        }
                    }
                }
            }
        }
    }

    private fun bindMap(nearByAgent: NearbyAgentResponse) {
        googleMap?.apply {
            moveCameraMap(nearByAgent.myLat, nearByAgent.myLong)

            val options = MarkerOptions()
            val latLongs = mutableListOf<LatLng>()
            val nameAgents = mutableListOf<String>()

            latLongs.add(LatLng(nearByAgent.myLat, nearByAgent.myLong))
            nameAgents.add(getString(R.string.your_location))

            nearByAgent.agents.forEach { agent ->
                latLongs.add(LatLng(agent.lat, agent.long))
                nameAgents.add(agent.name)
            }

            latLongs.forEachIndexed { index, point ->
                options.position(point)

                if (index == 0)
                    options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location))
                else options.icon(BitmapDescriptorFactory.defaultMarker())

                options.title(nameAgents[index])
                addMarker(options)?.showInfoWindow()
            }
        }
    }

    private fun moveCameraMap(lat: Double, long: Double) {
        googleMap?.apply {
            val newLatLong = CameraUpdateFactory.newLatLngZoom(
                LatLng(lat, long),
                zoomLevelMap
            )
            animateCamera(newLatLong)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        viewModel.onEventClosestAgent(ClosestAgentEvent.GetClosestAgent)
    }

    private fun getCurrentLocation() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, (10000).toLong())

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionLocationLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return
        }

        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
                .show(supportFragmentManager, "Check Location")
            return
        }

        locationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                val latitude = location?.latitude
                val longitude = location?.longitude
                if (latitude == null || longitude == null) return@addOnSuccessListener

                viewModel.onEventClosestAgent(ClosestAgentEvent.UpdateLatLong(lat = latitude, long = longitude))
            }
            .addOnFailureListener { exception ->
                locationProviderClient.requestLocationUpdates(
                    locationRequest.build(),
                    locationCallback,
                    Looper.getMainLooper()
                )
                Log.d("Location on failure", exception.message.toString())
            }
    }

    override fun onPause() {
        super.onPause()
        locationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        getCurrentLocation()
    }
}