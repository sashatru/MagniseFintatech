package com.magnise.fintatech.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.magnise.fintatech.data.models.Gics
import com.magnise.fintatech.data.models.Instrument
import com.magnise.fintatech.data.models.Mappings
import com.magnise.fintatech.data.models.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstrumentSpinner(
    instruments: List<Instrument>,
    onInstrumentSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedInstrument by remember { mutableStateOf(instruments.first()) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedInstrument.symbol,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Instrument") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            instruments.forEach { instrument ->
                DropdownMenuItem(
                    text = { Text(instrument.symbol) },
                    onClick = {
                        selectedInstrument = instrument
                        onInstrumentSelected(instrument.id)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewInstrumentSpinner() {
    // Sample preview with mock instruments
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
    InstrumentSpinner(instruments = mockInstruments, onInstrumentSelected = {})
}
