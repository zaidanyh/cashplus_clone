package com.pasukanlangit.id.travelling.datasource.mapper

import com.pasukanlangit.id.travelling.datasource.network.dto.*
import com.pasukanlangit.id.travelling.domain.model.flight.*
import com.pasukanlangit.id.core.model.BalanceRequestCore
import com.pasukanlangit.id.core.model.BalanceResponseCore
import com.pasukanlangit.id.travelling.domain.model.*
import com.pasukanlangit.id.travelling.domain.model.hotel.*
import com.pasukanlangit.id.travelling.domain.model.ship.*
import com.pasukanlangit.id.travelling.domain.model.train.*

fun BalanceRequest.toBalanceRequestDto(): BalanceRequestCore = BalanceRequestCore(this.uuid)
fun BalanceResponseCore.toBalanceResponse(): BalanceResponse = BalanceResponse(this.balance)

fun AirportRequest.toAirportRequestDto(): AirportRequestDto =
    AirportRequestDto(code = this.code, city = this.city, group = this.group, uuid = this.uuid)

fun AirportsResponseDto.toListAirportResponse(): AirportsResponse =
    AirportsResponse(
        rc = this.rc, message = this.message,
        data = this.data?.map {
            Airports(
                code = it.code, city = it.city, group = it.group,
                airport = it.airport, status = it.status
            )
        }
    )

fun FlightScheduleRequest.toFlightScheduleRequestDto(): FlightScheduleRequestDto =
    FlightScheduleRequestDto(uuid = this.uuid, from = this.from, to = this.to, date = this.date)

fun FlightScheduleResponseDto.toListFlightScheduleResponse(): FlightScheduleResponse =
    FlightScheduleResponse(
        rc = this.rc, message = (this.message ?: this.msg).toString(),
        data = this.data?.map {
            FlightSchedule(
                flightId = it.flightId, flight = it.flight, flightCode = it.flightCode, flightImage = it.flightImage,
                flightFrom = it.flightFrom, flightTo = it.flightTo, flightRoute = it.flightRoute, flightDate = it.flightDate,
                flightTransit = it.flightTransit, flightInfoTransit = it.flightInfotransit, flightTime = it.flightDatetime,
                flightDuration = it.flightDuration, flightPrice = it.flightPrice, flightPublishFare = it.flightPublishFare,
                flightSeatAvailable = it.flightSeatavail, flightBaggage = it.flightBaggage, flightFacilities = it.flightFacilities
            )
        }
    )

fun FlightPriceRequest.toFlightPriceRequestDto(): FlightPriceRequestDto =
    FlightPriceRequestDto(
        date = this.date, flightCode = this.flightCode, from = this.from, to = this.to,
        adult = this.adult, child = this.child, infant = this.infant, uuid = this.uuid
    )

fun FlightPriceResponseDto.toFlightPriceResponse(): FlightPriceResponse =
    this.data.let {
        FlightPriceResponse(
            flight = it?.flight.toString(), flightCode = it?.flightCode.toString(), flightFrom = it?.flightFrom.toString(),
            flightTo = it?.flightTo.toString(), flightRoute = it?.flightRoute.toString(), flightDate = it?.flightDate.toString(),
            flightDeparture = it?.flightDeparture.toString(), flightSeatAvailable = it?.flightAvailableSeat.toString(),
            flightTransit = it?.flightTransit.toString(), flightInfoTransit = it?.flightInfoTransit.toString(),
            flightTime = it?.flightTime.toString(), flightClass = it?.flightClass.toString(), adult = it?.adult ?: 1,
            child = it?.child ?: 0, infant = it?.infant ?: 0, publish = it?.publish ?: 0, tax = it?.tax ?: 0,
            totalFare = it?.totalFare ?: 0,
        )
    }

fun FlightBookingRequest.toFlightBookingRequestDto(): FlightBookingRequestDto =
    FlightBookingRequestDto(
        uuid = this.uuid, from = this.from, to = this.to, date = this.date, flightCode = this.flightCode,
        adult = this.adult, child = this.child, infant = this.infant, email = this.email, phone = this.phone,
        passengerName = this.passengersName, dateofBirth = this.passengersDateOfBirth, baggageVolume = this.baggageVolumes,
        passportNumber = this.passportsNumbers, passportExpired = this.passportsExpired
    )

fun BookingResponseDto.toBookingCodeResponse(): BookingCodeResponse =
    BookingCodeResponse(
        rc = this.rc, message = (this.message ?: this.msg).toString(),
        bookingCode = this.data?.bookingCode.toString()
    )

fun HotelCityListRequest.toHotelCityListRequestDto(): HotelCityListRequestDto =
    HotelCityListRequestDto(uuid = this.uuid, hotelCityName = this.hotelCityName)

fun HotelCityListResponseDto.toHotelCityListResponse(): HotelCityListResponse =
    HotelCityListResponse(
        rc = this.rc, message = (this.message ?: this.msg).toString(),
        data = this.data?.map {
            HotelCityList(
                hotelCityCode = it.hotelCityCode,
                hotelCityName = it.hotelCityName
            )
        }
    )

fun FindHotelRequest.toFindHotelRequestDto(): FindHotelRequestDto =
    FindHotelRequestDto(
        uuid = this.uuid, hotelCityCode = this.hotelCityCode, hotelCode = this.hotelCode,
        hotelCheckin = this.hotelCheckin, hotelCheckout = this.hotelCheckout, hotelRoom = this.hotelRoom
    )

fun FindHotelByCityRequest.toFindHotelRequestDto(): FindHotelRequestDto =
    FindHotelRequestDto(
        uuid = this.uuid, hotelCityCode = this.hotelCityCode, hotelCheckin = this.hotelCheckin,
        hotelCheckout = this.hotelCheckout, hotelRoom = this.hotelRoom
    )

fun FindHotelResponseDto.toFindHotelResponse(): FindHotelResponse =
    FindHotelResponse(
        rc = this.rc, message = (this.message ?: this.msg).toString(),
        data = this.data?.dataHotel?.map {
            Hotels(
                key = it.hotelKey, code = it.hotelCode, name = it.hotelName, rating = it.hotelRating,
                address = it.hotelAddress, city = it.hotelCity, country = it.hotelCountry, latitude = it.hotelLatitude,
                longitude = it.hotelLongitude, marketName = it.hotelMarketname.toString(), checkin = it.hotelCheckin,
                checkout = it.hotelCheckout, images = it.hotelImage, remark = it.hotelRemark, extraBeds = it.hotelExtrabeds,
                facilities = it.hotelFacilities, room = it.hotelRoom?.map { roomHotel ->
                    RoomHotel(
                        rateKey = roomHotel.roomRateKey, code = roomHotel.roomCode, name = roomHotel.roomName,
                        request = roomHotel.roomRequest, price = roomHotel.roomPrice, nta = roomHotel.roomNta,
                        image = roomHotel.roomImage, boardName = roomHotel.roomBoardname, cancellation = roomHotel.roomCancellation,
                        included = roomHotel.roomIncluded
                    )
                }
            )
        }
    )

fun HotelBookingRequest.toBookingHotelRequestDto(): BookingHotelRequestDto =
    BookingHotelRequestDto(
        uuid = this.uuid, hotelCode = this.hotelCode, hotelKey = this.hotelKey, roomRateKey = this.rateKey,
        hotelCheckin = this.checkIn, hotelCheckout = this.checkOut, hotelRoom = this.hotelRoom,
        hotelPaxname = this.paxName, email = this.email, phone = this.phone, hotelRequest = this.hotelRequest
    )

fun BookingResponseDto.toPaymentCodeResponse():  BookingCodeResponse =
    BookingCodeResponse(
        rc = this.rc, message = (this.message ?: this.msg).toString(),
        bookingCode = this.data?.paymentCode
    )

fun StationsRequest.toStationsRequestDto(): StationsRequestDto =
    StationsRequestDto(uuid = this.uuid, code = this.code, location = this.location)

fun StationsResponseDto.toStationsResponse(): StationsResponse =
    StationsResponse(
        rc = this.rc, message = (this.message ?: this.msg).toString(),
        data = this.data?.map { item ->
            Stations(
                code = item.code, location = item.location
            )
        }
    )

fun TrainScheduleRequest.toTrainScheduleRequestDto(): TrainScheduleRequestDto =
    TrainScheduleRequestDto(
        date = this.date, from = this.from, to = this.to, adult = this.adult, infant = this.infant, uuid = this.uuid
    )

fun TrainScheduleResponseDto.toTrainScheduleResponse(): TrainScheduleResponse =
    TrainScheduleResponse(
        rc = this.rc, message = (this.message ?: this.msg).toString(),
        session = this.data?.session.toString(),
        data = this.data?.schedule?.map {
            TrainItemSchedule(
                name = it.trainName, code = it.trainCode, id = it.trainId, from = it.trainFrom,
                to = it.trainTo, route = it.trainRoute, date = it.trainDate, timeDeparture = it.trainDeparture,
                timeArrival = it.trainArrival, trainDateTime = it.trainDatetime, price = it.trainFare,
                trainClass = it.trainClass, subClass = it.trainSubclass, available = it.trainAvailable
            )
        }
    )

fun TrainPriceRequest.toTrainPriceRequestDto(): TrainPriceRequestDto =
    TrainPriceRequestDto(
        uuid = this.uuid, session = this.session, from = this.from, to = this.to, date = this.date,
        adult = this.adult, infant = this.infant, trainCode = this.trainCode, trainClass = this.trainClass,
        trainSubclass = this.trainSubClass
    )

fun TrainPriceResponseDto.toTrainPriceResponse(): TrainPriceResponse =
    TrainPriceResponse(
        rc = this.rc, message = (this.message ?: this.msg).toString(),
        trainPrice = this.data?.let { itemPrice ->
            TrainPrice(
                name = itemPrice.trainName, code = itemPrice.trainCode, id = itemPrice.trainId,
                route = itemPrice.trainRoute, basicFare = itemPrice.trainBasicfare,
                serviceCharge = itemPrice.trainServicecharge, totalFare = itemPrice.trainTotalfare
            )
        }
    )

fun TrainBookingRequest.toTrainBookingRequestDto(): TrainBookingRequestDto =
    TrainBookingRequestDto(
        uuid = this.uuid, session = this.session, from = this.from, to = this.to, date = this.date,
        adult = this.adult, infant = this.infant, trainCode = this.trainCode, trainClass = this.trainClass,
        trainSubclass = this.trainSubClass, passengerName = this.passengerName, idNumber = this.idNumber,
        phone = this.phone, email = this.email
    )

fun HarborCitiesRequest.toHarborRequestDto(): HarborsRequestDto =
    HarborsRequestDto(uuid = this.uuid, code = this.code, name = this.name)

fun HarborsResponseDto.toHarborCitiesResponse(): HarborCitiesResponse =
    HarborCitiesResponse(
        rc = this.rc, message = (this.message ?: this.msg).toString(),
        data = this.data?.map {
            Harbors(code = it.harborCode, name = it.harborName)
        }
    )

fun HarborScheduleRequest.toHarborScheduleRequestDto(): ShipScheduleRequestDto =
    ShipScheduleRequestDto(
        uuid = this.uuid, from = this.from, to = this.to, date = this.date, adult = this.adult,
        infant = this.infant, male = this.male, female = this.female
    )

fun ShipScheduleResponseDto.toHarborScheduleResponse(): HarborScheduleResponse =
    HarborScheduleResponse(
        rc = this.rc, message = (this.message ?: this.msg).toString(),
        data = this.data?.schedule?.map {
            HarborSchedules(
                name = it.shipName, number = it.shipNumber, code = it.shipCode, from = it.shipFrom,
                to = it.shipTo, route = it.shipRoute, date = it.shipDate, dateTime = it.shipDatetime,
                infoDateTime = it.shipInfodatetime, infoRoute = it.shipInforoute, shipClass = it.shipClass,
                basicFare = it.shipBasicfare, admin = it.shipAdmin, price = it.shipPrice,
                maleSeat = it.shipMaleSeat, femaleSeat = it.shipFemaleSeat
            )
        }
    )

fun ShipBookingRequest.toShipBookingRequestDto(): ShipBookRequestDto =
    ShipBookRequestDto(
        uuid = this.uuid, from = this.from, to = this.to, date = this.date, shipName = this.shipName,
        shipNumber = this.shipNumber, shipCode = this.shipCode, shipClass = this.shipClass,
        adult = this.adult, infant = this.infant, passengername = this.passengername, dateofbirth = this.dateofbirth,
        idnumber = this.idnumber, phone = this.phone, email = this.email
    )

fun TransactionRequest.toTransactionRequestDto(): TransactionRequestDto =
    TransactionRequestDto(uuid = this.uuid, codeProduct = this.productCode, destination = this.destination, pin = this.pin)

fun TransactionResponseDto.toTransactionResponse(): TransactionResponse =
    TransactionResponse(
        phone = this.phone, dsc = this.dsc.toString(), fee = this.fee.toString(),
        price = this.price.toString(), admin = this.billData?.adminCost.toString(),
        info = this.billData?.info.toString(), total = this.billData?.total.toString()
    )