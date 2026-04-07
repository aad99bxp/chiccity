package chiccity.`in`.appWebView

import android.graphics.Bitmap
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var progress by remember { mutableIntStateOf(0) }
    var hasError by remember { mutableStateOf(false) }

    val backgroundColor = MaterialTheme.colorScheme.background
    val contentColor = MaterialTheme.colorScheme.onBackground
    val primaryColor = MaterialTheme.colorScheme.primary

    // Use the preloaded singleton WebView from WebViewManager
    val webView = remember {
        WebViewManager.getOrCreateWebView(context).apply {
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    progress = newProgress
                    if (newProgress == 100) {
                        isLoading = false
                    }
                }
            }

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    isLoading = true
                    hasError = false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    isLoading = false
                    
                    val cookieManager = CookieManager.getInstance()
                    val cookies = cookieManager.getCookie("https://chiccity.in")
                    
                    if (cookies?.contains("wordpress_logged_in") == true) {
                        onLoginSuccess?.invoke()
                    }

                    // Only keep WhatsApp removal
                    injectWhatsAppRemoval(view)
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val link = request?.url.toString()
                    return if (link.startsWith("whatsapp://") || link.contains("wa.me") || link.contains("api.whatsapp.com")) {
                        true 
                    } else {
                        false
                    }
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

    // Sync the webViewHolder for back navigation etc.
    LaunchedEffect(webView) {
        webViewHolder.webView = webView
    }

    // Load the URL only if it's different from current to avoid redundant reloads
    LaunchedEffect(url) {
        if (webView.url != url && !url.isNullOrEmpty()) {
            webView.loadUrl(url)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (hasError) 0f else 1f)
        )

        // Progress bar at the top
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.fillMaxWidth().height(3.dp),
                color = primaryColor,
                trackColor = Color.Transparent
            )
        }

        if (isLoading && progress < 10 && !hasError) {
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

    // Handle Back Navigation using WebView history
    BackHandler(enabled = webView.canGoBack()) {
        webView.goBack()
    }
}

private fun injectWhatsAppRemoval(view: WebView?) {
    view?.evaluateJavascript(
        "(function() { " +
        "const selectors = ['.wa__btn_popup_txt','.wa__btn_popup_icon','.wa__popup_heading','.wa__popup_content'];" +
        "selectors.forEach(sel => { const el = document.querySelector(sel); if(el) el.remove(); });" +
        "})();", null
    )
}

@Composable
private fun LoadingOverlay(backgroundColor: Color, primaryColor: Color, contentColor: Color) {
    Box(modifier = Modifier.fillMaxSize().background(backgroundColor), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = primaryColor)
            Spacer(modifier = Modifier.height(20.dp))
            Text("Optimizing your experience...", style = MaterialTheme.typography.bodyLarge, color = contentColor)
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
                modifier = Modifier.size(120.dp),
                colorFilter = ColorFilter.tint(contentColor)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Connection issue", style = MaterialTheme.typography.titleMedium, color = contentColor)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = primaryColor)) {
                Text("Try Again", color = Color.White)
            }
        }
    }
}
