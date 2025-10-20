package com.example.shoppinglist.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController // <-- TAMBAHKAN IMPORT INI
import androidx.navigation.compose.rememberNavController // <-- TAMBAHKAN IMPORT INI
import com.example.shoppinglist.components.ItemInput
import com.example.shoppinglist.components.SearchInput
import com.example.shoppinglist.components.ShoppingList
import com.example.shoppinglist.components.Title
import com.example.shoppinglist.ui.theme.ShoppingListTheme


@Composable
fun HomeScreen(navController: NavController, snackbarHostState: SnackbarHostState) { // <-- TAMBAHKAN NavController DI SINI
    var newItemText by rememberSaveable { mutableStateOf("") }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    // Gunakan rememberSaveable agar daftar tidak hilang saat pindah halaman
    val shoppingItems = rememberSaveable { mutableStateListOf<String>() }

    val filteredItems by remember(searchQuery, shoppingItems) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                shoppingItems
            } else {
                shoppingItems.filter { it.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Title()
        ItemInput(
            text = newItemText,
            onTextChange = { newItemText = it },
            onAddItem = {
                if (newItemText.isNotBlank()) {
                    shoppingItems.add(newItemText)
                    newItemText = ""
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        SearchInput(
            query = searchQuery,
            onQueryChange = { searchQuery = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        // <-- KIRIMKAN navController KE SHOPPINGLIST -->
        ShoppingList(items = filteredItems, navController = navController)
    }
}


@Preview(showBackground = true)
@Composable
fun ShoppingListAppPreview() { // atau nama preview Anda
    ShoppingListTheme {
        HomeScreen(
            navController = rememberNavController(),
            snackbarHostState = SnackbarHostState() // <-- TAMBAHKAN BARIS INI
        )
    }
}