package com.magnise.fintatech.utils

import androidx.compose.runtime.saveable.Saver
import com.magnise.fintatech.data.models.Gics
import com.magnise.fintatech.data.models.Instrument
import com.magnise.fintatech.data.models.Mappings
import com.magnise.fintatech.data.models.Profile

val InstrumentListSaver = Saver<List<Instrument>, List<List<Any?>>>(
    save = { instruments ->
        instruments.map { instrument ->
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
        }
    },
    restore = { savedList ->
        savedList.map { savedInstrument ->
            Instrument(
                id = savedInstrument[0] as String,
                symbol = savedInstrument[1] as String,
                kind = savedInstrument[2] as String,
                description = savedInstrument[3] as String,
                tickSize = (savedInstrument[4] as Number).toDouble(),
                currency = savedInstrument[5] as String,
                baseCurrency = savedInstrument[6] as String,
                mappings = Mappings(), // Используем пустой Mappings
                profile = Profile(
                    name = savedInstrument[7] as String,
                    gics = Gics(sector = savedInstrument[8] as String)
                )
            )
        }
    }
)
