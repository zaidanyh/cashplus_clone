package com.pasukanlangit.cashplus.ui.pages.history.detailhotel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.pasukanlangit.cashplus.databinding.ActivityDetailHistoryHotelBinding
import com.pasukanlangit.cashplus.ui.checkout.DetailTrxAdapter
import org.json.JSONException
import org.json.JSONObject

class DetailHistoryHotelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryHotelBinding

    private lateinit var detailTrxAdapter: DetailTrxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryHotelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jsonValue = intent.getStringExtra(KEY_JSON_PRINT)
        detailTrxAdapter = DetailTrxAdapter()
        binding.btnBack.setOnClickListener { finish() }
        if(jsonValue != null){
            try {
                val jsonObject = JSONObject(jsonValue)
                val listInfo = mutableListOf<String>()

                listInfo.add("Kode Booking : ${jsonObject.getString("kodebooking")}")
                listInfo.add("Nama Hotel : ${jsonObject.getString("hotel_name")}")
                listInfo.add(
                    "Alamat Hotel : ${jsonObject.getString("hotel_address")}, ${
                        jsonObject.getString(
                            "hotel_city"
                        )
                    }"
                )
                listInfo.add("Room Name : ${jsonObject.getString("room_name")}")
                listInfo.add("Check-In : ${jsonObject.getString("hotel_checkin")}")
                listInfo.add("Check-Out : ${jsonObject.getString("hotel_checkout")}")
                listInfo.add("Hotel Phone : ${jsonObject.getString("hotel_phone")}")
//                if("""(?i)(HOTEL)""".toRegex().containsMatchIn(typeProduct ?: HistoryDetailActivity.PRODUCT_CODE_INVALID)) {
//                    listInfo.add("Nama Hotel : ${jsonObject.getString("hotel_name")}")
//                    listInfo.add(
//                        "Alamat Hotel : ${jsonObject.getString("hotel_address")}, ${
//                            jsonObject.getString(
//                                "hotel_city"
//                            )
//                        }"
//                    )
//                    listInfo.add("Room Name : ${jsonObject.getString("room_name")}")
//                    listInfo.add("Check-In : ${jsonObject.getString("hotel_checkin")}")
//                    listInfo.add("Check-Out : ${jsonObject.getString("hotel_checkout")}")
//                    listInfo.add("Hotel Phone : ${jsonObject.getString("hotel_phone")}")
//                }else if("""(?i)(KERETA)""".toRegex().containsMatchIn(typeProduct ?: HistoryDetailActivity.PRODUCT_CODE_INVALID)){
//                    listInfo.add("Nama Kereta : ${jsonObject.getString("train_name")}")
//                    listInfo.add("Info Kereta : Kelas ${jsonObject.getString("train_class")}, Nomer Kereta ${jsonObject.getString("train_number")}")
//                    listInfo.add("Tanggal : ${jsonObject.getString("tanggal")}")
//                    listInfo.add("Rute : ${jsonObject.getString("train_inforoute")}")
//                    listInfo.add("${DetailTrxAdapter.STYLE_DASHED}:")
//
//                    val passengersArray = JSONArray(jsonObject.getString("train_datapassengers_json"))
//                    for(i in 0 until passengersArray.length()){
//                        val passengerObject = passengersArray.getJSONObject(i)
//                        listInfo.add("Penumpang ${i.plus(1)} : ${passengerObject.getString("passenger_title")} ${passengerObject.getString("passenger_fullname")}")
//                        listInfo.add("Seat: Gerbong ${passengerObject.getString("passenger_gerbong")}, No ${passengerObject.getString("passenger_seat")}")
//                        listInfo.add("${DetailTrxAdapter.STYLE_DASHED}:")
////                        if(i != passengersArray.length() - 1){
////                            listInfo.add("${DetailTrxAdapter.STYLE_NEW_LINE}:")
////                        }
//                    }
//                }else if("""(?i)(PESAWAT)""".toRegex().containsMatchIn(typeProduct ?: HistoryDetailActivity.PRODUCT_CODE_INVALID)){
//                    listInfo.add("Nama Pesawat : ${jsonObject.getString("flight")}")
//                    listInfo.add("Kode Pesawat : ${jsonObject.getString("flight_code")}")
//                    listInfo.add("Tanggal : ${jsonObject.getString("tanggal")}")
//                    listInfo.add("Rute : ${jsonObject.getString("flight_infotransit")}")
//                    listInfo.add("${DetailTrxAdapter.STYLE_DASHED}:")
//
//                    val passengersArray = JSONArray(jsonObject.getString("flight_datapassengers_json"))
//                    for(i in 0 until passengersArray.length()){
//                        val passengerObject = passengersArray.getJSONObject(i)
//                        listInfo.add("Penumpang ${i.plus(1)} : ${passengerObject.getString("passenger_title")} ${passengerObject.getString("passenger_fullname")}")
//                        listInfo.add("ID : ${passengerObject.getString("passenger_passportnumber")}")
//                        listInfo.add("Nomor : ${passengerObject.getString("passenger_ffnumber")}")
//                        listInfo.add("Bagasi : ${passengerObject.getString("passenger_baggage_prepaid")}")
//                        listInfo.add("${DetailTrxAdapter.STYLE_DASHED}:")
////                        if(i != passengersArray.length() - 1){
////                            listInfo.add("${DetailTrxAdapter.STYLE_NEW_LINE}:")
////                        }
//                    }
//                }

                with(binding.rvDataPrint) {
                    layoutManager = LinearLayoutManager(this@DetailHistoryHotelActivity)
                    adapter = detailTrxAdapter
                    detailTrxAdapter.setInfoDetails(listInfo)
                }
//
//                iterate(jsonObject.keys())?.forEach { key ->
//                    value.append("$key : ${jsonObject.getString(key)}\n")
//                }
//            for (key : iterate(jsonObject.keys()))
//            {
//                // here key will be containing your OBJECT NAME YOU CAN SET IT IN TEXTVIEW.
//                Toast.makeText(HomeActivity.this, ""+key, Toast.LENGTH_SHORT).show();
//            }
            }catch (ex: JSONException){
               Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val KEY_JSON_PRINT = "KEY_JSON_PRINT"
    }
}