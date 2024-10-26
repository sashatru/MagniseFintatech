package com.magnise.fintatech.utils

import androidx.compose.runtime.saveable.Saver
import com.magnise.fintatech.data.models.Gics
import com.magnise.fintatech.data.models.Instrument
import com.magnise.fintatech.data.models.Mappings
import com.magnise.fintatech.data.models.Profile

val InstrumentSaver = Saver<Instrument, List<Any?>>(
    save = { instrument ->
        listOf(
            instrument.id,
            instrument.symbol,
            instrument.kind,
            instrument.description,
            instrument.tickSize,
            instrument.currency,
            instrument.baseCurrency,
            instrument.profile.name,
            instrument.profile.gics.sector
        )
    },
    restore = { savedList ->
        Instrument(
            id = savedList[0] as String,
            symbol = savedList[1] as String,
            kind = savedList[2] as String,
            description = savedList[3] as String,
            tickSize = (savedList[4] as Number).toDouble(),
            currency = savedList[5] as String,
            baseCurrency = savedList[6] as String,
            mappings = Mappings(), // Assuming mappings can be empty or default here
            profile = Profile(
                name = savedList[7] as String,
                gics = Gics(sector = savedList[8] as String)
            )
        )
    }
)
