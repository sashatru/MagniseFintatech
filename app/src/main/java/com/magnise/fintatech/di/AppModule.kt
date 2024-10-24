package com.magnise.fintatech.di

import com.magnise.fintatech.data.remote.api.MarketApi
import com.magnise.fintatech.data.remote.providers.EncryptedSharedPreferencesProvider
import com.magnise.fintatech.data.remote.providers.HttpClientProvider
import io.ktor.client.HttpClient
import io.ktor.client.features.logging.Logging
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
// Provide HttpClient using your custom HttpClientProvider with OkHttp
    single<HttpClient> {
        HttpClientProvider.create()
    }

    // Provide EncryptedSharedPreferences via the new provider
    single {
        EncryptedSharedPreferencesProvider.create(androidContext())
    }

    // Provide AuthenticationManager to handle token storage and refresh
    single { AuthenticationManager(get(), get()) }


    single { MarketApi(get()) }
 /*   single { WebSocketManager() }
    single { MarketRepository(get(), get()) }
    viewModel { MarketViewModel(get()) }
*/
}
