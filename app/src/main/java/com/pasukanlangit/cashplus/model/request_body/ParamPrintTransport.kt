package com.pasukanlangit.cashplus.model.request_body

data class ParamPrintTransport(
	val date: String,
	val booking_code: String,
	val print_category: String,
	val passengers: List<ParamPassengersItem>,
	val transport_name: String,
	val departure_date: String,
	val transport_rute: String,
	val departure_time: String,
	val transport_number: String,
	val transport_class: String
)

data class ParamPassengersItem(
	val seat: String,
	val name: String,
	val gerbong: String,
	val id: String,
	val title: String,
	val type: String
)
