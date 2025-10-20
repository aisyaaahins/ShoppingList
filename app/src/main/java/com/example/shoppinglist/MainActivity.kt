package com.example.shoppinglist

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shoppinglist.navigation.AppNavGraph
import com.example.shoppinglist.navigation.Screen
import com.example.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    // --- KONTROLER NAVIGASI ---
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // <-- TAMBAH DI SINI: State untuk Snackbar -->
    val snackbarHostState = remember { SnackbarHostState() }

    // --- DEFINISI MENU ---
    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Profile
    )
    val drawerNavItems = listOf(
        Screen.Settings
    )

    // --- STATE UNTUK UI ---
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = navBackStackEntry?.destination?.route

    // <-- TAMBAH DI SINI: Logika untuk panah kembali -->
    // 1. Buat daftar halaman utama
    val topLevelDestinations = listOf(
        Screen.Home.route,
        Screen.Profile.route,
        Screen.Settings.route
    )
    // 2. Cek apakah kita sedang di halaman utama
    val isTopLevelDestination = currentRoute in topLevelDestinations
    // <-- SELESAI TAMBAHAN -->


    // --- STRUKTUR UI UTAMA ---
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // ... (Kode DrawerContent Anda tidak berubah)
            ModalDrawerSheet {
                drawerNavItems.forEach { screen ->
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text(stringResource(R.string.title_settings)) },
                        selected = screen.route == currentRoute,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            // <-- UBAH DI SINI: Tambahkan snackbarHost -->
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

            topBar = {
                TopAppBar(
                    title = {
                        // ... (Kode title Anda tidak berubah)
                        val title = when (currentRoute) {
                            Screen.Home.route -> stringResource(R.string.title_home)
                            Screen.Profile.route -> stringResource(R.string.title_profile)
                            Screen.Settings.route -> stringResource(R.string.title_settings)
                            // <-- TAMBAH DI SINI: Judul untuk halaman detail -->
                            Screen.ItemDetail.route -> "Detail Item" // Judul halaman detail
                            else -> stringResource(R.string.app_name)
                        }
                        Text(title)
                    },

                    // <-- UBAH DI SINI: Ganti ikon "hamburger" dengan "panah kembali" -->
                    navigationIcon = {
                        if (isTopLevelDestination) {
                            // Tampilkan "hamburger" jika di halaman utama
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "Buka Drawer")
                            }
                        } else {
                            // Tampilkan "panah kembali" jika di halaman detail
                            IconButton(onClick = {
                                navController.popBackStack() // Aksi untuk kembali
                            }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                            }
                        }
                    }
                    // <-- SELESAI PERUBAHAN navigationIcon -->
                )
            },
            bottomBar = {
                // Tampilkan BottomBar HANYA jika kita di halaman utama
                if (isTopLevelDestination) {
                    NavigationBar {
                        bottomNavItems.forEach { screen ->
                            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            val icon = if (screen == Screen.Home) Icons.Default.Home else Icons.Default.Person
                            val titleResId = if (screen == Screen.Home) R.string.title_home else R.string.title_profile
                            val title = stringResource(id = titleResId)

                            NavigationBarItem(
                                icon = { Icon(icon, contentDescription = title) },
                                label = { Text(title) },
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            AppNavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                // <-- UBAH DI SINI: Kirim snackbarHostState -->
                snackbarHostState = snackbarHostState
            )
        }
    }
}