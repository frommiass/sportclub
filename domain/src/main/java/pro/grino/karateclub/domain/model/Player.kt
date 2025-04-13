package pro.grino.karateclub.domain.model

data class Player(
    val id: String,
    val name: String,
    val age: Int,
    val belt: String,
    val group: String,
    val phone: String = "",
    val email: String = "",
    val joinDate: String = ""
)