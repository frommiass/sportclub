package pro.grino.karateclub.features.players

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Данные для демонстрации
data class Player(
    val id: Int,
    val name: String,
    val age: Int,
    val belt: String,
    val group: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersListScreen(navController: NavController? = null) {
    // Демо-данные игроков
    val players = remember {
        listOf(
            Player(1, "Иван Иванов", 15, "Зеленый", "Юниоры"),
            Player(2, "Мария Петрова", 14, "Синий", "Юниоры"),
            Player(3, "Алексей Сидоров", 22, "Коричневый", "Взрослые"),
            Player(4, "Елена Кузнецова", 20, "Черный", "Взрослые"),
            Player(5, "Дмитрий Новиков", 12, "Желтый", "Дети"),
            Player(6, "Ольга Смирнова", 16, "Зеленый", "Юниоры")
        )
    }

    // Состояние поиска
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Участники") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Добавление нового игрока */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить участника",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Заголовок страницы
            Text(
                text = "Список участников",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )

            // Список игроков
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(players) { player ->
                    PlayerItem(player = player, onPlayerClick = {
                        // Здесь будет навигация к деталям игрока
                    })
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        }
    }
}

@Composable
fun PlayerItem(
    player: Player,
    onPlayerClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onPlayerClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватар участника
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(getBeltColor(player.belt)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Информация об участнике
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Возраст: ${player.age} лет",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Пояс: ${player.belt}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Группа: ${player.group}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// Функция для определения цвета пояса
@Composable
fun getBeltColor(belt: String): Color {
    return when (belt.lowercase()) {
        "белый" -> Color.White
        "желтый" -> Color(0xFFFFC107)
        "оранжевый" -> Color(0xFFFF9800)
        "зеленый" -> Color(0xFF4CAF50)
        "синий" -> Color(0xFF2196F3)
        "коричневый" -> Color(0xFF795548)
        "черный" -> Color(0xFF000000)
        else -> MaterialTheme.colorScheme.primary
    }
}