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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.magnise.fintatech.data.models.Gics
import com.magnise.fintatech.data.models.HistoricalPrice
import com.magnise.fintatech.data.models.Instrument
import com.magnise.fintatech.data.models.Mappings
import com.magnise.fintatech.data.models.PriceData
import com.magnise.fintatech.data.models.Profile
import com.magnise.fintatech.presentation.ui.theme.MagniseTheme

@Composable
fun MarketScreen(
    instruments: List<Instrument>,
    onSubscribeClick: (String) -> Unit,
    lastPriceData: PriceData?,
    historicalPrices: List<HistoricalPrice>
) {
    var selectedInstrumentId by rememberSaveable { mutableStateOf(instruments.first().id) }
    var selectedInstrumentSymbol by rememberSaveable { mutableStateOf(instruments.first().symbol) }

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
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(2.0f)) {
                InstrumentSpinner(
                    instruments = instruments,
                    onInstrumentSelected = { instrument ->
                        selectedInstrumentId = instrument.id
                        selectedInstrumentSymbol = instrument.symbol
                    },
                    modifier = Modifier
                        .height(56.dp) // Set a consistent height for alignment
                )
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)) {
                SubscribeButton(
                    instrumentId = selectedInstrumentId,
                    onSubscribeClick = {
                        onSubscribeClick(selectedInstrumentId)
                    },
                    modifier = Modifier
                        .height(56.dp) // Match the height of the spinner
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Market Data Display
        MarketDataDisplay(
            symbol = selectedInstrumentSymbol,
            lastPriceData = lastPriceData
        )
    }
}

// Mock data for preview
val mockInstruments = listOf(
    Instrument(
        "1",
        "BTC/USD",
        "forex",
        "description",
        0.01,
        "USD",
        "BTC",
        Mappings(),
        Profile("BTC", Gics("Crypto"))
    ),
    Instrument(
        "2",
        "EUR/USD",
        "forex",
        "description",
        0.01,
        "USD",
        "EUR",
        Mappings(),
        Profile("EUR", Gics("Fiat"))
    )
)

val mockMarketData = PriceData(
    timestamp = "2024-08-07T09:45:00Z",
    price = 48126.333,
    volume = 100
)

// Updated mock data for HistoricalPrice
val mockHistoricalPrices = listOf(
    HistoricalPrice(
        t = "2024-08-07T09:45:00Z",
        o = 47000.0,
        h = 47500.0,
        l = 46800.0,
        c = 47100.0,
        v = 1000
    ),
    HistoricalPrice(
        t = "2024-08-07T10:00:00Z",
        o = 47100.0,
        h = 47800.0,
        l = 47000.0,
        c = 47500.0,
        v = 1200
    ),
    HistoricalPrice(
        t = "2024-08-07T10:15:00Z",
        o = 47500.0,
        h = 48000.0,
        l = 47400.0,
        c = 47800.0,
        v = 1100
    ),
    HistoricalPrice(
        t = "2024-08-07T10:30:00Z",
        o = 47800.0,
        h = 48200.0,
        l = 47700.0,
        c = 48100.0,
        v = 1300
    )
)

@Preview(showBackground = true)
@Composable
fun PreviewMarketScreen() {
    MagniseTheme {
        MarketScreen(
            instruments = mockInstruments,
            onSubscribeClick = {},
            lastPriceData = mockMarketData,
            historicalPrices = mockHistoricalPrices
        )
    }
}