//
//
//
//package chiccity.`in`
//
//import android.graphics.Bitmap
//import android.webkit.*
//import androidx.activity.compose.BackHandler
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.graphics.ColorFilter
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//
//@Composable
//fun WebViewScreen(
//    url: String,
//    webViewHolder: WebViewHolder,
//    modifier: Modifier = Modifier,
//    onLoginSuccess: (() -> Unit)? = null
//) {
//    var isLoading by remember { mutableStateOf(true) }
//    var hasError by remember { mutableStateOf(false) }
//
//    val backgroundColor = MaterialTheme.colorScheme.background
//    val contentColor = MaterialTheme.colorScheme.onBackground
//    val primaryColor = MaterialTheme.colorScheme.primary
//
//    // Reset state when URL changes
//    LaunchedEffect(url) {
//        isLoading = true
//        hasError = false
//        webViewHolder.webView?.loadUrl(url)
//    }
//
//    Box(modifier = modifier.fillMaxSize()) {
//
//        AndroidView(
//            factory = { context ->
//                val cookieManager = CookieManager.getInstance()
//                cookieManager.setAcceptCookie(true)
//
//                WebView(context).apply {
//                    webViewHolder.webView = this
//
//                    settings.javaScriptEnabled = true
//                    settings.domStorageEnabled = true
//                    settings.javaScriptCanOpenWindowsAutomatically = false
//                    settings.setSupportMultipleWindows(false)
//
//                    cookieManager.setAcceptThirdPartyCookies(this, true)
//
//                    webViewClient = object : WebViewClient() {
//
//                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                            isLoading = true
//                            hasError = false
//                        }
//
//                        override fun onPageFinished(view: WebView?, url: String?) {
//                            isLoading = false
//
//                            val cookies = cookieManager.getCookie("https://chiccity.in")
//                            if (cookies?.contains("wordpress_logged_in") == true) {
//                                cookieManager.flush()
//                                // Notify login success
//                                onLoginSuccess?.invoke()
//                            }
//
//                            // Inject JS for hiding WhatsApp popups and custom wishlist
//                            injectWhatsAppRemoval(view)
////                            injectWishlistButton(view)
//                        }
//
//                        override fun shouldOverrideUrlLoading(
//                            view: WebView?,
//                            request: WebResourceRequest?
//                        ): Boolean {
//                            val link = request?.url.toString()
//                            return link.startsWith("whatsapp://") ||
//                                    link.contains("wa.me") ||
//                                    link.contains("api.whatsapp.com")
//                        }
//
//                        override fun onReceivedError(
//                            view: WebView?,
//                            request: WebResourceRequest?,
//                            error: WebResourceError?
//                        ) {
//                            if (request?.isForMainFrame == true) {
//                                isLoading = false
//                                hasError = true
//                            }
//                        }
//                    }
//
//                    loadUrl(url)
//                }
//            },
//            modifier = Modifier
//                .fillMaxSize()
//                .alpha(if (hasError) 0f else 1f)
//        )
//
//        if (isLoading && !hasError) {
//            LoadingOverlay(backgroundColor, primaryColor, contentColor)
//        }
//
//        if (hasError) {
//            ErrorOverlay(
//                backgroundColor = backgroundColor,
//                contentColor = contentColor,
//                primaryColor = primaryColor,
//                onRetry = {
//                    hasError = false
//                    isLoading = true
//                    webViewHolder.webView?.reload()
//                }
//            )
//        }
//    }
//
//    // Back handler for WebView navigation
//    BackHandler(enabled = webViewHolder.webView?.canGoBack() == true) {
//        webViewHolder.webView?.goBack()
//    }
//}
//
//@Composable
//private fun LoadingOverlay(
//    backgroundColor: androidx.compose.ui.graphics.Color,
//    primaryColor: androidx.compose.ui.graphics.Color,
//    contentColor: androidx.compose.ui.graphics.Color
//) {
//    Box(
//        modifier = Modifier.fillMaxSize().background(backgroundColor),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            CircularProgressIndicator(color = primaryColor)
//            Spacer(modifier = Modifier.height(20.dp))
//            Text("Please wait...", style = MaterialTheme.typography.bodyLarge, color = contentColor)
//        }
//    }
//}
//
//@Composable
//private fun ErrorOverlay(
//    backgroundColor: androidx.compose.ui.graphics.Color,
//    contentColor: androidx.compose.ui.graphics.Color,
//    primaryColor: androidx.compose.ui.graphics.Color,
//    onRetry: () -> Unit
//) {
//    Box(
//        modifier = Modifier.fillMaxSize().background(backgroundColor),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Image(
//                painter = painterResource(id = R.drawable.no_wifi_2_svgrepo_com),
//                contentDescription = "No Internet",
//                modifier = Modifier.size(150.dp),
//                colorFilter = ColorFilter.tint(contentColor)
//            )
//            Spacer(modifier = Modifier.height(20.dp))
//            Text("Page could not be loaded", style = MaterialTheme.typography.titleMedium, color = contentColor)
//            Spacer(modifier = Modifier.height(8.dp))
//            Text("Please check your internet connection.", style = MaterialTheme.typography.bodyMedium, color = contentColor)
//            Spacer(modifier = Modifier.height(20.dp))
//            Button(
//                onClick = onRetry,
//                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
//            ) {
//                Text("Retry", color = backgroundColor)
//            }
//        }
//    }
//}
//
//// JavaScript injection helpers
//private fun injectWhatsAppRemoval(view: WebView?) {
//    view?.evaluateJavascript("""
//        (function() {
//            const selectors = ['.wa__btn_popup_txt','.wa__btn_popup_icon','.wa__popup_heading','.wa__popup_content'];
//            selectors.forEach(sel => { const el = document.querySelector(sel); if(el) el.remove(); });
//        })();
//    """.trimIndent(), null)
//}
//
//private fun injectWishlistButton(view: WebView?) {
//    view?.evaluateJavascript("""
//        (function() {
//            const cartContainers = document.querySelectorAll('.ast-header-woo-cart');
//            cartContainers.forEach(cart => {
//                cart.innerHTML = '';
//                const a = document.createElement('a');
//                a.href = "https://youtube.com/";
//                a.className = "wishlist-container";
//                a.setAttribute("aria-label", "View Wishlist");
//                const div = document.createElement('div');
//                div.className = "wishlist-icon-wrap";
//                const i = document.createElement('i');
//                i.className = "astra-icon ast-icon-heart";
//                const span = document.createElement('span');
//                span.className = "ast-icon icon-heart";
//                span.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg>';
//                i.appendChild(span);
//                div.appendChild(i);
//                a.appendChild(div);
//                a.style.display = "flex";
//                a.style.alignItems = "center";
//                a.style.marginTop = "5px";
//                cart.appendChild(a);
//                a.addEventListener('click', function(e) {
//                    e.stopPropagation();
//                    e.preventDefault();
//                    window.location.href = a.href;
//                });
//            });
//        })();
//    """.trimIndent(), null)
//}

package chiccity.`in`.appWebView

import android.graphics.Bitmap
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import chiccity.`in`.R

// @Composable
// fun WebViewScreen(
//     url: String,
//     webViewHolder: WebViewHolder,
//     modifier: Modifier = Modifier,
//     onLoginSuccess: (() -> Unit)? = null
// ) {
//     var isLoading by remember { mutableStateOf(true) }
//     var hasError by remember { mutableStateOf(false) }

//     val backgroundColor = MaterialTheme.colorScheme.background
//     val contentColor = MaterialTheme.colorScheme.onBackground
//     val primaryColor = MaterialTheme.colorScheme.primary

//     val context = LocalContext.current

//     // ✅ Create WebView ONLY once
//     val webView = remember {
//         WebView(context).apply {

//             val cookieManager = CookieManager.getInstance()
//             cookieManager.setAcceptCookie(true)
//             cookieManager.setAcceptThirdPartyCookies(this, true)

//             settings.javaScriptEnabled = true
//             settings.domStorageEnabled = true
//             settings.javaScriptCanOpenWindowsAutomatically = false
//             settings.setSupportMultipleWindows(false)

//             webViewClient = object : WebViewClient() {

//                 override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                     isLoading = true
//                     hasError = false
//                 }

//                 override fun onPageFinished(view: WebView?, url: String?) {
//                     isLoading = false

//                     val cookies = cookieManager.getCookie("https://chiccity.in")
//                     if (cookies?.contains("wordpress_logged_in") == true) {
//                         cookieManager.flush()
//                         onLoginSuccess?.invoke()
//                     }

//                     injectWhatsAppRemoval(view)
//                 }

//                 override fun shouldOverrideUrlLoading(
//                     view: WebView?,
//                     request: WebResourceRequest?
//                 ): Boolean {
//                     val link = request?.url.toString()
//                     return link.startsWith("whatsapp://") ||
//                             link.contains("wa.me") ||
//                             link.contains("api.whatsapp.com")
//                 }

//                 override fun onReceivedError(
//                     view: WebView?,
//                     request: WebResourceRequest?,
//                     error: WebResourceError?
//                 ) {
//                     if (request?.isForMainFrame == true) {
//                         isLoading = false
//                         hasError = true
//                     }
//                 }
//             }
//         }
//     }

//     // ✅ Attach to holder once
//     LaunchedEffect(Unit) {
//         webViewHolder.webView = webView
//     }

//     // ✅ Single source of URL loading (FIXED)
//     LaunchedEffect(url) {
//         isLoading = true
//         hasError = false
//         webView.loadUrl(url)
//     }

//     Box(modifier = modifier.fillMaxSize()) {

//         AndroidView(
//             factory = { webView },
//             modifier = Modifier
//                 .fillMaxSize()
//                 .alpha(if (hasError) 0f else 1f)
//         )

//         if (isLoading && !hasError) {
//             LoadingOverlay(backgroundColor, primaryColor, contentColor)
//         }

//         if (hasError) {
//             ErrorOverlay(
//                 backgroundColor = backgroundColor,
//                 contentColor = contentColor,
//                 primaryColor = primaryColor,
//                 onRetry = {
//                     hasError = false
//                     isLoading = true
//                     webView.reload()
//                 }
//             )
//         }
//     }

//     // ✅ Back navigation
//     BackHandler(enabled = webView.canGoBack()) {
//         webView.goBack()
//     }
// }

// @Composable
// private fun LoadingOverlay(
//     backgroundColor: Color,
//     primaryColor: Color,
//     contentColor: Color
// ) {
//     Box(
//         modifier = Modifier
//             .fillMaxSize()
//             .background(backgroundColor),
//         contentAlignment = Alignment.Center
//     ) {
//         Column(horizontalAlignment = Alignment.CenterHorizontally) {
//             CircularProgressIndicator(color = primaryColor)
//             Spacer(modifier = Modifier.height(20.dp))
//             Text(
//                 "Please wait...",
//                 style = MaterialTheme.typography.bodyLarge,
//                 color = contentColor
//             )
//         }
//     }
// }

// @Composable
// private fun ErrorOverlay(
//     backgroundColor: Color,
//     contentColor: Color,
//     primaryColor: Color,
//     onRetry: () -> Unit
// ) {
//     Box(
//         modifier = Modifier
//             .fillMaxSize()
//             .background(backgroundColor),
//         contentAlignment = Alignment.Center
//     ) {
//         Column(horizontalAlignment = Alignment.CenterHorizontally) {
//             Image(
//                 painter = painterResource(id = R.drawable.no_wifi_2_svgrepo_com),
//                 contentDescription = "No Internet",
//                 modifier = Modifier.size(150.dp),
//                 colorFilter = ColorFilter.tint(contentColor)
//             )
//             Spacer(modifier = Modifier.height(20.dp))
//             Text(
//                 "Page could not be loaded",
//                 style = MaterialTheme.typography.titleMedium,
//                 color = contentColor
//             )
//             Spacer(modifier = Modifier.height(8.dp))
//             Text(
//                 "Please check your internet connection.",
//                 style = MaterialTheme.typography.bodyMedium,
//                 color = contentColor
//             )
//             Spacer(modifier = Modifier.height(20.dp))
//             Button(
//                 onClick = onRetry,
//                 colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
//             ) {
//                 Text("Retry", color = backgroundColor)
//             }
//         }
//     }
// }

// // ✅ JavaScript injection
// private fun injectWhatsAppRemoval(view: WebView?) {
//     view?.evaluateJavascript(
//         """
//         (function() {
//             const selectors = ['.wa__btn_popup_txt','.wa__btn_popup_icon','.wa__popup_heading','.wa__popup_content'];
//             selectors.forEach(sel => { 
//                 const el = document.querySelector(sel); 
//                 if(el) el.remove(); 
//             });
//         })();
//         """.trimIndent(),
//         null
//     )
// }


@Composable
fun WebViewScreen(
    url: String,
    webViewHolder: WebViewHolder,
    modifier: Modifier = Modifier,
    hideUI: Boolean = false, // New parameter to control header/footer visibility
    onLoginSuccess: (() -> Unit)? = null
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    val backgroundColor = MaterialTheme.colorScheme.background
    val contentColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(this, true)

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = false
            
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    isLoading = true
                    hasError = false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    isLoading = false

                    // Check for login success
                    val cookies = cookieManager.getCookie("https://chiccity.in")
                    if (cookies?.contains("wordpress_logged_in") == true) {
                        cookieManager.flush()
                        onLoginSuccess?.invoke()
                    }

                    // Always remove WhatsApp
                    injectWhatsAppRemoval(view)
                    
                    // Conditionally remove Header and Footer
                    if (hideUI) {
                        injectUIElementsRemoval(view)
                    }
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val link = request?.url.toString()
                    return link.startsWith("whatsapp://") || link.contains("wa.me") || link.contains("api.whatsapp.com")
                }

                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    if (request?.isForMainFrame == true) {
                        isLoading = false
                        hasError = true
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) { webViewHolder.webView = webView }

    LaunchedEffect(url) {
        isLoading = true
        hasError = false
        webView.loadUrl(url)
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { webView },
            modifier = Modifier.fillMaxSize().alpha(if (hasError) 0f else 1f)
        )

        if (isLoading && !hasError) {
            LoadingOverlay(backgroundColor, primaryColor, contentColor)
        }

        if (hasError) {
            ErrorOverlay(
                backgroundColor = backgroundColor,
                contentColor = contentColor,
                primaryColor = primaryColor,
                onRetry = {
                    hasError = false
                    isLoading = true
                    webView.reload()
                }
            )
        }
    }

    BackHandler(enabled = webView.canGoBack()) {
        webView.goBack()
    }
}

// --- Helper Functions ---

private fun injectWhatsAppRemoval(view: WebView?) {
    view?.evaluateJavascript(
        """
        (function() {
            const selectors = ['.wa__btn_popup_txt','.wa__btn_popup_icon','.wa__popup_heading','.wa__popup_content'];
            selectors.forEach(sel => { 
                const el = document.querySelector(sel); 
                if(el) el.remove(); 
            });
        })();
        """.trimIndent(), null
    )
}

/**
 * Removes Header and Footer from the website.
 * Adjust the selectors ('.site-header', '.site-footer') based on your specific WordPress theme.
 */
private fun injectUIElementsRemoval(view: WebView?) {
    view?.evaluateJavascript(
        """
        (function() {
            // Common WordPress/Astra selectors. Change these if your header/footer still show.
            const uiSelectors = [
                'header', 
                'footer', 
                '.site-header', 
                '.site-footer', 
                '.ast-mobile-header-wrap', 
                '.main-header-bar'
            ];
            uiSelectors.forEach(sel => { 
                const el = document.querySelector(sel); 
                if(el) el.style.display = 'none'; 
            });
        })();
        """.trimIndent(), null
    )
}

@Composable
private fun LoadingOverlay(backgroundColor: Color, primaryColor: Color, contentColor: Color) {
    Box(modifier = Modifier.fillMaxSize().background(backgroundColor), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = primaryColor)
            Spacer(modifier = Modifier.height(20.dp))
            Text("Please wait...", style = MaterialTheme.typography.bodyLarge, color = contentColor)
        }
    }
}

@Composable
private fun ErrorOverlay(backgroundColor: Color, contentColor: Color, primaryColor: Color, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(backgroundColor), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.no_wifi_2_svgrepo_com),
                contentDescription = "No Internet",
                modifier = Modifier.size(150.dp),
                colorFilter = ColorFilter.tint(contentColor)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text("Page could not be loaded", style = MaterialTheme.typography.titleMedium, color = contentColor)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = primaryColor)) {
                Text("Retry", color = backgroundColor)
            }
        }
    }
}
