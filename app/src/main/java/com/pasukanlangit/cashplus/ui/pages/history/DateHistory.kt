package com.pasukanlangit.cashplus.ui.pages.history

data class DateHistory (
    var dateStart: String,
    var dateEnd: String ?= dateStart,
    var dateLabel: String ?= "Hari ini"
)