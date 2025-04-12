package pro.grino.karateclub.core.navigation

// Определение навигационных маршрутов в core-модуле для доступа из всех модулей
object NavRoutes {
    // Участники
    const val PLAYERS_LIST = "players_list"
    const val PLAYERS_DETAILS = "participant_details/{playerId}"
    const val PLAYERS_EDIT = "player_edit?playerId={playerId}"

    // Группы
    const val GROUPS_LIST = "groups_list"
    const val GROUP_DETAILS = "group_details/{groupId}"
}