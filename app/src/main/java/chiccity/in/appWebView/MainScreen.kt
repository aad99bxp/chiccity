package chiccity.`in`.appWebView

import android.webkit.CookieManager
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController



@Composable
fun MainScreen() {

    val navController = rememberNavController()
    val webViewHolder = remember { WebViewHolder() }

    // ✅ Check login via cookies
    val cookieManager = CookieManager.getInstance()
    val hasLoginCookie =
        cookieManager.getCookie("https://chiccity.in")?.contains("wordpress_logged_in") == true

    var isLoggedIn by remember { mutableStateOf(hasLoginCookie) }

    Scaffold(
        bottomBar = {
            if (isLoggedIn) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val currentDestination = when {
                    currentRoute?.contains("Cart") == true -> NavDestination.Cart
                    currentRoute?.contains("Orders") == true -> NavDestination.Orders
                    currentRoute?.contains("Account") == true -> NavDestination.Account
                    else -> NavDestination.Home
                }

                ChicCityBottomBar(
                    currentDestination = currentDestination,
                    onDestinationSelected = { destination ->
                        navController.navigate(destination) {
                            popUpTo(NavDestination.Home) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->

        // ✅ PURE TYPE-SAFE NAVIGATION (FIXED)
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) NavDestination.Home else NavDestination.Auth,
            modifier = Modifier.padding(innerPadding)
        ) {

            // ✅ AUTH SCREEN
            composable<NavDestination.Auth> {
                WebViewScreen(
                    url = "https://chiccity.in/my-account/",
                    webViewHolder = webViewHolder,
                    onLoginSuccess = {
                        isLoggedIn = true
                        navController.navigate(NavDestination.Home) {
                            popUpTo(NavDestination.Auth) { inclusive = true }
                        }
                    }
                )
            }

            // ✅ HOME
            composable<NavDestination.Home> {
                WebViewScreen(
                    url = NavUrls.HOME,
                    webViewHolder = webViewHolder
                )
            }

            // ✅ CART
            composable<NavDestination.Cart> {
                WebViewScreen(
                    url = NavUrls.CART,
                    webViewHolder = webViewHolder
                )
            }

            // ✅ ORDERS
            composable<NavDestination.Orders> {
                WebViewScreen(
                    url = NavUrls.ORDERS,
                    webViewHolder = webViewHolder
                )
            }

            // ✅ ACCOUNT
            composable<NavDestination.Account> {
                WebViewScreen(
                    url = NavUrls.ACCOUNT,
                    webViewHolder = webViewHolder
                )
            }
        }
    }
}
