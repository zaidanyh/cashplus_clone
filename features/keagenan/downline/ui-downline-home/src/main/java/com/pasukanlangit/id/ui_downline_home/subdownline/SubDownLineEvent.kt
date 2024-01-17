package com.pasukanlangit.id.ui_downline_home.subdownline

sealed class SubDownLineEvent {
    data class SetDatePicker(val dateStart: String, val dateEnd: String): SubDownLineEvent()
}
