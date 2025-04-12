package pro.grino.karateclub.features.groups

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Данные для демонстрации
data class Group(
    val id: Int,
    val name: String,
    val coach: String,
    val level: String,
    val membersCount: Int,
    val schedule: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsListScreen(navController: NavController? = null) {
    // Демо-данные групп
    val groups = remember {
        listOf(
            Group(1, "Взрослые", "Иванов И.И.", "Продвинутый", 12, "Пн, Ср, Пт 19:00-21:00"),
            Group(2, "Юниоры", "Петров П.П.", "Средний", 18, "Вт, Чт 17:00-19:00, Сб 10:00-12:00"),
            Group(3, "Дети 8-12", "Сидорова О.В.", "Начальный", 15, "Пн, Ср, Пт 16:00-17:30"),
            Group(4, "Старшие мастера", "Кузнецов А.А.", "Мастера", 8, "Сб, Вс 12:00-14:00")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Группы") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Добавление новой группы */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить группу",
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
            // Список групп
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(groups) { group ->
                    GroupItem(group = group, onGroupClick = {
                        // Здесь будет навигация к деталям группы
                    })
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        }
    }
}

@Composable
fun GroupItem(
    group: Group,
    onGroupClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onGroupClick),
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
            // Иконка группы
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(getGroupLevelColor(group.level)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Информация о группе
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Тренер: ${group.coach}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Уровень: ${group.level}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Участники: ${group.membersCount}",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Карточка с расписанием
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Расписание: ${group.schedule}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

// Функция для определения цвета в зависимости от уровня группы
@Composable
fun getGroupLevelColor(level: String): Color {
    return when (level.lowercase()) {
        "начальный" -> Color(0xFF4CAF50) // Зеленый
        "средний" -> Color(0xFF2196F3)   // Синий
        "продвинутый" -> Color(0xFFFF9800) // Оранжевый
        "мастера" -> Color(0xFF9C27B0)   // Фиолетовый
        else -> MaterialTheme.colorScheme.primary
    }
}