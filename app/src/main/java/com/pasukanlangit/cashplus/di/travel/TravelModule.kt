package com.pasukanlangit.cashplus.di.travel

import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.library_core.datasource.utils.GlobalErrorParser
import com.pasukanlangit.id.travelling.datasource.TravellingRepositoryImpl
import com.pasukanlangit.id.travelling.datasource.network.TravellingService
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.usecases.BalanceUseCase
import com.pasukanlangit.id.travelling.domain.usecases.BookingUseCase
import com.pasukanlangit.id.travelling.domain.usecases.TravellingUseCases
import com.pasukanlangit.id.travelling.domain.usecases.flight.GetAirportList
import com.pasukanlangit.id.travelling.domain.usecases.flight.GetFlightBooking
import com.pasukanlangit.id.travelling.domain.usecases.flight.GetFlightPrice
import com.pasukanlangit.id.travelling.domain.usecases.flight.GetFlightSchedule
import com.pasukanlangit.id.travelling.domain.usecases.hotel.FindHotel
import com.pasukanlangit.id.travelling.domain.usecases.hotel.FindHotelByCity
import com.pasukanlangit.id.travelling.domain.usecases.hotel.GetCityList
import com.pasukanlangit.id.travelling.domain.usecases.hotel.GetHotelBooking
import com.pasukanlangit.id.travelling.domain.usecases.ship.GetHarborList
import com.pasukanlangit.id.travelling.domain.usecases.ship.GetHarborSchedule
import com.pasukanlangit.id.travelling.domain.usecases.ship.GetShipBooking
import com.pasukanlangit.id.travelling.domain.usecases.train.GetStationsList
import com.pasukanlangit.id.travelling.domain.usecases.train.GetTrainBooking
import com.pasukanlangit.id.travelling.domain.usecases.train.GetTrainPrice
import com.pasukanlangit.id.travelling.domain.usecases.train.GetTrainSchedule
import com.pasukanlangit.id.travelling.flight.FlightViewModel
import com.pasukanlangit.id.travelling.flight.detail.FlightPriceViewModel
import com.pasukanlangit.id.travelling.flight.schedule.FlightScheduleViewModel
import com.pasukanlangit.id.travelling.hotel.InitialHotelViewModel
import com.pasukanlangit.id.travelling.hotel.fill.FillDataViewModel
import com.pasukanlangit.id.travelling.hotel.find.FindDetailHotelViewModel
import com.pasukanlangit.id.travelling.hotel.findbycity.FindHotelByCityViewModel
import com.pasukanlangit.id.travelling.ship.fill.FillDataShipViewModel
import com.pasukanlangit.id.travelling.ship.init.InitialShipViewModel
import com.pasukanlangit.id.travelling.ship.schedule.ShipScheduleViewModel
import com.pasukanlangit.id.travelling.train.detailprice.TrainPriceViewModel
import com.pasukanlangit.id.travelling.train.init.InitialTrainViewModel
import com.pasukanlangit.id.travelling.train.schedule.TrainScheduleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

private fun provideTravelServices(retrofit: Retrofit): TravellingService =
    retrofit.create(TravellingService::class.java)

private fun provideRepository(travellingService: TravellingService, sharedPref: SharedPrefDataSource, errorParser: GlobalErrorParser): TravellingRepository =
    TravellingRepositoryImpl(
        travellingApi = travellingService,
        sharedPref = sharedPref,
        errorParser = errorParser
    )

private fun provideTravelUseCases(travellingRepository: TravellingRepository): TravellingUseCases =
    TravellingUseCases(
        checkBalance = BalanceUseCase(travellingRepository),
        booking = BookingUseCase(travellingRepository),

        airportList = GetAirportList(travellingRepository),
        flightSchedule = GetFlightSchedule(travellingRepository),
        flightPrice = GetFlightPrice(travellingRepository),
        flightBooking = GetFlightBooking(travellingRepository),

        hotelCityList = GetCityList(travellingRepository),
        findHotel = FindHotel(travellingRepository),
        findHotelByCity = FindHotelByCity(travellingRepository),
        hotelBooking = GetHotelBooking(travellingRepository),

        stations = GetStationsList(travellingRepository),
        trainSchedule = GetTrainSchedule(travellingRepository),
        trainPrice = GetTrainPrice(travellingRepository),
        trainBooking = GetTrainBooking(travellingRepository),

        harbors = GetHarborList(travellingRepository),
        harborSchedule = GetHarborSchedule(travellingRepository),
        shipBooking =  GetShipBooking(travellingRepository)
    )

val travelModule = module {
    single { provideTravelServices(get()) }
    single { provideRepository(get(), get(), get()) }
    single { provideTravelUseCases(get()) }

    viewModel { FlightViewModel(get()) }
    viewModel { FlightScheduleViewModel(get()) }
    viewModel { FlightPriceViewModel(get()) }

    viewModel { InitialHotelViewModel(get()) }
    viewModel { FindHotelByCityViewModel(get()) }
    viewModel { FindDetailHotelViewModel(get()) }
    viewModel { FillDataViewModel(get()) }

    viewModel { InitialTrainViewModel(get()) }
    viewModel { TrainScheduleViewModel(get()) }
    viewModel { TrainPriceViewModel(get()) }

    viewModel { InitialShipViewModel(get()) }
    viewModel { ShipScheduleViewModel(get()) }
    viewModel { FillDataShipViewModel(get()) }
}