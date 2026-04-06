package chiccity.`in`

import android.app.Application
import android.webkit.WebView
import chiccity.`in`.appWebView.WebViewManager

class ChicCityApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize WebViewManager and preload a WebView instance
        WebViewManager.initialize(this)
    }
}
