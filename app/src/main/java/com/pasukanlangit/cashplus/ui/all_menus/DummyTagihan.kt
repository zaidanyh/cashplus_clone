package com.pasukanlangit.cashplus.ui.all_menus

import com.pasukanlangit.cashplus.R

data class DummyTagihan (
    val title: String,
    val image: Int,
    val direction: Int
)

enum class PositionPagePembayaranBulanan(val position: Int) {
    PASCABAYAR(0),
    BPJS(1),
    PBB(2),
    TELKOM(3),
    PDAM(4),
    TVKABEL(5),
    SAMSAT(6),
    MULTIFINANCE(7),
    KARTU_KREDIT(8),
    ASURANSI(9),
    GAS_PERTAMINA(10),
    GAS_NEGARA(11),
    TAGIHAN_PLN(12)
}

fun getDummyTagihanMenu() : List<DummyTagihan>{
    return listOf(
        DummyTagihan(
            "OMNI",
            R.drawable.ic_telkomsel_rounded,
            310
        ),
        DummyTagihan(
            "Pasca Bayar",
            R.drawable.icon_pascabayar,
            PositionPagePembayaranBulanan.PASCABAYAR.position
        ),
        DummyTagihan(
            "BPJS",
            R.drawable.tagihan_bpjs,
            PositionPagePembayaranBulanan.BPJS.position
        ),
        DummyTagihan(
            "Pajak Bumi & Bangunan",
            R.drawable.icon_pbb,
            PositionPagePembayaranBulanan.PBB.position
        ),
        DummyTagihan(
            "Telkom",
            R.drawable.icon_telkom,
            PositionPagePembayaranBulanan.TELKOM.position
        ),
        DummyTagihan(
            "PDAM",
            R.drawable.menu_pdam,
            PositionPagePembayaranBulanan.PDAM.position
        ),
        DummyTagihan(
            "Listrik",
            R.drawable.menu_token_listrik,
            PositionPagePembayaranBulanan.TAGIHAN_PLN.position
        ),
        DummyTagihan(
            "Tv Kabel",
            R.drawable.icon_tv,
            PositionPagePembayaranBulanan.TVKABEL.position
        ),
        DummyTagihan(
            "Samsat",
            R.drawable.icon_samsat,
            PositionPagePembayaranBulanan.SAMSAT.position
        ),
        DummyTagihan(
            "Multifinance",
            R.drawable.icon_multifinance,
            PositionPagePembayaranBulanan.MULTIFINANCE.position
        ),
        DummyTagihan(
            "Kartu Kredit",
            R.drawable.icon_kartukredit,
            PositionPagePembayaranBulanan.KARTU_KREDIT.position
        ),
        DummyTagihan(
            "Asuransi",
            R.drawable.icon_asuransi,
            PositionPagePembayaranBulanan.ASURANSI.position
        ),
        DummyTagihan(
            "Gas Pertamina",
            R.drawable.icon_gas_pertamina,
            PositionPagePembayaranBulanan.GAS_PERTAMINA.position
        ),
        DummyTagihan(
            "Gas Negara",
            R.drawable.icon_gas_negara,
            PositionPagePembayaranBulanan.GAS_NEGARA.position
        )
    )
}