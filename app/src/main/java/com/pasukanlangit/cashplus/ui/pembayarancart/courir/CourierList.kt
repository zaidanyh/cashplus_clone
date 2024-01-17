package com.pasukanlangit.cashplus.ui.pembayarancart.courir

import android.os.Parcelable
import com.pasukanlangit.cashplus.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class Courier(
    val name: String,
    val value: String,
    val image: Int
) : Parcelable

fun getCouriers(): List<Courier> = listOf(
    Courier("JNE","jne", R.drawable.icon_jne),
    Courier("JNT","jnt", R.drawable.icon_jnt),
    Courier("Sicepat","sicepat", R.drawable.icon_sicepat),
    Courier("Ninja Express","ninja", R.drawable.icon_ninjaexpress),
    Courier("TIKI","tiki", R.drawable.icon_tiki),
    Courier("Anteraja","anteraja", R.drawable.icon_anteraja),
    Courier("POS","pos", R.drawable.icon_pos_indonesia)
//    ,
//    Courier("Lion Parcel","lion", R.drawable.icon_lionparcel),
//    Courier("iDexpress","ide", R.drawable.icon_idexpress),
//    Courier("SAP Express","sap", R.drawable.icon_sap),
//    Courier("NCS","ncs", R.drawable.icon_ncs),
//    Courier("REX","rex", R.drawable.icon_rex),
//    Courier("SENTRAL","sentral", R.drawable.icon_sentralcargo),
//    Courier("RPX","rpx", R.drawable.icon_rpx),
//    Courier("Pandu","pandu", R.drawable.icon_pandu),
//    Courier("Wahana","wahana", R.drawable.icon_wahana),
//    Courier("Pahala","pahala", R.drawable.icon_pahala),
//    Courier("JET","jet", R.drawable.icon_jet),
)