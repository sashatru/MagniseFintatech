package com.magnise.fintatech.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.magnise.fintatech.R
import com.magnise.fintatech.presentation.ui.navigation.AppNavHost
import com.magnise.fintatech.presentation.viewmodel.MarketViewModel
import com.magnise.fintatech.utils.AuthState
import org.koin.androidx.compose.getViewModel

const val USER_NAME: String = "r_test@fintatech.com"
const val PASSWORD: String = "kisfiz-vUnvy9-sopnyv"

@Composable
fun MainScreenContent(modifier: Modifier = Modifier) {
    val viewModel: MarketViewModel = getViewModel()
    val authState by viewModel.authState.collectAsState()
    val navController = rememberNavController()

    // Authentication and navigation logic
    LaunchedEffect(Unit) {
        viewModel.authenticateUser(USER_NAME, PASSWORD) // Replace with actual credentials
    }

    // Navigate to the MarketScreen if authentication is successful
    if (authState is AuthState.Authenticated) {
        //AppNavHost(navController)
    } else {
        // Show logo and error message if authentication fails
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display the logo in the center
                Image(
                    painter = painterResource(id = R.drawable.logo_magnise),
                    contentDescription = "Magnise Logo",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Fit
                )

                // If authentication failed, show the error message
                if (authState is AuthState.Error) {
                    val errorMessage = (authState as AuthState.Error).message
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
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
