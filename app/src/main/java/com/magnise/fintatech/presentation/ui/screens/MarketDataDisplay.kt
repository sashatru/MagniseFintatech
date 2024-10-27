package com.magnise.fintatech.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.magnise.fintatech.data.models.PriceData
import com.magnise.fintatech.presentation.ui.theme.MagniseTheme
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MarketDataDisplay(
    symbol: String = "no data",
    lastPriceData: PriceData?
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Market data:", style = MaterialTheme.typography.titleSmall)

            Spacer(modifier = Modifier.height(8.dp))

            // Main data row with symbol, price, and time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Symbol:\n$symbol", style = MaterialTheme.typography.bodyMedium)

                lastPriceData?.let { priceData ->
                    val formattedPrice = formatPrice(priceData.price)
                    val formattedTime = formatTimestamp(priceData.timestamp)
                    LastData(price = formattedPrice, time = formattedTime)
                }?: LastData(price = "no data", time = "no data")
            }
        }
    }
}

@Composable
fun LastData (price: String, time: String){
    Text(
        text = "Price:\n$price",
        style = MaterialTheme.typography.bodyMedium
    )
    Text(
        text = "Time:\n$time",
        style = MaterialTheme.typography.bodyMedium
    )
}


// Helper function to format the timestamp as per the example (e.g., Aug 7, 9:45 AM)
fun formatTimestamp(timestamp: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = parser.parse(timestamp)
        date?.let {
            val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
            formatter.format(it)
        } ?: timestamp // Fallback to the raw timestamp if parsing fails
    } catch (e: Exception) {
        timestamp // Fallback to the raw timestamp if parsing fails
    }
}

// Helper function to format the price with three decimal places
fun formatPrice(price: Double): String {
    val decimalFormat = DecimalFormat("#,##0.000")
    return "$${decimalFormat.format(price)}"
}

@Preview(showBackground = true)
@Composable
fun PreviewMarketDataDisplay() {
    val mockLastPriceData = PriceData(
        timestamp = "2024-08-07T09:45:00Z",
        price = 48126.333,
        volume = 100
    )

    MagniseTheme {
        MarketDataDisplay(
            symbol = "BTC/USD",
            lastPriceData = mockLastPriceData
        )
    }
}
