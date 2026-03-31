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

@Composable
fun WebViewScreen(
    url: String,
    webViewHolder: WebViewHolder,
    modifier: Modifier = Modifier,
    onLoginSuccess: (() -> Unit)? = null
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    val backgroundColor = MaterialTheme.colorScheme.background
    val contentColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary

    val context = LocalContext.current

    // ✅ Create WebView ONLY once
    val webView = remember {
        WebView(context).apply {

            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(this, true)

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = false
            settings.setSupportMultipleWindows(false)

            // ✅ Extra stability settings
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

            webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    isLoading = true
                    hasError = false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    isLoading = false

                    val cookies = cookieManager.getCookie("https://chiccity.in")

                    // ✅ Detect login success
                    if (cookies?.contains("wordpress_logged_in") == true) {
                        cookieManager.flush()
                        onLoginSuccess?.invoke()
                    }

                    // ✅ 🚫 Disable injection on login/account pages
                    val isLoginOrAccountPage =
                        url?.contains("app-login") == true ||
                                url?.contains("my-account") == true

                    if (!isLoginOrAccountPage) {
                        injectWhatsAppRemoval(view)
                    }
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val link = request?.url.toString()
                    return link.startsWith("whatsapp://") ||
                            link.contains("wa.me") ||
                            link.contains("api.whatsapp.com")
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    if (request?.isForMainFrame == true) {
                        isLoading = false
                        hasError = true
                    }
                }
            }
        }
    }

    // ✅ Attach WebView to holder once
    LaunchedEffect(Unit) {
        webViewHolder.webView = webView
    }

    // ✅ Decide FIRST page based on login cookie
    LaunchedEffect(url) {
        val cookieManager = CookieManager.getInstance()
        val cookies = cookieManager.getCookie("https://chiccity.in")

        val finalUrl = if (cookies?.contains("wordpress_logged_in") == true) {
            url   // ✅ Logged in → open main page
        } else {
            "https://chiccity.in/app-login/"  // ❗ Not logged in → login page
        }

        isLoading = true
        hasError = false
        webView.loadUrl(finalUrl)
    }

    Box(modifier = modifier.fillMaxSize()) {

        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (hasError) 0f else 1f)
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

    // ✅ Back navigation
    BackHandler(enabled = webView.canGoBack()) {
        webView.goBack()
    }
}

@Composable
private fun LoadingOverlay(
    backgroundColor: Color,
    primaryColor: Color,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = primaryColor)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Please wait...",
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor
            )
        }
    }
}

@Composable
private fun ErrorOverlay(
    backgroundColor: Color,
    contentColor: Color,
    primaryColor: Color,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.no_wifi_2_svgrepo_com),
                contentDescription = "No Internet",
                modifier = Modifier.size(150.dp),
                colorFilter = ColorFilter.tint(contentColor)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Page could not be loaded",
                style = MaterialTheme.typography.titleMedium,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Please check your internet connection.",
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text("Retry", color = backgroundColor)
            }
        }
    }
}

// ✅ JavaScript injection (only used on safe pages)
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
        """.trimIndent(),
        null
    )
}
