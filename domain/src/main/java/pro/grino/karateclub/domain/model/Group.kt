package pro.grino.karateclub.domain.model

data class Group(
    val id: String,
    val name: String,
    val coach: String,
    val level: String,
    val schedule: String,
    val maxCapacity: Int = 0,
    val currentMembersCount: Int = 0
)