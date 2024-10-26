package com.magnise.fintatech.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.magnise.fintatech.data.models.Instrument
import com.magnise.fintatech.presentation.ui.screens.MarketScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    instruments: List<Instrument>
) {
    NavHost(navController = navController, startDestination = "market") {
        // Define the Market screen
        composable("market") {
            //MarketScreen(instruments = instruments)
        }
    }
}
