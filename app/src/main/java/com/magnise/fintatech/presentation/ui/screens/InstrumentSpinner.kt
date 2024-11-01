package com.magnise.fintatech.presentation.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.magnise.fintatech.data.models.Gics
import com.magnise.fintatech.data.models.Instrument
import com.magnise.fintatech.data.models.Mappings
import com.magnise.fintatech.data.models.Profile
import com.magnise.fintatech.presentation.ui.theme.MagniseTheme
import com.magnise.fintatech.utils.InstrumentSaver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstrumentSpinner(
    instruments: List<Instrument>,
    onInstrumentSelected: (Instrument) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedInstrument by rememberSaveable(stateSaver = InstrumentSaver) {
        mutableStateOf(
            instruments.first()
        )
    }


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
            modifier = modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
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
                        onInstrumentSelected(instrument)
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
    MagniseTheme {
        InstrumentSpinner(instruments = mockInstruments, onInstrumentSelected = {})
    }
}
