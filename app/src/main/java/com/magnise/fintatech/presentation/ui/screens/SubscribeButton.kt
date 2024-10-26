package com.magnise.fintatech.presentation.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.magnise.fintatech.presentation.ui.theme.MagniseTheme


@Composable
fun SubscribeButton(
    instrumentId: String,
    onSubscribeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onSubscribeClick,
        modifier = modifier.width(120.dp)
    ) {
        Text("Subscribe")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSubscribeButton() {
    MagniseTheme {
        SubscribeButton(
            instrumentId = "BTC/USD",
            onSubscribeClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}