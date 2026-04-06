package chiccity.`in`.appWebView

import android.webkit.WebView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Stable holder so the WebView instance survives recompositions
 * and can be referenced by the BackHandler from the parent scaffold.
 */
class WebViewHolder {
    var webView: WebView? by mutableStateOf(null)
}
