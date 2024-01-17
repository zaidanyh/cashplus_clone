package com.pasukanlangit.cashplus.ui.pages.home

import com.pasukanlangit.cashplus.R

data class HotelModel (
    val logo: Int,
    val title: String,
    val startCount: Int,
    val price: String
)

fun getDummyHotel() : List<HotelModel> {
    return listOf(
        HotelModel(
            R.drawable.dummy_hotel_1,
            "Aston Pasteur",
            4,
            "Rp 627.900"
        ),
        HotelModel(
            R.drawable.dummy_hotel_2,
            "The Trans Luxury Hotel",
            5,
            "Rp 1.437.500"
        ),
        HotelModel(
            R.drawable.dummy_hotel_3,
            "Swiss-Belresort Dago Heritage Bandung",
            4,
            "Rp 954.990"
        ),
        HotelModel(
            R.drawable.dummy_hotel_4,
            "Crowne Plaza Bandung",
            5,
            "Rp 732.244"
        ),
        HotelModel(
            R.drawable.dummy_hotel_5,
            "Horison Ultima Bandung",
            4,
            "Rp 380.000"
        ),
        HotelModel(
            R.drawable.dummy_hotel_6,
            "Aryaduta Bandung",
            4,
            "Rp 666.667"
        ),
        HotelModel(
            R.drawable.dummy_hotel_7,
            "Hilton Bandung",
            5,
            "Rp 1.004.300"
        ),
        HotelModel(
            R.drawable.dummy_hotel_8,
            "Grand Tjokro Premiere Bandung",
            4,
            "Rp 471.316"
        ),
        HotelModel(
            R.drawable.dummy_hotel_9,
            "Holiday Inn Bandung Pasteur",
            4,
            "Rp 602.109"
        ),
        HotelModel(
            R.drawable.dummy_hotel_10,
            "InterContinental Bandung Dago Pakar",
            5,
            "Rp 1.747.845"
        ),
        HotelModel(
            R.drawable.dummy_hotel_1,
            "Aston Pasteur",
            4,
            "Rp 627.900"
        ),
        HotelModel(
            R.drawable.dummy_hotel_2,
            "The Trans Luxury Hotel",
            5,
            "Rp 1.437.500"
        ),
        HotelModel(
            R.drawable.dummy_hotel_3,
            "Swiss-Belresort Dago Heritage Bandung",
            4,
            "Rp 954.990"
        ),
        HotelModel(
            R.drawable.dummy_hotel_4,
            "Crowne Plaza Bandung",
            5,
            "Rp 732.244"
        ),
        HotelModel(
            R.drawable.dummy_hotel_5,
            "Horison Ultima Bandung",
            4,
            "Rp 380.000"
        ),
        HotelModel(
            R.drawable.dummy_hotel_6,
            "Aryaduta Bandung",
            4,
            "Rp 666.667"
        ),
        HotelModel(
            R.drawable.dummy_hotel_7,
            "Hilton Bandung",
            5,
            "Rp 1.004.300"
        ),
        HotelModel(
            R.drawable.dummy_hotel_8,
            "Grand Tjokro Premiere Bandung",
            4,
            "Rp 471.316"
        ),
        HotelModel(
            R.drawable.dummy_hotel_9,
            "Holiday Inn Bandung Pasteur",
            4,
            "Rp 602.109"
        ),
        HotelModel(
            R.drawable.dummy_hotel_10,
            "InterContinental Bandung Dago Pakar",
            5,
            "Rp 1.747.845"
        )
    )
}