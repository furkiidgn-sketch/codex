package com.sharkoguess.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.sharkoguess.core.designsystem.theme.SharkoGuessTheme
import com.sharkoguess.feature.artistpicker.ArtistPickerRoute
import com.sharkoguess.feature.category.CategoryRoute
import com.sharkoguess.feature.leaderboard.LeaderboardRoute
import com.sharkoguess.feature.onboarding.OnboardingRoute
import com.sharkoguess.feature.settings.SettingsRoute
import com.sharkoguess.feature.solo.SoloGameRoute
import com.sharkoguess.feature.versus.VersusMatchRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SharkoGuessTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SharkoGuessApp()
                }
            }
        }
    }
}

// region Navigation
object Destinations {
    const val Onboarding = "onboarding"
    const val ArtistPicker = "artistPicker"
    const val Category = "category/{artistId}"
    const val SoloGame = "solo/{artistId}"
    const val Versus = "versus/{matchId}"
    const val Leaderboard = "leaderboard"
    const val Settings = "settings"
}

@Composable
fun SharkoGuessApp() {
    val navController = rememberNavController()
    Column(modifier = Modifier.fillMaxSize()) {
        androidx.compose.foundation.layout.Box(modifier = Modifier.weight(1f)) {
            NavHost(navController = navController, startDestination = Destinations.Onboarding) {
        composable(Destinations.Onboarding) {
            OnboardingRoute(
                onContinue = {
                    navController.navigate(Destinations.ArtistPicker) {
                        popUpTo(Destinations.Onboarding) { inclusive = true }
                    }
                },
                viewModel = hiltViewModel()
            )
        }
        composable(Destinations.ArtistPicker) {
            ArtistPickerRoute(
                onArtistSelected = { artistId ->
                    navController.navigate("category/$artistId")
                },
                onLeaderboardClick = { navController.navigate(Destinations.Leaderboard) },
                viewModel = hiltViewModel()
            )
        }
        composable(
            route = Destinations.Category,
            arguments = listOf(navArgument("artistId") { type = NavType.StringType })
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getString("artistId") ?: return@composable
            CategoryRoute(
                artistId = artistId,
                onStartSolo = { navController.navigate("solo/$artistId") },
                onStartVersus = { matchId -> navController.navigate("versus/$matchId") },
                viewModel = hiltViewModel()
            )
        }
        composable(
            route = Destinations.SoloGame,
            arguments = listOf(navArgument("artistId") { type = NavType.StringType })
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getString("artistId") ?: return@composable
            SoloGameRoute(
                artistId = artistId,
                onFinished = { navController.popBackStack(Destinations.ArtistPicker, false) },
                viewModel = hiltViewModel()
            )
        }
        composable(
            route = Destinations.Versus,
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: return@composable
            VersusMatchRoute(
                matchId = matchId,
                onFinished = { navController.popBackStack(Destinations.ArtistPicker, false) },
                viewModel = hiltViewModel()
            )
        }
        composable(Destinations.Leaderboard) {
            LeaderboardRoute(onBack = { navController.popBackStack() }, viewModel = hiltViewModel())
        }
        composable(Destinations.Settings) {
            SettingsRoute(onBack = { navController.popBackStack() }, viewModel = hiltViewModel())
        }
            }
        }
        AppleMusicAttribution(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp))
    }
}
// endregion

@Composable
fun AppleMusicAttribution(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = com.sharkoguess.core.designsystem.R.drawable.ic_apple_music),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.apple_attribution))
    }
}
