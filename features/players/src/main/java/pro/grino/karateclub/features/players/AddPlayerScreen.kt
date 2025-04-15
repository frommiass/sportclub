package pro.grino.karateclub.features.players

import android.util.Log
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
import org.koin.androidx.compose.get
import pro.grino.karateclub.domain.model.Player
import pro.grino.karateclub.domain.usecase.GetAllPlayersUseCase
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlayerScreen(
    navController: NavController,
    viewModel: AddPlayerViewModel = koinViewModel(),
    playersViewModel: PlayersViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var belt by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Наблюдение за состоянием для перехода назад после успешного сохранения
    LaunchedEffect(state) {
        if (state is AddPlayerState.Success) {
            Log.d("AddPlayerScreen", "Участник успешно добавлен, обновляем список")
            // Важно: обновляем список участников перед возвратом
            playersViewModel.loadPlayers()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавление участника") },
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
                    // Текущая дата в формате YYYY-MM-DD
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val currentDate = dateFormat.format(Date())

                    Log.d("AddPlayerScreen", "Создаем нового участника: $name")

                    // Создаем нового участника и сохраняем
                    val newPlayer = Player(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        age = age.toIntOrNull() ?: 0,
                        belt = belt,
                        group = group,
                        phone = phone,
                        email = email,
                        joinDate = currentDate
                    )

                    coroutineScope.launch {
                        Log.d("AddPlayerScreen", "Вызываем метод addPlayer для ${newPlayer.name}")
                        viewModel.addPlayer(newPlayer)
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
            if (state is AddPlayerState.Error) {
                val errorMessage = (state as AddPlayerState.Error).message
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
                label = { Text("ФИО") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Возраст") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Выпадающий список для пояса
            val belts = listOf("Белый", "Желтый", "Оранжевый", "Зеленый", "Синий", "Коричневый", "Черный")
            ExposedDropdownMenuBox(
                expanded = false, // Не раскрывать при создании
                onExpandedChange = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = belt,
                    onValueChange = { belt = it },
                    label = { Text("Пояс") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    singleLine = true
                )

                // В реальном приложении здесь будет выпадающий список
                // Упрощаем для примера - пользователь вводит текст
            }

            OutlinedTextField(
                value = group,
                onValueChange = { group = it },
                label = { Text("Группа") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Телефон") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            // Индикатор загрузки, если данные сохраняются
            if (state is AddPlayerState.Loading) {
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