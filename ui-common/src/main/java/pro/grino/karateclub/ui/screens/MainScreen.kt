package pro.grino.karateclub.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pro.grino.karateclub.core.navigation.NavRoutes
import pro.grino.karateclub.ui.R
import pro.grino.karateclub.ui.components.BottomNavItem
import pro.grino.karateclub.ui.components.BottomNavigationBar

/**
 * Главный экран приложения с нижним меню
 */
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    playersListContent: @Composable (NavController: NavHostController) -> Unit,
    playersAddContent: @Composable (NavController: NavHostController) -> Unit,
    groupsListContent: @Composable (NavController: NavHostController) -> Unit,
    groupsAddContent: @Composable (NavController: NavHostController) -> Unit
) {
    val navItems = listOf(
        BottomNavItem(
            route = NavRoutes.PLAYERS_LIST,
            icon = ImageVector.vectorResource(R.drawable.ic_player),
            label = stringResource(R.string.players)
        ),
        BottomNavItem(
            route = NavRoutes.GROUPS_LIST,
            icon = ImageVector.vectorResource(R.drawable.ic_group),
            label = stringResource(R.string.groups)
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavRoutes.PLAYERS_LIST

    // Определяем, нужно ли показывать нижнюю навигацию
    val showBottomBar = when {
        currentRoute == NavRoutes.PLAYERS_LIST -> true
        currentRoute == NavRoutes.GROUPS_LIST -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    items = navItems,
                    currentRoute = currentRoute,
                    onItemSelected = { route ->
                        navController.navigate(route) {
                            // Избегаем создания множества копий экранов при навигации
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Избегаем множества копий одного экрана в стеке
                            launchSingleTop = true
                            // Восстанавливаем состояние при возврате
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.PLAYERS_LIST,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(NavRoutes.PLAYERS_LIST) {
                playersListContent(navController)
            }
            composable(NavRoutes.PLAYER_ADD) {
                playersAddContent(navController)
            }
            composable(NavRoutes.GROUPS_LIST) {
                groupsListContent(navController)
            }
            composable(NavRoutes.GROUP_ADD) {
                groupsAddContent(navController)
            }
        }
    }
}