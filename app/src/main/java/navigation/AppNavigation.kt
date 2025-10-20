package com.example.shoppinglist.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
// Hapus komentar error jika sudah tidak diperlukan
import com.example.shoppinglist.ui.screen.HomeScreen
import com.example.shoppinglist.ui.screen.ItemDetailScreen
import com.example.shoppinglist.ui.screen.ProfileScreen
import com.example.shoppinglist.ui.screen.SettingsScreen

// 1. Definisikan "alamat" unik untuk setiap layar
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object ItemDetail : Screen("itemDetail/{itemName}") {
        fun createRoute(itemName: String) = "itemDetail/$itemName"
    }
}

// 2. Buat "NavHost" (grafik navigasi)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier, // <-- KESALAHAN 1: TAMBAHKAN KOMA DI SINI
    snackbarHostState: SnackbarHostState
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route, // Mulai dari Home
        modifier = modifier
    ) {
        // Rute untuk Halaman Home
        composable(
            route = Screen.Home.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            // <-- KESALAHAN 2: TERUSKAN 'snackbarHostState' DI SINI
            HomeScreen(
                navController = navController,
                snackbarHostState = snackbarHostState
            )
        }

        // Rute untuk Halaman Profile
        composable(
            route = Screen.Profile.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            ProfileScreen()
        }

        // Rute untuk Halaman Settings
        composable(
            route = Screen.Settings.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            SettingsScreen()
        }

        // Rute untuk Halaman Detail
        composable(
            route = Screen.ItemDetail.route,
            arguments = listOf(navArgument("itemName") { type = NavType.StringType }),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) { backStackEntry ->
            val itemName = backStackEntry.arguments?.getString("itemName")
            ItemDetailScreen(itemName = itemName)
        }
    }
}