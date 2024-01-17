package com.pasukanlangit.cashplus.ui.pages.home

import android.os.Parcelable
import com.pasukanlangit.cashplus.R
import kotlinx.parcelize.Parcelize

//DEEPLINK BASE_URL
private const val DEEPLINK_SCHEME = "app"
private const val DEEPLINK_HOST = "www.cashplus.id"
private const val DEEPLINK_URL = "$DEEPLINK_SCHEME://$DEEPLINK_HOST"


//DEEPLINK PATH
//NOTE: this should match with pathPrefix at manifest in each activity
private const val GAME_PATH = "game_menu"
private const val DETAIL_MENU_E_WALLET_PATH = "detail_menu_e_wallet"
private const val PEMBAYARAN_TAGIHAN_PATH = "pembayaran_tagihan"
private const val PEMBAYARAN_PULSA_DAN_DATA_PATH = "pulsa_data"
private const val PEMBAYARAN_PLN_PATH = "pln"


//DEEPLINK QUERY PARAMS KEY
const val KEY_GAME_DEEPLINK = "search"
const val KEY_GLOBAL_DETAIL_MENU_PROVIDER_CODE = "providerCode"
const val KEY_PULSA_DAN_DATA_ACTIVE_PAGE = "activePage"


@Parcelize
data class BannerDummy (
    val image: Int,
    val titleBanner: String,
    val descriptionShort: String,
    val descriptionLong: String,
    val uri: String? = null
): Parcelable

fun getBannerTravelDummy() : List<BannerDummy> {
    return listOf(
        BannerDummy(
            R.drawable.banner_travel1,
            "Booking hotel cepat dan mudah",
            "Booking Hotel dan akomodasi lainnya dengan mudah, cepat, dan aman bisa kamu lakukan di Cashplus.",
            "Booking Hotel dan akomodasi lainnya dengan mudah, cepat, dan aman bisa kamu lakukan di Cashplus. Proses pemilihan akomodasi, jenis kamar, dan detail lainnya bisa kamu lakukan di Cashplus dengan aman, lancar, dan dengan harga yang terbaik!."
        ),
        BannerDummy(
            R.drawable.banner_travel2,
            "Booking hotel cepat dan mudah",
            "Booking Hotel dan akomodasi lainnya dengan mudah, cepat, dan aman bisa kamu lakukan di Cashplus.",
            "Booking Hotel dan akomodasi lainnya dengan mudah, cepat, dan aman bisa kamu lakukan di Cashplus. Proses pemilihan akomodasi, jenis kamar, dan detail lainnya bisa kamu lakukan di Cashplus dengan aman, lancar, dan dengan harga yang terbaik!."
        ),
        BannerDummy(
            R.drawable.banner_travel3,
            "Booking hotel cepat dan mudah",
            "Booking Hotel dan akomodasi lainnya dengan mudah, cepat, dan aman bisa kamu lakukan di Cashplus.",
            "Booking Hotel dan akomodasi lainnya dengan mudah, cepat, dan aman bisa kamu lakukan di Cashplus. Proses pemilihan akomodasi, jenis kamar, dan detail lainnya bisa kamu lakukan di Cashplus dengan aman, lancar, dan dengan harga yang terbaik!."
        )
    )
}

fun getBannerCashGoldDummy() : List<BannerDummy> {
    return listOf(
        BannerDummy(
            R.drawable.cashgold_banner_1_100,
            "Gunakan dan Nikmati kemudahan membeli Emas di CashGold",
            "Dengan melengkapi data-data pengguna melalui KYC, kamu bisa langsung gunakan fitur Cashgold",
            "Hai Sahabat Cashplus! Sekarang kamu dapat membeli Emas dan Cetak Emas di Cashplus. Gunakan dan Nikmati kemudahan membeli Emas di CashGold. Dengan melengkapi data-data pengguna melalui KYC, kamu bisa langsung gunakan fitur Cashgold. Harga Emas akan selalu update realtime sesuai harga pasar emas. Setelah kamu membeli Emas di Cashplus, kamu juga bisa memproses pencetakkan emas dengan mudah, dan tentu nya emas yang dicetak tersertifikasi dengan aman."
        ),
        BannerDummy(
            R.drawable.cashgold_banner_2_100,
            "Gunakan dan Nikmati kemudahan membeli Emas di CashGold",
            "Dengan melengkapi data-data pengguna melalui KYC, kamu bisa langsung gunakan fitur Cashgold",
            "Hai Sahabat Cashplus! Sekarang kamu dapat membeli Emas dan Cetak Emas di Cashplus. Gunakan dan Nikmati kemudahan membeli Emas di CashGold. Dengan melengkapi data-data pengguna melalui KYC, kamu bisa langsung gunakan fitur Cashgold. Harga Emas akan selalu update realtime sesuai harga pasar emas. Setelah kamu membeli Emas di Cashplus, kamu juga bisa memproses pencetakkan emas dengan mudah, dan tentu nya emas yang dicetak tersertifikasi dengan aman."
        ),
        BannerDummy(
            R.drawable.cashgold_banner_3_100,
            "Gunakan dan Nikmati kemudahan membeli Emas di CashGold",
            "Dengan melengkapi data-data pengguna melalui KYC, kamu bisa langsung gunakan fitur Cashgold",
            "Hai Sahabat Cashplus! Sekarang kamu dapat membeli Emas dan Cetak Emas di Cashplus. Gunakan dan Nikmati kemudahan membeli Emas di CashGold. Dengan melengkapi data-data pengguna melalui KYC, kamu bisa langsung gunakan fitur Cashgold. Harga Emas akan selalu update realtime sesuai harga pasar emas. Setelah kamu membeli Emas di Cashplus, kamu juga bisa memproses pencetakkan emas dengan mudah, dan tentu nya emas yang dicetak tersertifikasi dengan aman."
        ),
        BannerDummy(
            R.drawable.cashgold_banner_4_100,
            "Gunakan dan Nikmati kemudahan membeli Emas di CashGold",
            "Dengan melengkapi data-data pengguna melalui KYC, kamu bisa langsung gunakan fitur Cashgold",
            "Hai Sahabat Cashplus! Sekarang kamu dapat membeli Emas dan Cetak Emas di Cashplus. Gunakan dan Nikmati kemudahan membeli Emas di CashGold. Dengan melengkapi data-data pengguna melalui KYC, kamu bisa langsung gunakan fitur Cashgold. Harga Emas akan selalu update realtime sesuai harga pasar emas. Setelah kamu membeli Emas di Cashplus, kamu juga bisa memproses pencetakkan emas dengan mudah, dan tentu nya emas yang dicetak tersertifikasi dengan aman."
        ),
        BannerDummy(
            R.drawable.cashgold_banner_5_100,
            "Gunakan dan Nikmati kemudahan membeli Emas di CashGold",
            "Dengan melengkapi data-data pengguna melalui KYC, kamu bisa langsung gunakan fitur Cashgold",
            "Hai Sahabat Cashplus! Sekarang kamu dapat membeli Emas dan Cetak Emas di Cashplus. Gunakan dan Nikmati kemudahan membeli Emas di CashGold. Dengan melengkapi data-data pengguna melalui KYC, kamu bisa langsung gunakan fitur Cashgold. Harga Emas akan selalu update realtime sesuai harga pasar emas. Setelah kamu membeli Emas di Cashplus, kamu juga bisa memproses pencetakkan emas dengan mudah, dan tentu nya emas yang dicetak tersertifikasi dengan aman."
        ),
    )
}


fun getBannerGotomekkaDummy() : List<BannerDummy> {
    return listOf(
        BannerDummy(
            R.drawable.gotomekka_banner_1_100,
            "Beli paket perjalanan Haji dan Umroh dengan mudah, aman, dan dengan harga yang terbaik",
            "Kamu dapat mewujudkan mimpi kamu untuk pergi Haji dan Umroh melalui Cashplus.",
            "Kamu dapat mewujudkan mimpi kamu untuk pergi Haji dan Umroh melalui Cashplus. Beli paket perjalanan Haji dan Umroh dengan mudah, aman, dan dengan harga yang terbaik."
        ),
        BannerDummy(
            R.drawable.gotomekka_banner_2_100,
            "Beli paket perjalanan Haji dan Umroh dengan mudah, aman, dan dengan harga yang terbaik",
            "Kamu dapat mewujudkan mimpi kamu untuk pergi Haji dan Umroh melalui Cashplus.",
            "Kamu dapat mewujudkan mimpi kamu untuk pergi Haji dan Umroh melalui Cashplus. Beli paket perjalanan Haji dan Umroh dengan mudah, aman, dan dengan harga yang terbaik."
        ),
        BannerDummy(
            R.drawable.gotomekka_banner_3_100,
            "Beli paket perjalanan Haji dan Umroh dengan mudah, aman, dan dengan harga yang terbaik",
            "Kamu dapat mewujudkan mimpi kamu untuk pergi Haji dan Umroh melalui Cashplus.",
            "Kamu dapat mewujudkan mimpi kamu untuk pergi Haji dan Umroh melalui Cashplus. Beli paket perjalanan Haji dan Umroh dengan mudah, aman, dan dengan harga yang terbaik."
        ),
        BannerDummy(
            R.drawable.gotomekka_banner_4_100,
            "Beli paket perjalanan Haji dan Umroh dengan mudah, aman, dan dengan harga yang terbaik",
            "Kamu dapat mewujudkan mimpi kamu untuk pergi Haji dan Umroh melalui Cashplus.",
            "Kamu dapat mewujudkan mimpi kamu untuk pergi Haji dan Umroh melalui Cashplus. Beli paket perjalanan Haji dan Umroh dengan mudah, aman, dan dengan harga yang terbaik."
        ),
        BannerDummy(
            R.drawable.gotomekka_banner_5_100,
            "Beli paket perjalanan Haji dan Umroh dengan mudah, aman, dan dengan harga yang terbaik",
            "Kamu dapat mewujudkan mimpi kamu untuk pergi Haji dan Umroh melalui Cashplus.",
            "Kamu dapat mewujudkan mimpi kamu untuk pergi Haji dan Umroh melalui Cashplus. Beli paket perjalanan Haji dan Umroh dengan mudah, aman, dan dengan harga yang terbaik."
        ),
    )
}


fun getBannerDummy() : List<BannerDummy> {
    return listOf(
        BannerDummy(
            R.drawable.banner_codm_100,
            "Topup CODM dengan mudah",
            "Kemudahan transaksi top up Game Call Of Duty Mobile ada di Cashplus !.",
            "Kemudahan transaksi top up Game Call Of Duty Mobile ada di Cashplus !. Dengan variasi nominal Top Up yang kami sediakan Anda dapat mengisi CP Anda dengan sesuai kemauan Anda. Transaksi mudah dan cepat dengan harga yang bersahabat. CP Point akan langsung masuk kedalam akun game kamu setelah transaksi sukses di Cashplus. Transaksi bisa kamu lakukan kapanpun dan dimanapun. Top Up dapat kamu lakukan beberapa kali dalam waktu satu hari. ",
            "$DEEPLINK_URL/$GAME_PATH?$KEY_GAME_DEEPLINK=CODM"
        ),
        BannerDummy(
            R.drawable.banner_ewallet_100,
            "Topup E-Wallet dengan mudah",
            "Dapatkan kemudahan transaksi Top Up Saldo E-Wallet di Cashplus dengan kecepatan transaksi dan harga yang terbaik",
            "Dear Sahabat Cashplus! Dapatkan kemudahan transaksi Top Up Saldo E-Wallet di Cashplus dengan kecepatan transaksi dan harga yang terbaik yang kami berikan. Saldo E-Wallet yang kamu isi / Top up melalui Cashplus akan langsung ke dalam aplikasi E-Wallet kamu, tanpa perlu memasukkan kode voucher atau apapun. Top Up E-Wallet dapat kamu lakukan beberapa kali dalam 1 hari.",
            "$DEEPLINK_URL/menu_e_wallet"
        ),
        BannerDummy(
            R.drawable.banner_game_100,
            "Topup game dengan mudah",
            "Dengan variasi nominal Top Up dan Voucher yang kami sediakan Anda dapat Top Up Games sesuai kemauan Anda.",
            "Kemudahan transaksi top up dan Pembelian Voucher Game kami hadirkan di Cashplus !. Dengan variasi nominal Top Up dan Voucher yang kami sediakan Anda dapat Top Up Games sesuai kemauan Anda. Transaksi mudah dan cepat dengan harga yang bersahabat. Transaksi cepat, aman, dan valid. Transaksi bisa kamu lakukan kapanpun dan dimanapun. Top Up dapat kamu lakukan beberapa kali dalam waktu satu hari.",
            "$DEEPLINK_URL/$GAME_PATH"
        ),

        BannerDummy(
            R.drawable.banner_ff_100,
            "Topup Diamond Free Fire dengan mudah",
            "Hai Free Fire Lovers. Top Up Diamonds Free Fire kamu sekarang di Cashplus!",
            "Hai Free Fire Lovers. Top Up Diamonds Free Fire kamu sekarang di Cashplus!. Transaksi cepat, aman, dan harga yang terbaik hanya untuk kamu user Cashplus. Diamonds Free Fire kamu akan langsung masuk ke dalam akun game yang kamu Top Up melalui Cashplus. Ayoo isi sekarang, semua items di Shop menanti kamu!",
            "$DEEPLINK_URL/$GAME_PATH?$KEY_GAME_DEEPLINK=FREEFIRE_MOBILE"
        ),
        BannerDummy(
            R.drawable.banner_ml_100,
            "Topup Diamond Mobile Legends dengan mudah",
            "Hai Legends!. Top Up Diamonds Mobile Legends kamu sekarang di Cashplus!.",
            "Hai Legends!. Top Up Diamonds Mobile Legends kamu sekarang di Cashplus!. Transaksi cepat, aman, dan harga yang terbaik hanya untuk kamu user Cashplus. Diamonds Mobile Legends kamu akan langsung masuk ke dalam akun game yang kamu Top Up melalui Cashplus. Ayoo isi sekarang, semua items di Shop menanti kamu!",
            "$DEEPLINK_URL/$GAME_PATH?$KEY_GAME_DEEPLINK=MOBILE_LEGENDS"
        ),
        BannerDummy(
            R.drawable.banner_pubg_100,
            "Topup UC PUBG dengan mudah",
            "Segera isi UC PUBG dengan beli voucher UC PUBG di Cashplus",
            "Segera isi UC PUBG dengan beli voucher UC PUBG di Cashplus, kami berikan kemudahan transaksi dan harga yang terbaik untuk kamu.Dengan variasi nominal Voucher yang kami sediakan Anda dapat mengisi UC Anda dengan sesuai kemauan Anda. Transaksi mudah dan cepat dengan harga yang bersahabat. Transaksi bisa kamu lakukan kapanpun dan dimanapun.",
            "$DEEPLINK_URL/$GAME_PATH?$KEY_GAME_DEEPLINK=PUBG_MOBILE"
        ),
        BannerDummy(
            R.drawable.banner_ppob_100,
            "Bayar PPOB dengan mudah",
            "Cek dan bayar tagihan PPOB dapat dengan mudah kamu lakukan hanya di Cashplus",
            "Sahabat Cashplus, cek dan bayar tagihan PPOB dapat dengan mudah kamu lakukan hanya di Cashplus!. Proses yang cepat dan dengan biaya admin yang murah, tentunya akan kamu nikmati. Hanya dengan 1 Aplikasi, kamu dapat membayar berbagai macam tagihan PPOB.",
            "$DEEPLINK_URL/$PEMBAYARAN_TAGIHAN_PATH"
        ),
        BannerDummy(
            R.drawable.banner_dana_100,
            "Isi saldo Dana dengan mudah",
            "Dapatkan kemudahan transaksi Top Up Saldo E-Wallet di Cashplus dengan kecepatan transaksi dan harga yang terbaik ",
            "Dear Sahabat Cashplus! Dapatkan kemudahan transaksi Top Up Saldo E-Wallet di Cashplus dengan kecepatan transaksi dan harga yang terbaik yang kami berikan. Saldo E-Wallet yang kamu isi / Top up melalui Cashplus akan langsung ke dalam aplikasi E-Wallet kamu, tanpa perlu memasukkan kode voucher atau apapun. Top Up E-Wallet dapat kamu lakukan beberapa kali dalam 1 hari.",
            "$DEEPLINK_URL/$DETAIL_MENU_E_WALLET_PATH?$KEY_GLOBAL_DETAIL_MENU_PROVIDER_CODE=DANA"
        ),
        BannerDummy(
            R.drawable.banner_data_100,
            "Isi paket data dengan mudah",
            "Dapatkan kemudahan pengisian kuota All Operator di Cashplus, dengan kecepatan transaksi dan harga terbaik",
            "Dear sahabat Cashplus. Jangan sampai kehabisan kuota disaat kamu sedang beraktivitas. Dapatkan kemudahan pengisian kuota All Operator di Cashplus, dengan kecepatan transaksi dan harga terbaik yang kami berikan. Kuota kamu akan langsung masuk ke nomor handphone yang kamu isi. Transaksi bisa kamu lakukan dimanapun dan kapanpun.",
            "$DEEPLINK_URL/$PEMBAYARAN_PULSA_DAN_DATA_PATH?$KEY_PULSA_DAN_DATA_ACTIVE_PAGE=1"
        ),
        BannerDummy(
            R.drawable.banner_gopay_100,
            "Isi saldo Gopay dengan mudah",
            "Dapatkan kemudahan transaksi Top Up Saldo E-Wallet di Cashplus dengan kecepatan transaksi dan harga yang terbaik ",
            "Dear Sahabat Cashplus! Dapatkan kemudahan transaksi Top Up Saldo E-Wallet di Cashplus dengan kecepatan transaksi dan harga yang terbaik yang kami berikan. Saldo E-Wallet yang kamu isi / Top up melalui Cashplus akan langsung ke dalam aplikasi E-Wallet kamu, tanpa perlu memasukkan kode voucher atau apapun. Top Up E-Wallet dapat kamu lakukan beberapa kali dalam 1 hari.",
            "$DEEPLINK_URL/$DETAIL_MENU_E_WALLET_PATH?$KEY_GLOBAL_DETAIL_MENU_PROVIDER_CODE=GOPAY_CUSTOMER"
        ),
        BannerDummy(
            R.drawable.banner_linkaja_100,
            "Isi saldo Link Aja dengan mudah",
            "Dapatkan kemudahan transaksi Top Up Saldo E-Wallet di Cashplus dengan kecepatan transaksi dan harga yang terbaik ",
            "Dear Sahabat Cashplus! Dapatkan kemudahan transaksi Top Up Saldo E-Wallet di Cashplus dengan kecepatan transaksi dan harga yang terbaik yang kami berikan. Saldo E-Wallet yang kamu isi / Top up melalui Cashplus akan langsung ke dalam aplikasi E-Wallet kamu, tanpa perlu memasukkan kode voucher atau apapun. Top Up E-Wallet dapat kamu lakukan beberapa kali dalam 1 hari.",
            "$DEEPLINK_URL/$DETAIL_MENU_E_WALLET_PATH?$KEY_GLOBAL_DETAIL_MENU_PROVIDER_CODE=LINK_AJA"
        ),
        BannerDummy(
            R.drawable.banner_ovo_100,
            "Isi saldo Ovo dengan mudah",
            "Dapatkan kemudahan transaksi Top Up Saldo E-Wallet di Cashplus dengan kecepatan transaksi dan harga yang terbaik ",
            "Dear Sahabat Cashplus! Dapatkan kemudahan transaksi Top Up Saldo E-Wallet di Cashplus dengan kecepatan transaksi dan harga yang terbaik yang kami berikan. Saldo E-Wallet yang kamu isi / Top up melalui Cashplus akan langsung ke dalam aplikasi E-Wallet kamu, tanpa perlu memasukkan kode voucher atau apapun. Top Up E-Wallet dapat kamu lakukan beberapa kali dalam 1 hari.",
            "$DEEPLINK_URL/$DETAIL_MENU_E_WALLET_PATH?$KEY_GLOBAL_DETAIL_MENU_PROVIDER_CODE=OVO"
        ),
        BannerDummy(
            R.drawable.banner_pulsa_100,
            "Isi pulsa dengan mudah",
            "Segera isi Pulsa kamu di Cashplus. Pulsa dengan harga terbaik, nominal yang bervariasi dan kecepatan yang transaksi terbaik",
            "Hai Sahabat Cashplus! Segera isi Pulsa kamu di Cashplus. Pulsa dengan harga terbaik, nominal yang bervariasi dan kecepatan yang transaksi terbaik kami sediakan di Cashplus!. Gausah bingung lagi mau isi pulsa dimana, buka aplikasi Cashplus mu, pergi ke menu “Pulsa”, masukkan nomor handphone mu, masukkan pin mu, dan transaksi Sukses! ",
            "$DEEPLINK_URL/$PEMBAYARAN_PULSA_DAN_DATA_PATH"
        ),
        BannerDummy(
            R.drawable.banner_shopee_100,
            "Isi saldo Shopee Pay dengan mudah",
            "Dapatkan kemudahan transaksi Top Up Saldo E-Wallet di Cashplus dengan kecepatan transaksi dan harga yang terbaik ",
            "Dear Sahabat Cashplus! Dapatkan kemudahan transaksi Top Up Saldo E-Wallet di Cashplus dengan kecepatan transaksi dan harga yang terbaik yang kami berikan. Saldo E-Wallet yang kamu isi / Top up melalui Cashplus akan langsung ke dalam aplikasi E-Wallet kamu, tanpa perlu memasukkan kode voucher atau apapun. Top Up E-Wallet dapat kamu lakukan beberapa kali dalam 1 hari.",
            "$DEEPLINK_URL/$DETAIL_MENU_E_WALLET_PATH?$KEY_GLOBAL_DETAIL_MENU_PROVIDER_CODE=SHOPEE"
        ),
        BannerDummy(
            R.drawable.banner_tokenpln_100,
            "Isi token PLN dengan mudah",
            "Dengan Cashplus kamu dapat melakukan pembelian token PLN dengan cepat, mudah, dan tentunya dengan harga yang murah!.",
            "Hai sahabat Cashplus!. Saat ini isi Token PLN tidak perlu bingung dan susah. Dengan Cashplus kamu dapat melakukan pembelian token PLN dengan cepat, mudah, dan tentunya dengan harga yang murah!. Buktikan sekarang dan rasakan kemudahan dan kualitas nya!",
            "$DEEPLINK_URL/$PEMBAYARAN_PLN_PATH"
        )
    )
}