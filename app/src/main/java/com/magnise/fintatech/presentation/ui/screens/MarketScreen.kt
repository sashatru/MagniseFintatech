package com.magnise.fintatech.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.magnise.fintatech.data.models.Instrument
import com.magnise.fintatech.presentation.viewmodel.MarketViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import timber.log.Timber

@Composable
fun MarketScreen(
    instruments: List<Instrument>
) {
    val marketViewModel: MarketViewModel = getViewModel()
    val coroutineScope = rememberCoroutineScope()


    Timber.tag("MarketScreen").d("MS open instruments: %s", instruments)


    val selectedInstrumentId =
        rememberSaveable { mutableStateOf(marketViewModel.selectedInstrumentId) }
    val selectedInstrumentSymbol =
        rememberSaveable { mutableStateOf(marketViewModel.selectedInstrumentSymbol) }
    val selectedInstrumentSymbolForData =
        rememberSaveable { mutableStateOf(selectedInstrumentSymbol.value) }

    val lastPriceData by marketViewModel.realTimePrice.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Row layout for InstrumentSpinner and SubscribeButton
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Add space between components
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2.0f)
            ) {
                InstrumentSpinner(
                    instruments = instruments,
                    onInstrumentSelected = { instrument ->
                        marketViewModel.selectInstrument(instrument)
                        selectedInstrumentId.value = instrument.id
                        selectedInstrumentSymbol.value = instrument.symbol
                    },
                    modifier = Modifier
                        .height(56.dp) // Set a consistent height for alignment
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f)
            ) {
                SubscribeButton(
                    onSubscribeClick = {
                        coroutineScope.launch {
                            selectedInstrumentSymbolForData.value = selectedInstrumentSymbol.value
                            selectedInstrumentId.value?.let { marketViewModel.startFetchData(it) }
                        }
                    },
                    modifier = Modifier
                        .height(56.dp) // Match the height of the spinner
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Market Data Display
        selectedInstrumentSymbolForData.value?.let {
            MarketDataDisplay(
                symbol = it,
                lastPriceData = lastPriceData
            )
        }
    }
}

