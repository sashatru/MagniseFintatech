package com.magnise.fintatech.di

import com.magnise.fintatech.data.remote.api.ApiManager
import com.magnise.fintatech.data.remote.api.WebSocketManager
import com.magnise.fintatech.data.remote.providers.EncryptedSharedPreferencesProvider
import com.magnise.fintatech.data.remote.providers.HttpClientProvider
import com.magnise.fintatech.data.repository.TokenRepository
import com.magnise.fintatech.domain.usecases.GetHistoricalPriceUseCase
import com.magnise.fintatech.domain.usecases.GetInstrumentsUseCase
import com.magnise.fintatech.domain.usecases.GetRealDataUseCase
import com.magnise.fintatech.domain.usecases.LoginUseCase
import com.magnise.fintatech.presentation.viewmodel.MarketViewModel
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
// Provide HttpClient using your custom HttpClientProvider with OkHttp
    single<HttpClient> {
        HttpClientProvider.create()
    }

    // Provide EncryptedSharedPreferences
    single {
        EncryptedSharedPreferencesProvider.create(androidContext())
    }

    // Provide TokenRepository
    single { TokenRepository(get()) }

    // Provide AuthenticationManager to handle tokens
    single { ApiManager(get(), get()) }
    //Provide WebSocketManager
    factory { WebSocketManager(get()) }

    // Provide Use Cases
    factory { LoginUseCase(get()) }
    factory { GetInstrumentsUseCase(get()) }
    factory { GetRealDataUseCase(get()) }
    factory { GetHistoricalPriceUseCase(get()) }

    // Provide ViewModel
    viewModel { MarketViewModel(get(), get(), get(), get()) }
}
