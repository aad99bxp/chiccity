package chiccity.`in`.appWebView

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView

/**
 * Singleton to manage a preloaded WebView instance for performance.
 */
object WebViewManager {
    private var preloadedWebView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    fun initialize(context: Context) {
        if (preloadedWebView == null) {
            preloadedWebView = WebView(context.applicationContext).apply {
                // Optimize settings
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    
                    // Caching strategy
                    cacheMode = WebSettings.LOAD_DEFAULT
                    
                    // Hardware Acceleration is enabled by default in Android 3.0+
                    // but we can ensure it's prioritized for the WebView's layer
                    setLayerType(View.LAYER_TYPE_HARDWARE, null)

                    // Performance optimizations
                    allowContentAccess = true
                    allowFileAccess = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    
                    // Improve image loading
                    loadsImagesAutomatically = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        offscreenPreRaster = true
                    }
                }

                // Cookie configuration
                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(this, true)

                // Layout params to match parent
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    }

    /**
     * Provides the preloaded WebView instance. 
     * IMPORTANT: It must be removed from its parent before being used in a new view hierarchy.
     */
    fun getOrCreateWebView(context: Context): WebView {
        if (preloadedWebView == null) {
            initialize(context)
        }
        val webView = preloadedWebView!!
        
        // Remove from previous parent if exists
        (webView.parent as? ViewGroup)?.removeView(webView)
        
        return webView
    }
    
    /**
     * Clear cache and cookies if needed (e.g., on logout)
     */
    fun clearData() {
        preloadedWebView?.apply {
            clearCache(true)
            clearHistory()
        }
        CookieManager.getInstance().removeAllCookies(null)
    }
}
