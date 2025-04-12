package pro.grino.karateclub.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import pro.grino.karateclub.ui.R

/**
 * Элемент меню нижней навигации
 */
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

/**
 * Компонент нижней навигации
 */
@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    currentRoute: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route || currentRoute.startsWith(item.route)

            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(text = item.label) },
                selected = selected,
                onClick = { onItemSelected(item.route) }
            )
        }
    }
}