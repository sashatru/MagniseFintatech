package com.magnise.fintatech.di

import com.magnise.fintatech.data.remote.api.AuthenticationManager
import com.magnise.fintatech.data.remote.providers.EncryptedSharedPreferencesProvider
import com.magnise.fintatech.data.remote.providers.HttpClientProvider
import com.magnise.fintatech.data.repository.TokenRepository
import com.magnise.fintatech.domain.usecase.GetInstrumentsUseCase
import com.magnise.fintatech.domain.usecase.LoginUseCase
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
    single { AuthenticationManager(get(), get()) }

    // Provide Use Cases
    factory { LoginUseCase(get()) }
    factory { GetInstrumentsUseCase(get()) }

    // Provide ViewModel
    viewModel { MarketViewModel(get(), get()) }

    //single { MarketApi(get()) }
    /*   single { WebSocketManager() }
       single { MarketRepository(get(), get()) }
       viewModel { MarketViewModel(get()) }
   */
}
