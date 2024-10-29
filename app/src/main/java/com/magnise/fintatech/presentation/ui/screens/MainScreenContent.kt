package com.magnise.fintatech.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.magnise.fintatech.R
import com.magnise.fintatech.presentation.viewmodel.MarketViewModel
import com.magnise.fintatech.utils.AuthState
import com.magnise.fintatech.utils.InstrumentListSaver
import org.koin.androidx.compose.getViewModel
import timber.log.Timber


@Composable
fun MainScreenContent(modifier: Modifier = Modifier) {
    val viewModel: MarketViewModel = getViewModel()

    val authState by viewModel.authState.collectAsState()
    val instrumentsState = viewModel.instruments.collectAsState(initial = emptyList())
    var instruments by rememberSaveable(stateSaver = InstrumentListSaver) { mutableStateOf(value = instrumentsState.value) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {


        Spacer(modifier = Modifier.height(24.dp))

        // Navigate to the MarketScreen if authentication is successful
        when (authState) {
            is AuthState.Loading -> {
                // Show loading indicator while authentication is in progress
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                )

                {
                    // Display the logo at the top
                    Logo()
                    CircularProgressIndicator()
                }
            }

            is AuthState.Authenticated -> {
                Timber.tag("Instruments")
                    .d("MSC AuthState.Authenticated, instruments %s\n%s", instruments, instrumentsState.value)

                if (instrumentsState.value.isNotEmpty() && instruments.isEmpty()) {
                    instruments = instrumentsState.value
                }

                MarketScreen(
                    instruments = instruments,
                )
            }

            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Display the logo at the top
                    Image(
                        painter = painterResource(id = R.drawable.logo_magnise),
                        contentDescription = "Magnise Logo",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Fit
                    )
                    // Show error message if authentication fails
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Logo() {
    Image(
        painter = painterResource(id = R.drawable.logo_magnise),
        contentDescription = "Magnise Logo",
        modifier = Modifier.size(200.dp),
        contentScale = ContentScale.Fit
    )
}

/*
// Mock ViewModel for Preview
class MockMarketViewModel : MarketViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Error("Not authenticated"))
    override val authState: StateFlow<AuthState> = _authState
}

// Preview with Mock ViewModel
@Preview(showBackground = true)
@Composable
fun MainScreenContentPreview() {
    MainScreenContent(viewModel = MockMarketViewModel())
}*/
