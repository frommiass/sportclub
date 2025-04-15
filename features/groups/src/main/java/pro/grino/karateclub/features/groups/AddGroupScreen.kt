package pro.grino.karateclub.features.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import pro.grino.karateclub.domain.model.Group
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupScreen(
    navController: NavController,
    viewModel: AddGroupViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var coach by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }
    var schedule by remember { mutableStateOf("") }
    var maxCapacity by remember { mutableStateOf("0") }

    // Наблюдение за состоянием для перехода назад после успешного сохранения
    LaunchedEffect(state) {
        if (state is AddGroupState.Success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавление группы") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Создаем новую группу и сохраняем
                    val newGroup = Group(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        coach = coach,
                        level = level,
                        schedule = schedule,
                        maxCapacity = maxCapacity.toIntOrNull() ?: 0,
                        currentMembersCount = 0
                    )
                    coroutineScope.launch {
                        viewModel.addGroup(newGroup)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Сохранить",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Отображение ошибки, если есть
            if (state is AddGroupState.Error) {
                val errorMessage = (state as AddGroupState.Error).message
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Поля ввода данных
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название группы") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = coach,
                onValueChange = { coach = it },
                label = { Text("Тренер") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Выпадающий список для уровня
            val levels = listOf("Начальный", "Средний", "Продвинутый", "Мастера")
            ExposedDropdownMenuBox(
                expanded = false, // Не раскрывать при создании
                onExpandedChange = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = level,
                    onValueChange = { level = it },
                    label = { Text("Уровень") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    singleLine = true
                )

                // В реальном приложении здесь будет выпадающий список
                // Упрощаем для примера - пользователь вводит текст
            }

            OutlinedTextField(
                value = schedule,
                onValueChange = { schedule = it },
                label = { Text("Расписание") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = maxCapacity,
                onValueChange = { maxCapacity = it },
                label = { Text("Максимальное количество участников") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Индикатор загрузки, если данные сохраняются
            if (state is AddGroupState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}