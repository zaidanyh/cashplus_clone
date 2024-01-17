package com.pasukanlangit.id.core

import android.content.Context
import android.content.Intent

/** This is for activity path between module that not has relation */
const val MAIN_ACTIVITY_PATH = "com.pasukanlangit.cashplus.MainActivityNavComp"
const val KYC_INIT_PATH = "com.pasukanlangit.id.kyc_presentation.pages.init.StartedEKYCActivity"
const val CASHGOLD_WITHDRAW_HOME_PATH = "com.pasukanlangit.id.ui_cashgold_withdraw.home.CashGoldWDActivity"
const val CASHGOD_ADDRESS_HOME_PATH = "com.pasukanlangit.id.ui_cashgold_address.home.AddressGoldHomeActivity"
const val UPDATE_PROFILE_CASHGOLD_PATH = "com.pasukanlangit.id.ui_cashgold_updateprofile.UpdateProfileCashGoldActivity"
const val REGISTER_DOWNLINE_PATH = "com.pasukanlangit.id.ui_downline_register.RegisterDownLineActivity"
const val TOPUP_SALDO_AGEN_PATH = "com.pasukanlangit.id.ui_downline_transfersaldo.TransferSaldoAgenActivity"
const val MUTASI_SUMMARY_DETAIL_DOWNLINE_PATH = "com.pasukanlangit.id.ui_downline_mutasi_summary_detail.MutationSumDetActivity"
const val NEAR_AGENT_PATH = "com.pasukanlangit.id.downline_nearby_agent.NearbyAgentActivity"
const val NOBU_PATH = "com.pasukanlangit.id.nobu.presentation.LoadingStateActivity"
const val SCAN_PATH = "com.pasukanlangit.id.nobu.presentation.scan.QrScanActivity"
const val UNBIND_NOBU_PATH = "com.pasukanlangit.id.nobu.presentation.binding.UnbindingActivity"
const val NOBU_HISTORY_PATH = "com.pasukanlangit.id.nobu.presentation.history.HistoryNobuActivity"
const val CHANGE_PROFILE_PATH = "com.pasukanlangit.cashplus.ui.pages.others.settings.profile.ChangeProfilActivity"
const val FLIGHT_PATH = "com.pasukanlangit.id.travelling.flight.FlightActivity"
const val HOTEL_PATH = "com.pasukanlangit.id.travelling.hotel.InitialHotelActivity"
const val HOTEL_VIEW_ALL_PATH = "com.pasukanlangit.id.travelling.hotel.viewall.HotelViewAllActivity"
const val DETAIL_HOTEL_PATH = "com.pasukanlangit.id.travelling.hotel.find.FindDetailHotelActivity"
const val TRAIN_PATH = "com.pasukanlangit.id.travelling.train.init.InitialTrainActivity"
const val SHIP_PATH = "com.pasukanlangit.id.travelling.ship.init.InitialShipActivity"
const val LOCAL_TRANSFER_PATH = "com.pasukanlangit.id.cash_transfer.presentation.local.ListRekeningActivity"
const val GLOBAL_TRANSFER_PATH = "com.pasukanlangit.id.cash_transfer.presentation.global.GlobalTransferActivity"
const val CASHPLUS_BALANCE_TOP_UP = "com.pasukanlangit.cash_topup.presentation.InitialTopUpActivity"
const val CLOSEST_AGENT_PATH_CLASS = "com.pasukanlangit.id.downline_nearby_agent.NearbyAgentActivity"
const val RECAPITULATION_PROFIT_PATH = "com.pasukanlangit.id.recapitulation.presentation.profit.ProfitRecapitulationActivity"
const val RECAPITULATION_DEPOSIT_PATH = "com.pasukanlangit.id.recapitulation.presentation.deposit.DepositRecapitulationActivity"

/** This is for key between module for sending data that not has relation */
const val MAIN_ACTIVITY_KEY_CALLBACK_MESSAGE = "CALLBACK_MESSAGE"
const val MAIN_ACTIVITY_FORWARDING_TO_HISTORY = "FORWARDING_TO_HISTORY"
const val MAIN_ACTIVITY_FORWARDING_TO_HELP = "FORWARDING_TO_HELP"
const val MUTASI_SUMMARY_DETAIL_DOWNLINE_KEY_PHONE_NUMBER = "PHONE_NUMBER_DOWNLINE"
const val MUTASI_SUMMARY_DETAIL_DOWNLINE_KEY_IS_FROM_SUBDOWNLINE = "IS_FROM_SUB_DOWNLINE"
const val MUTASI_SUMMARY_DETAIL_DOWNLINE_KEY_NAME = "NAME_DOWNLINE"
const val PROFILE_KEY_DATA = "PROFILE_DATA"
const val CHANGE_PROFILE_FROM_EKYC = "change_profile_from_EKyc"

const val HOTEL_CITY_SELECTED = "travelling_city_selected"
const val CHECK_IN_HOTEL = "travelling_check_in_hotel"
const val CHECK_OUT_HOTEL = "travelling_check_out_hotel"
const val HOTEL_ROOM_VISITOR = "travelling_room_visitor_hotel"
const val HOTEL_CODE = "hotel_code"
const val IS_DESTINATION_EDIT = "is_destination_edit"
const val STATE_REQUEST_FLIP_ACCEPT_KEY = "state_request_flip_accept_key"


object ModuleRoute {
    fun internalIntent(context: Context, className: String) = Intent(context,
        Class.forName(className)).setPackage(context.packageName)
}
