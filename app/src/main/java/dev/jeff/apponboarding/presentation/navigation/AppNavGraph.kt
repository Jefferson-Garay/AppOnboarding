package dev.jeff.apponboarding.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.jeff.apponboarding.presentation.calendario.CalendarioScreen
import dev.jeff.apponboarding.presentation.home.HomeScreen
import dev.jeff.apponboarding.presentation.recursos.RecursosScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        DrawerItem("Inicio", "home", Icons.Filled.Home),
        DrawerItem("Calendario", "calendario", Icons.Filled.CalendarMonth),
        DrawerItem("Recursos", "recursos", Icons.AutoMirrored.Filled.MenuBook)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = item.route == currentRoute,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) { 
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(items.find { it.route == currentRoute }?.title ?: "TCS") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                        }
                    }
                )
            },
            floatingActionButton = {
                 if (currentRoute == "home") {
                    ExtendedFloatingActionButton(
                        onClick = { /* Acción del chat */ },
                        icon = { Icon(Icons.Filled.QuestionAnswer, contentDescription = "Botón de chat") },
                        text = { Text(text = "¿Tienes preguntas?") }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController,
                startDestination = "home",
                Modifier.padding(innerPadding)
            ) {
                composable("home") { HomeScreen() }
                composable("calendario") { CalendarioScreen() }
                composable("recursos") { RecursosScreen() }
            }
        }
    }
}

data class DrawerItem(val title: String, val route: String, val icon: ImageVector)