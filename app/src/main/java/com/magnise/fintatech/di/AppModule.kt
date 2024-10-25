package com.magnise.fintatech.di

import com.magnise.fintatech.data.remote.api.AuthenticationManager
import com.magnise.fintatech.data.remote.providers.EncryptedSharedPreferencesProvider
import com.magnise.fintatech.data.remote.providers.HttpClientProvider
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

    // Provide AuthenticationManager to handle token storage and refresh
    single { AuthenticationManager(get(), get()) }

    // Provide Use Cases
    factory { LoginUseCase(get()) }

    // Provide ViewModel
    viewModel { MarketViewModel(get()) }

    //single { MarketApi(get()) }
    /*   single { WebSocketManager() }
       single { MarketRepository(get(), get()) }
       viewModel { MarketViewModel(get()) }
   */
}
