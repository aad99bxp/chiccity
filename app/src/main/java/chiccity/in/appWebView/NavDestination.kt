package chiccity.`in`.appWebView
import kotlinx.serialization.Serializable

@Serializable
sealed class NavDestination {
    @Serializable data object Home : NavDestination()
    @Serializable data object Cart : NavDestination()
    @Serializable data object Orders : NavDestination()
    @Serializable data object Account : NavDestination()
}

object NavUrls {
    const val HOME = "https://chiccity.in/"
    const val CART = "https://chiccity.in/cart-2/"
    const val ORDERS = "https://chiccity.in/my-account-2/orders/"
    const val ACCOUNT = "https://chiccity.in/my-account/"

    fun urlForDestination(dest: NavDestination): String = when (dest) {
        is NavDestination.Home -> HOME
        is NavDestination.Cart -> CART
        is NavDestination.Orders -> ORDERS
        is NavDestination.Account -> ACCOUNT
    }
}