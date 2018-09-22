
object AccountRepository {

    private val accounts = listOf(
            Account(id = "1", name = "Tom"),
            Account(id = "2", name = "Jessica")
    )

    fun findById(id: String): Account? = accounts.find { it.id == id }

}